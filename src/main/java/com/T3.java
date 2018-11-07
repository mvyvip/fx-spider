package com;

import com.hs.reptilian.util.ProxyUtil2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class T3 {

    public static List<String> hosts = new ArrayList<>();
    public static ProxyUtil2 proxyUtil2 = new ProxyUtil2();

    public static Integer index = 0;

    static {
        hosts.add("113.107.238.133");
        hosts.add("122.228.238.92");
        hosts.add("117.21.219.73");
        hosts.add("106.42.25.225");
        hosts.add("113.207.76.18");
        hosts.add("122.228.238.92");
        hosts.add("113.107.238.193");
        hosts.add("113.107.238.193");
        hosts.add("106.42.25.225");
        hosts.add("106.42.25.225");
        hosts.add("106.42.25.225");
        hosts.add("113.107.238.133");
        hosts.add("113.107.238.193");
        hosts.add("113.107.238.206");
        hosts.add("106.42.25.225");
        hosts.add("117.21.219.111");
    }

    public static String getHosts() {
        if(index == hosts.size()) {
            index = 0;
        }
        return hosts.get(index++);
    }

    public static void main(String[] args) throws Exception {

//        for (int i = 0; i < 20; i++) {
//            System.out.println(getHosts());
//        }
//        Thread.sleep(10000000);

        proxyUtil2.initIps();
        for (int i = 0; i < 1; i++) {
            String randomIp = "60.31.89.2";
            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .timeout(100000)
//                .header("Host", "www.baidu.com")
                .header("Host", "http://info.bet007.com")
//                    .header("Host", getHosts())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/checkout-fastbuy.html")
                    .header("Connection", "keep-alive")
                    .header("Cookie", "__jsluid=54d5a8a8c765c62f3287be364a258866; _VMC_UID=b74619d868761593c39e79325ad9205e; _SID=e46b208b9496d73ef77d344cd06c6764; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541400392,1541402956,1541406051,1541472828; MEMBER_LEVEL_ID=1; c_dizhi=769478; c_peisong=1; c_zhifu=alipay; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1541559313; __jsl_clearance=1541559779.732|0|oBsx%2Fm%2B5CtGv5yFdfwcpZA%2Fyl08%3D; CACHE_VARY=f7fcece073fc929eaa679bd488609349-0f063e018c840f8a56946ecec41a6c18; UNAME=18585816873; MEMBER_IDENT=6460242; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541560198; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541560299")
//                .proxy(proxyUtil2.getProxy())
//                .cookie("__jsluid", "6d3709ce0c1d71f725f787f3a0bb50c4")
//                .cookie("__jsl_clearance", "1541516857.396|0|2FDklvlSK4I48FTE37IpVM7D5Ek%3D")
                    .header("Upgrade-Insecure-Requests", "1")
//                .proxy( new Proxy(Proxy.Type.HTTP, new InetSocketAddress("60.31.89.2", 22309)))
//                    .header("x-forward-for", randomIp)
//                    .header("X-Forwarded-For", randomIp)
//                    .header("client_ip", randomIp)
//                    .header("CLIENT_IP", randomIp)
//                    .header("REMOTE_ADDR", randomIp)
//                    .header("VIA", randomIp)
                    .execute();
            System.out.println(execute.cookies());
            System.out.println(execute.body());
        }
    }

    public static String getJs() {
       try {
           Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
               .timeout(100000)
                .header("Host", "mall.phicomm.com")
//               .header("Host", "http://info.bet007.com")
//                    .header("Host", getHosts())
               .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
               .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
               .header("Referer", "https://mall.phicomm.com/checkout-fastbuy.html")
               .header("Connection", "keep-alive")
               .header("Cookie", "__jsluid=54d5a8a8c765c62f3287be364a258866; _VMC_UID=b74619d868761593c39e79325ad9205e; _SID=e46b208b9496d73ef77d344cd06c6764; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541400392,1541402956,1541406051,1541472828; MEMBER_LEVEL_ID=1; c_dizhi=769478; c_peisong=1; c_zhifu=alipay; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1541559313; __jsl_clearance=1541559779.732|0|oBsx%2Fm%2B5CtGv5yFdfwcpZA%2Fyl08%3D; CACHE_VARY=f7fcece073fc929eaa679bd488609349-0f063e018c840f8a56946ecec41a6c18; UNAME=18585816873; MEMBER_IDENT=6460242; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541560198; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541560874")
//               .proxy(proxyUtil2.getProxy())
//                .cookie("__jsluid", "6d3709ce0c1d71f725f787f3a0bb50c4")
//                .cookie("__jsl_clearance", "1541516857.396|0|2FDklvlSK4I48FTE37IpVM7D5Ek%3D")
               .header("Upgrade-Insecure-Requests", "1")
//                .proxy( new Proxy(Proxy.Type.HTTP, new InetSocketAddress("60.31.89.2", 22309)))
//                    .header("x-forward-for", randomIp)
//                    .header("X-Forwarded-For", randomIp)
//                    .header("client_ip", randomIp)
//                    .header("CLIENT_IP", randomIp)
//                    .header("REMOTE_ADDR", randomIp)
//                    .header("VIA", randomIp)
               .execute();
           return "";
       } catch (Exception e) {
           return getJs();
       }
    }


    public static String getRandomIp() {
        // ip范围
        int[][] range = {{607649792, 608174079}, // 36.56.0.0-36.63.255.255
                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
        };

        Random rdint = new Random();
        int index = rdint.nextInt(10);
        String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    /*
     * 将十进制转换成IP地址
     */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        String x = "";
        b[0] = (int) ((ip >> 24) & 0xff);
        b[1] = (int) ((ip >> 16) & 0xff);
        b[2] = (int) ((ip >> 8) & 0xff);
        b[3] = (int) (ip & 0xff);
        x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);

        return x;
    }



}
