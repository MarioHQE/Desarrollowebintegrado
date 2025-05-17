package com.springboot.desarrolloweb.service.sucursal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;

@Service
public interface sucursalimplservice {

    public List<sucursal> getSucursales();

    public sucursal getSucursal(int idSucursal);

    public ResponseEntity<String> saveSucursal(sucursalrequest sucursal);

    public ResponseEntity<String> deleteSucursal(int idSucursal);

    public ResponseEntity<String> updateSucursal(int idSucursal, sucursalupdaterequest sucursal);
}
