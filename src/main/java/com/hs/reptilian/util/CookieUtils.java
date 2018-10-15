package com.hs.reptilian.util;

import com.hs.reptilian.model.OrderAccount;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class CookieUtils {

    private volatile ConcurrentMap<OrderAccount, Map<String, String>> concurrentMap = new ConcurrentHashMap<>();

    public synchronized void saveCookie(OrderAccount orderAccount, Map<String, String> cookies) {
        for (OrderAccount account : concurrentMap.keySet()) {
            if(account.getPhone().equals(orderAccount.getPhone())) {
                return;
            }
        }
        concurrentMap.put(orderAccount, cookies);
    }

    public ConcurrentMap<OrderAccount, Map<String, String>> getCookies() {
        return concurrentMap;
    }

}
