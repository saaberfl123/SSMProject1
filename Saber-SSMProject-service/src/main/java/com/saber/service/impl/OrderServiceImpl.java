package com.saber.service.impl;

import com.saber.dao.OrderDao;
import com.saber.pojo.Order;
import com.saber.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Primary
public class OrderServiceImpl implements OrderService
{
    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public List<Order> findAllById(int id) {
        return orderDao.findAllById(id);
    }

    @Override
    public List<Order> simplyFindAll() {
        return orderDao.simplyFindAll();
    }
}
