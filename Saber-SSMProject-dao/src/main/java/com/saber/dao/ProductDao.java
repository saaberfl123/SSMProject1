package com.saber.dao;

import com.saber.pojo.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProductDao
{
    @Select("select * from product")
    public List<Product> findAll();

    @Select("select * from product where id=#{id}")
    public Product findById(int id);

    @Insert("INSERT INTO product(productNum,productName,cityName,DepartureTime,productPrice,productStatus) VALUES(#{productNum},#{productName},#{cityName},#{departureTime},#{productPrice},#{productStatus})")
    public void save(Product product);




}
