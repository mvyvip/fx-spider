package com.hs.reptilian.util;

import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.model.TaskList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CookieUtils {

    public static volatile ConcurrentHashMap<OrderAccount, Map<String, String>> cookiesMap = new ConcurrentHashMap<>();

    public static volatile List<TaskList> taskLists = new ArrayList<>();

    public static volatile List<Map<String, String>> cache = new ArrayList<>();

    public static synchronized void addCookies(OrderAccount orderAccount, Map<String, String> cookies) {
        for (Map.Entry<OrderAccount, Map<String, String>> entry : cookiesMap.entrySet()) {
            if(entry.getKey().getPhone().equals(orderAccount.getPhone())) {
                return;
            }
        }
        cookiesMap.put(orderAccount, cookies);
    }

    public static synchronized ConcurrentHashMap<OrderAccount, Map<String, String>> getCookies() {
        return cookiesMap;
    }

    public static void addTask(String username, String password, String vcCodeUrl, String addrId, String vc, String cart_md5, Map<String, String> cookies) {
        if(!cache.contains(cookies)) {
            TaskList build = TaskList.builder()
                    .createDate(new Date())
                    .vc(vc)
                    .mobile(username)
                    .password(password)
                    .vc_code_url(vcCodeUrl)
                    .addr_id(addrId)
                    .cart_md5(cart_md5)
                    .cookies(cookies.toString().replaceAll(",", ";").replaceAll("\\{", "").replaceAll("}", ""))
                    .build();
            taskLists.add(build);
            cache.add(cookies);
        }
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("a", "aa");
        map.put("b", "bb");
        map.put("c", "cc");
        System.out.println(map.toString().replaceAll(",", ";").replaceAll("\\{", "").replaceAll("}", ""));
    }
}
