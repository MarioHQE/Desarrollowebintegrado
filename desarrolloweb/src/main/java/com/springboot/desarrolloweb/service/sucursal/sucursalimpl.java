package com.springboot.desarrolloweb.service.sucursal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.sucursalrepository;
import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;

import jakarta.transaction.Transactional;

public class sucursalimpl implements sucursalimplservice {
    @Autowired
    private sucursalrepository sucursalrepository;

    @Override
    public List<sucursal> getSucursales() {
        return sucursalrepository.findAll();
    }

    @Override
    public sucursal getSucursal(int idSucursal) {
        return sucursalrepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encuentra la sucursal"));

    }

    @Override
    public ResponseEntity<String> saveSucursal(sucursalrequest request) {
        String codigoGenerado = request.generarcodigo();

        // Verificar si ya existe una sucursal con ese código
        if (sucursalrepository.existsByCodigoPropio(codigoGenerado)) {
            return ResponseEntity.badRequest().body("Ya existe una sucursal con el mismo código");
        }

        sucursal sucursal = new sucursal();
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setCiudad(request.getCiudad());
        sucursal.setCodigoPropio(codigoGenerado);

        sucursalrepository.save(sucursal);
        return ResponseEntity.ok("Sucursal guardada correctamente");
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteSucursal(int idSucursal) {
        // Verificar si existe la sucursal
        sucursal sucursalExistente = sucursalrepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encontró la sucursal con ID: " + idSucursal));

        // Eliminar la sucursal
        sucursalrepository.delete(sucursalExistente);

        return ResponseEntity.ok("Sucursal eliminada correctamente");
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateSucursal(int idSucursal, sucursalupdaterequest request) {
        // Buscar sucursal existente
        sucursal sucursalExistente = sucursalrepository.findById(idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encontró la sucursal"));

        // Verificar si cambió el nombre o ciudad (para regenerar el código)
        boolean nombreCambiado = request.getNombre() != null
                && !request.getNombre().equals(sucursalExistente.getNombre());
        boolean ciudadCambiada = request.getCiudad() != null
                && !request.getCiudad().equals(sucursalExistente.getCiudad());

        if (nombreCambiado) {
            sucursalExistente.setNombre(request.getNombre());
        }

        if (ciudadCambiada) {
            sucursalExistente.setCiudad(request.getCiudad());
        }

        if (nombreCambiado || ciudadCambiada) {
            String nuevoCodigo = sucursalExistente.getNombre().substring(0, 3).toUpperCase() + "_" +
                    sucursalExistente.getCiudad().substring(0, 3).toUpperCase();

            // Verificar que el nuevo código no lo tenga otra sucursal
            boolean codigoDuplicado = sucursalrepository.existsByCodigoPropio(nuevoCodigo)
                    && !nuevoCodigo.equals(sucursalExistente.getCodigoPropio()); // Evita conflicto consigo misma

            if (codigoDuplicado) {
                throw new RuntimeException("Ya existe otra sucursal con el mismo código");

            }

            sucursalExistente.setCodigoPropio(nuevoCodigo);
        }

        // Actualizar los demás campos si están presentes
        if (request.getDireccion() != null) {
            sucursalExistente.setDireccion(request.getDireccion());
        }

        if (request.getLat() != 0) {
            sucursalExistente.setLat(request.getLat());
        }

        if (request.getLon() != 0) {
            sucursalExistente.setLon(request.getLon());
        }

        // Guardar los cambios
        sucursalrepository.save(sucursalExistente);

        return ResponseEntity.ok("Sucursal actualizada correctamente");
    }

}
