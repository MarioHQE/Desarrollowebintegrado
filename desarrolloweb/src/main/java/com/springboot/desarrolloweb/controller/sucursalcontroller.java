package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.sucursal;
import com.springboot.desarrolloweb.request.sucursal.sucursalrequest;
import com.springboot.desarrolloweb.request.sucursal.sucursalupdaterequest;
import com.springboot.desarrolloweb.service.sucursal.sucursalimplservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/sucursal")
@Slf4j
public class sucursalcontroller {
    @Autowired
    sucursalimplservice sucursalimplservice;

    @GetMapping("/all")
    public List<sucursal> obtenerSucursales(@RequestParam String param) {
        return sucursalimplservice.getSucursales();
    }

    @GetMapping("/usercity/{idubicacion_usuario}")
    public List<sucursal> obtenerSucursalesByCiudadofUserCity(@PathVariable int idubicacion_usuario,
            HttpServletRequest requestheaderMap) {
        log.info("headertoken" + requestheaderMap.getHeader("Authorization"));
        return sucursalimplservice.getSucursalesByCiudadofUserCity(idubicacion_usuario, requestheaderMap);
    }

    @GetMapping("/isuserhasubicacion")
    public boolean isUserHasUbicacion(@RequestParam String email) {
        return sucursalimplservice.isUserHasUbicacion(email);
    }

    @GetMapping("/{idSucursal}")
    public sucursal obtenerSucursal(@PathVariable("idSucursal") int param) {
        return sucursalimplservice.getSucursal(param);
    }

    @PostMapping("/save")
    public ResponseEntity<String> guardarsucursal(@RequestBody sucursalrequest request) {

        return sucursalimplservice.saveSucursal(request);
    }

    @PutMapping("/update/{idSucursal}")
    public ResponseEntity<String> updatesucursal(@PathVariable("idSucursal") int idsucursal,
            @RequestBody sucursalupdaterequest request) {

        return sucursalimplservice.updateSucursal(idsucursal, request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletesucursal(@RequestParam int idSucursal) {
        return sucursalimplservice.deleteSucursal(idSucursal);
    }

}
