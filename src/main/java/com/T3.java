package com;

import com.hs.reptilian.util.ProxyUtil2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class T3 {

    public static void main(String[] args) throws Exception {
        ProxyUtil2 proxyUtil2 = new ProxyUtil2();
        proxyUtil2.initIps();
        Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                .timeout(100000)
                .header("Host", "mall.phicomm.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Referer", "https://mall.phicomm.com/passport-login.html")
                .header("Connection", "keep-alive")
                .cookie("__jsluid", "6d3709ce0c1d71f725f787f3a0bb50c4")
                .cookie("__jsl_clearance", "1541516857.396|0|2FDklvlSK4I48FTE37IpVM7D5Ek%3D")
                .header("Upgrade-Insecure-Requests", "1")
//                .proxy(proxyUtil2.getProxy())
                .execute();
        System.out.println(execute.cookies());
        System.out.println(execute.body());
    }

}
