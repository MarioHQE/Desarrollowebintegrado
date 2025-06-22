package com.springboot.desarrolloweb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.ProductoSucursal;

@Repository
public interface productosucursalrepository extends JpaRepository<ProductoSucursal, Integer> {
    List<ProductoSucursal> findProductosBySucursal(@Param("idSucursal") int idSucursal);

    ProductoSucursal findbyproductoysucursal(@Param("idProducto") int idProducto,
            @Param("idSucursal") int idSucursal);

}
