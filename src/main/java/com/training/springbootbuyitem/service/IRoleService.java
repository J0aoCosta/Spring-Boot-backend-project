package com.training.springbootbuyitem.service;

import com.training.springbootbuyitem.entity.model.Role;

public interface IRoleService{
    Role findByName(String name);
}
