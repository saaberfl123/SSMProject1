package com.saber.service;

import com.saber.pojo.Order;

import java.util.List;

public interface OrderService
{
    public List<Order> findAllById(int id);

    public List<Order> simplyFindAll();
}
