package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;
import com.springboot.desarrolloweb.service.sucursal.sucursalimplservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/sucursal")
public class sucursalcontroller {
    @Autowired
    sucursalimplservice sucursalimplservice;

    @GetMapping("/all")
    public List<sucursal> obtenerSucursales(@RequestParam String param) {
        return sucursalimplservice.getSucursales();
    }

    @GetMapping("/{idSucursal}")
    public sucursal obtenerSucursal(@PathVariable("idSucursal") int param) {
        return sucursalimplservice.getSucursal(param);
    }

    @PostMapping("/save")
    public ResponseEntity<String> guardarsucursal(@RequestBody sucursalrequest request) {

        return sucursalimplservice.saveSucursal(request);
    }

    @PutMapping("/update/{idsucursal}")
    public ResponseEntity<String> updatesucursal(@PathVariable("idSucursal") int idsucursal,
            @RequestBody sucursalupdaterequest request) {

        return sucursalimplservice.updateSucursal(idsucursal, request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletesucursal(@RequestParam int idSucursal) {
        return sucursalimplservice.deleteSucursal(idSucursal);
    }

}
