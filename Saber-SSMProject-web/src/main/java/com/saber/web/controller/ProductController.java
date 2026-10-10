package com.saber.web.controller;

import com.saber.pojo.Message;
import com.saber.pojo.Product;
import com.saber.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController
{
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Message> findAll()
    {
        return ResponseEntity.ok().body(new Message(200,"查询成功",productService.findAll()));
    }

    @PostMapping("/save")
    public ResponseEntity<Message> save(Product product)
    {
        productService.save(product);
        return ResponseEntity.ok().body(new Message(200,"保存成功",null));
    }
}
