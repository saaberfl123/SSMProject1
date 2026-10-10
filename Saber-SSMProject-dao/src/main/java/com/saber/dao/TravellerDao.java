package com.saber.dao;

import com.saber.pojo.Member;
import com.saber.pojo.Traveller;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface TravellerDao
{
    @Select("select * from traveller")
    public List<Traveller> findAll();


    @Select("select * from traveller where id in(select travellerID from order_traveller where orderID=#{id})")
    public List<Traveller> findTravellersByOrderId(int orderID);

}
