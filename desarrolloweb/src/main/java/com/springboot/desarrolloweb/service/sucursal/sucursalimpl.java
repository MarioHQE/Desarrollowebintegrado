package com.springboot.desarrolloweb.service.sucursal;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.springboot.desarrolloweb.dao.sucursalrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;
import com.springboot.desarrolloweb.security.jwutil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class sucursalimpl implements sucursalimplservice {
    @Autowired
    private sucursalrepository sucursalrepository;
    @Autowired
    private jwutil jwutil;
    @Autowired
    usuariorepository usuariorepository;

    @Override
    public List<sucursal> getSucursales(Map<String, Object> requestheaderMap) {
        String headertoken = (String) requestheaderMap.get("Authorization");
        String token = ((String) requestheaderMap.get("Authorization")).substring(7);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getPrincipal().toString());
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            return sucursalrepository.findAll();

        }
        if (headertoken != null && headertoken.startsWith("Bearer ")) {
            String email = jwutil.getUser(token);
            if (email != null && !email.isEmpty()) {
                List<sucursal> sucursales = sucursalrepository.findAll();
                usuario usuario = usuariorepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                sucursales.forEach(sucursal -> {
                    usuario.getUbicacion_usuario().forEach(ubicacion_usuario -> {
                        calculatedistancetokm(ubicacion_usuario.getUbicacion().getLatitud(),
                                ubicacion_usuario.getUbicacion().getLongitud(), sucursal.getLat(), sucursal.getLon());

                    });

                });
            }

        }
        return sucursalrepository.findAll();
    }

    /************* ✨ Windsurf Command ⭐ *************/
    /**
     * Calcula la distancia entre dos puntos geograficos dados en latitud y
     * longitud, en kilómetros.
     * 
     * @param lat1 latitud del punto 1
     * @param lon1 longitud del punto 1
     * @param lat2 latitud del punto 2
     * @param lon2 longitud del punto 2
     * @return la distancia en kilómetros
     */
    /******* d32e8031-f6e2-47bc-ae26-90cb38a3280d *******/
    public double calculatedistancetokm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radio de la Tierra en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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
        sucursal.setLat(request.getLat());
        sucursal.setLon(request.getLon());
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
