package com.saber.dao;

import com.saber.pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderDao
{
    @Results(id="order",value = {
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "orderNum",property = "orderNum"),
            @Result(column = "orderTime",property = "orderTime"),
            @Result(column = "orderStatus",property = "orderStatus"),
            @Result(column = "peopleCount",property = "peopleCount"),
            @Result(column = "payType",property = "payType"),
            @Result(column = "orderDesc",property = "orderDesc"),
            @Result(column = "productID",property = "product",one=@One(select = "com.saber.dao.ProductDao.findById")),
            @Result(column = "memberID",property = "member",one=@One(select = "com.saber.dao.MemberDao.findById")),
            @Result(column = "id",property = "travellers",many=@Many(select = "com.saber.dao.TravellerDao.findTravellersByOrderId")),
    })
    @Select("select * from `order` where id=#{id}")
    public List<Order> findAllById(int id);

    @Select("select * from `order`")
    public List<Order> simplyFindAll();
}
