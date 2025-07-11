package com.springboot.desarrolloweb.service.sucursal;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface sucursalimplservice {

    public List<sucursal> getSucursales();

    public List<sucursal> getSucursalesByCiudadofUserCity(int idubicacion_usuario,
            HttpServletRequest requestheaderMap);

    public sucursal getSucursal(int idSucursal);

    public ResponseEntity<String> saveSucursal(sucursalrequest sucursal);

    public ResponseEntity<String> deleteSucursal(int idSucursal);

    public ResponseEntity<String> updateSucursal(int idSucursal, sucursalupdaterequest sucursal);
}
