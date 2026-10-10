package com.saber.web.controller;

import com.saber.pojo.Message;
import com.saber.pojo.Order;
import com.saber.pojo.Role;
import com.saber.service.OrderService;
import com.saber.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController
{
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Message> findAll()
    {
        return ResponseEntity.ok().body(new Message(200,"查找成功",roleService.findAll()));
    }

    @GetMapping("/simplyFindAll")
    public ResponseEntity<Message> simplyFindAll()
    {
        return ResponseEntity.ok().body(new Message(200,"查找成功",roleService.simplyFindAll()));
    }

}
