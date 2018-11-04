package com.hs.reptilian;

import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.ProxyUtil2;
import com.hs.reptilian.util.UserAgentUtil;
import java.util.Random;
import java.util.UUID;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by lt on 2018/11/2 0002.
 */
@SuppressWarnings("ALL")
public class FilterTest2 {

    public static void main(String[] args) throws Exception{
//        ProxyUtil2 proxyUtil2 = new ProxyUtil2();
//        proxyUtil2.initIps();
//        proxyUtil2.getProxy();
        int a = 1;
//        while (1== a) {
            for (int i = 13000; i < 65535; i++) {
                Thread.sleep(30);
                int finalI = i;
                new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           // 106.75.223.239
                           Connection.Response connect = Jsoup.connect("http://106.75.217.70:" + finalI).timeout(4000).execute();
                           System.out.println(finalI + "----" + connect.parse().text());
                           if(connect.parse().text().toString().contains("斐讯")) {
                               System.exit(-1);
                           }
                       } catch (Exception e) {
                           System.out.println(finalI + "--" + e.getMessage());
                       }
                   }
               }).start();
            }

//        }

        while (a==1) {
            byte[] bytes = Jsoup.connect("http://106.75.217.70/vcode-index-passport6272948.html?d=0.010903156658214508")
                    .ignoreContentType(true)
                    .cookie("_SID", "DC9662635751408299A730DD532E8066")
                    .header("X-DevTools-Emulate-Network-Conditions-Client-Id", UUID.randomUUID().toString().toUpperCase())
                    .header("Upgrade-Insecure-Requests", "1")
//                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
//                    .proxy(proxyUtil.getProxy())
                    .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes();
            System.out.println(bytes);
        }


       while (true) {
           try {
               String randomIp = getRandomIp();
               System.out.println(randomIp);
               System.out.println(randomIp.concat(":443"));
               Document document = Jsoup.connect("http://mall.phicomm.com/index.php/my-orders.html").method(Method.GET)
                   .userAgent(UserAgentUtil.get())
                   .timeout(SystemConstant.TIME_OUT)
                       .header("Cookie", "_VMC_UID=2f5c392178fd38cf2b8da4bff3ecd32f; __jsluid=df842f7b477decdd19bfe5e4a75b70bb; _SID=16a69f4953d488f2df8af7a84dda41aa; goods_view_history=384G396G405G; from_vshopid=46225944878; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541007014,1541075135,1541103596,1541206933; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1540919048,1541088661,1541103608,1541214250; Hm_lvt_4532b50bd635e230f63e966a610afe18=1541224765; Hm_lpvt_4532b50bd635e230f63e966a610afe18=1541224765; c_dizhi=null; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541225941; UNAME=13648045607; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; CACHE_VARY=cc7c517108cedf81bf34cbb89f6539ab-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541226745")
//                   .cookie("_SID", "DC9662635751408299A730DD532E8066")
                   .header("x-forward-for", randomIp)
                   .header("X-Forward-For", randomIp)
                   .header("X-Forwarded-For", randomIp)
                   .header("HTTP_X_FORWARD_FOR", randomIp)
                   .header("client_ip", randomIp)
                   .header("CLIENT_IP", randomIp)
                   .header("Proxy-Client-IP", randomIp)
                   .header("Client-Ip", randomIp)
                   .header("WL-Proxy-Client-IP", randomIp)
                   .header("REMOTE_ADDR", randomIp)
                   .header("VIA", randomIp)
                   .header("HTTP_CLIENT_IP", randomIp)
                   .header("HTTP_X_FORWARDED_FOR", randomIp)
                   .header("Remote Address", randomIp.concat(":443"))
                   .header("remoteip", randomIp)
                   .followRedirects(true).execute().parse();
               System.out.println(document.body().text());
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
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
