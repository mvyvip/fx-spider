package com.hs.reptilian.util;

/**
 * Created by lt on 2018/10/19 0019.
 */
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
