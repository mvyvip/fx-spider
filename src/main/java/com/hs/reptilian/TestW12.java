package com.hs.reptilian;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.ProxyUtil2;
import com.hs.reptilian.util.RuoKuaiUtils;

import java.util.*;

import com.hs.reptilian.util.UserAgentUtil;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("all")
public class TestW12 {

    private static volatile String rsbody = null;

    private static ProxyUtil2 proxyUtil = new ProxyUtil2();

    private static Map<String, String> getCookies2(String username, String password) throws Exception {
        try {
            String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/openapi/hybirdappsetting/ios")
                    .userAgent("VEC_customer/2.2.4 (iPhone; iOS 11.3.1; Scale/2.00)")
                    .header("X-WxappStorage-SID", id)
                    .cookie("_SID", id)
                    .header("Accept-Language", "zh-Hans-CN;q=1")
                    .header("Accept-Encoding", "br, gzip, deflate")
                    .header("Connection", "keep-alive")
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .execute();

            Connection.Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(org.jsoup.Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                    .proxy(proxyUtil.getProxy())
                    .header("X-WxappStorage-SID", id)
                    .timeout(SystemConstant.TIME_OUT).execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/m/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                    .cookies(pageResponse.cookies())
                    .timeout(SystemConstant.TIME_OUT)
                    .cookie("_SID", id)

                    .ignoreContentType(true)
                    .proxy(proxyUtil.getProxy())
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
                    .header("Set-jsonstorage", "jsonstorage")
                    .header("Origin", "https://mall.phicomm.com")
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", username)
                    .data("password", password)
                    .execute();

            if(loginResponse.body().contains("error")) {
                throw new RuntimeException(JSON.parseObject(loginResponse.body()).toString());
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageResponse.cookies());
            cks.putAll(loginResponse.cookies());
            return cks;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return getCookies2(username, password);
        }
    }

    public static void main(String[] args) throws Exception {
        proxyUtil.initIps();
        Map<String, String> cookies = getCookies2("15756343442", "li5201314");
/*        Map<String, String> cookies = new HashMap<>();
        String[] split = "_VMC_UID=2f5c392178fd38cf2b8da4bff3ecd32f; __jsluid=df842f7b477decdd19bfe5e4a75b70bb; _SID=16a69f4953d488f2df8af7a84dda41aa; Hm_lvt_4532b50bd635e230f63e966a610afe18=1537703257,1538135330; goods_view_history=384G396G405G; from_vshopid=46225944878; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1540905087,1541007014,1541075135,1541103596; c_dizhi=null; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1540710637,1540919048,1541088661,1541103608; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541103645; UNAME=15756343442; MEMBER_IDENT=6340322; MEMBER_LEVEL_ID=1; CACHE_VARY=96e1c0d14d583af9f846b4750a7b70ec-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541110929".toString().replace("{", "").replace("}", "").split(";");
        for (String s : split) {
            cookies.put(s.split("=")[0], s.split("=")[1]);
        }*/
        System.out.println(cookies);

        int a = 1;
        while (a == 1) {
            try {
                String body = Jsoup.connect(SystemConstant.W3_URL).method(Connection.Method.GET)
                        .userAgent(UserAgentUtil.get())
                        .proxy(proxyUtil.getProxy())
                        .timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
                if (body.contains("库存不足,当前最多可售数量")) {
                    System.out.println("库存不足 - " + new Date().toLocaleString());
                } else if (body.contains("返回商品详情") || body.contains("cart_md5")) {
                    updateRsBody(body);
                }
                System.out.println(body);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            Thread.sleep(1000);
        }


	/*	String body = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
			.timeout(10000)
			.cookies(cookies)
			.execute().body();
		System.out.println(body);

		System.out.println();
*/
//				initBody(cookies);

        while (rsbody == null) {
//        	System.out.println("等待初始化中");
        }

        Document document = Jsoup.parse(rsbody);
        String cart_md5 = getCartMd5(document);
        String addr_id = getAddrId(document);
        String vcCodeUrl = "https://mall.phicomm.com" + document.getElementById("local-vcode-img").attr("onclick").split("'")[1].split("\\?")[0];
        System.out.println("cart_md5: " + cart_md5 + " addr_id: " + addr_id + " vcCodeUrl: " + vcCodeUrl);

        while(true) {
            try {
                String vcCodeJson = RuoKuaiUtils.createByPost("2980364030", "li5201314", "4030", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e",
                        Jsoup.connect(vcCodeUrl)
                                .ignoreContentType(true)
                                .cookies(cookies)
                                .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

                System.out.println(vcCodeJson);

                Thread[] threads = new Thread[SystemConstant.TASK_COUNT];
                for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
                    threads[i] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Method.POST).timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
                                        .cookies(cookies)
                                        .header("X-Requested-With", "XMLHttpRequest")
                                        .data("cart_md5", cart_md5)
                                        .data("addr_id", addr_id)
                                        .data("dlytype_id", "1")
                                        .data("payapp_id", "alipay")
                                        .data("yougouma", "")
                                        .data("invoice_type", "")
                                        .data("invoice_title", "")
                                        .data("useVcNum", SystemConstant.W3_VC)
                                        .data("need_invoice2", "on")
                                        .data("useDdwNum", "0")
                                        .data("memo", "")
                                        .data("vcode", JSONObject.parseObject(vcCodeJson).getString("Result"))
                                        .execute();

                                System.err.println("==========================================================");
                                System.err.println(createOrderResponse.body());
                                if(createOrderResponse.body().contains("success")) {
                                    System.err.println("success!!!!");
                                    System.exit(-1);
                                }
                            } catch (Exception e) {}
                        }
                    });
                    threads[i].start();
                }
                for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
                    threads[i].join();
                }
            } catch (Exception e) {
                System.err.println("err>> " + e.getMessage());
            }
        }

    }

    private static synchronized void updateRsBody(String body) {
        if(rsbody == null) {
            rsbody = body;
            System.out.println("==============设置成功==============");
        }
    }

    private static String initBody(Map<String, String> cookies) throws Exception {
        while(rsbody == null) {
            Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String body = Jsoup.connect(SystemConstant.W3_URL).method(Method.GET)
                                .timeout(SystemConstant.TIME_OUT)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6776.400 QQBrowser/10.3.2577.400")
                                .cookies(cookies).followRedirects(true).execute().body();
                        if(body.contains("库存不足,当前最多可售数量")) {
                            System.err.println("库存不足 - " + new Date().toLocaleString());
                        } else if(body.contains("返回商品详情") || body.contains("cart_md5")) {
                            updateRsBody(body);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        //						e.printStackTrace();
                    }
                }
            }).start();
        }
        System.out.println("成功");
        return rsbody;
    }

    private static String getAddrId(Document document) {
        try {
            String addr_id = document.select("input[name=addr_id]").get(0).val();
            if(addr_id != null && addr_id.length() > 0) {
//                System.err.println("addr_id获取成功："  + addr_id);
                return addr_id;
            }
        } catch (Exception e) {
            for (Element element : document.getElementsByTag("input")) {
                if(element.attr("name").equals("addr_id")) {
                    String addr_id = element.attr("value");
//                    System.err.println("addr_id获取成功："  + addr_id);
                    return addr_id;
                }
            }
        }
        throw new RuntimeException("addr_id获取失败");
    }

    private static String getCartMd5(Document document) {
        for (Element element : document.getElementsByTag("input")) {
            if(element.attr("name").equals("cart_md5")) {
                System.err.println("cart_md5获取成功："  + element.attr("value"));
                return element.attr("value");
            }
        }
        throw new RuntimeException("cart_md5获取失败");
    }

    private static boolean doCheck(String body) {
        if(body.contains("购物车为空")) {
            System.out.println("购物车为空 - " + new Date().toLocaleString());
            return true;
        }
        if(body.indexOf("库存不足") != -1) {
            System.out.println("库存不足 - " + new Date().toLocaleString());
            return true;
        }
        return false;
    }

    private static Map<String, String> getCookies(String username, String password) throws Exception {
        try{
            Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Method.GET)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Host", "mall.phicomm.com")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .ignoreContentType(true)
                    .timeout(SystemConstant.TIME_OUT).execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Method.POST)
                    .cookies(pageCookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Host", "mall.phicomm.com")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", username)
                    .data("password", password)
                    .execute();


            if(loginResponse.body().contains("error")) {
                return getCookies(username, password);
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            return cks;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return getCookies(username, password);
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
