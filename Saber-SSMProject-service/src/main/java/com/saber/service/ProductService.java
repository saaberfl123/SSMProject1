package com.saber.service;


import com.saber.dao.ProductDao;
import com.saber.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService
{
    public List<Product> findAll();

    public void save(Product product);
}
