package com.ecommerce.library.service;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;

public interface AdminService {
    public Admin findByUsername(String username);

    public Admin save(AdminDto adminDto);
}
