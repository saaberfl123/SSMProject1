package com.saber.dao;

import com.saber.pojo.Member;
import com.saber.pojo.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface MemberDao
{
    @Select("select * from member")
    public List<Member> findAll();

    @Select("select * from member where id=#{id}")
    public Member findById(int id);






}
