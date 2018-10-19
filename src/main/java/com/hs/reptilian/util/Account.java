package com.hs.reptilian.util;

import lombok.Data;

/**
 * Created by lt on 2018/10/19 0019.
 */
@Data
public class Account {
    public Account(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    private String phone;

    private String password;

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }


}
