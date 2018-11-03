package com.hs.reptilian;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class MyProxyTest {

    public static void main(String[] args) throws Exception {

        Connection.Response execute = Jsoup.connect("http://test.abuyun.com/proxy.php")
                .timeout(3000)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("118.24.153.209", 9999)))
                .execute();
        System.out.println(execute.body());

    }

}
