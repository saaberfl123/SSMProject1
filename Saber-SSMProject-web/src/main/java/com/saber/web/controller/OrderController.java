package com.saber.web.controller;

import com.saber.pojo.Message;
import com.saber.pojo.Order;
import com.saber.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController
{
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/findAllById")
    public ResponseEntity<Message> findAll(int id)
    {
        return ResponseEntity.ok().body(new Message(200,"查询成功",orderService.findAllById(id)));
    }

    @GetMapping("/simplyFindAll")
    public ResponseEntity<Message> simplyFindAll()
    {
        return ResponseEntity.ok().body(new Message(200,"查询成功",orderService.simplyFindAll()));
    }

}
