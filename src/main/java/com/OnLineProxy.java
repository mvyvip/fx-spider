package com;

import com.alibaba.fastjson.JSON;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.ProxyUtil2;
import com.hs.reptilian.util.UserAgentUtil;
import com.hs.reptilian.util.feifei.FeiFeiUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * Created by lt on 2018/11/6 0006.
 */
@Slf4j
@SuppressWarnings("all")
public class OnLineProxy {

    public static List<String> hosts = new ArrayList<>();

    public static Integer index = 0;
    public static ProxyUtil2 proxyUtil2 = new ProxyUtil2();


    static {


        hosts.add("1.31.128.217");
        hosts.add("112.90.216.104");
        hosts.add("1.31.128.203");
        hosts.add("1.31.128.244");
        hosts.add("111.202.98.6");
        hosts.add("113.207.76.18");

        hosts.add("117.21.219.73");
        hosts.add("113.107.238.133");
        hosts.add("122.228.238.92");
        hosts.add("106.42.25.225");
        hosts.add("1.31.128.202");
        hosts.add("117.21.219.111");
        hosts.add("113.107.238.193");
        hosts.add("117.21.219.76");
        hosts.add("113.107.238.206");
        hosts.add("106.42.25.225");


    }

    public static String getHosts() {
        if(index == hosts.size()) {
            index = 0;
        }
        return hosts.get(index++);
    }


    private static volatile String rsbody;

    private static volatile String cart_md5;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static Map<String, String> getCookies() {
        try {
            Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .proxy(proxyUtil2.getProxy())
                    .header("Upgrade-Insecure-Requests", "1")
                    .execute();

            Map<String, String> cookies = response.cookies();
            String body = response.body();
            cookies.put("__jsl_clearance", getck(body).split("=")[1]);
            toLoginPage(cookies);
            return cookies;
        } catch (Exception e) {
            log.error("获取 __jsl_clearance 失败，{}", e.getMessage());
            return getCookies();
        }
    }

    public static void toLoginPage(Map<String, String> cookies) {
       try {
           Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                   .timeout(100000)
                   .header("Host", "mall.phicomm.com")
                   .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                   .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                   .header("Referer", "https://mall.phicomm.com/passport-login.html")
                   .header("Connection", "keep-alive")
                   .header("Upgrade-Insecure-Requests", "1")
                   .cookies(cookies)
               .proxy(proxyUtil2.getProxy())
                   .execute();
           if(execute.statusCode() != 200) {
               log.error("登录界面ck返回异常， code: [{}]", execute.statusCode());
               toLoginPage(cookies);
           } else {
               cookies.putAll(execute.cookies());
               doLogin(cookies);
           }
       } catch (Exception e) {
           log.error("获取 登录界面 ck 失败，{}", e.getMessage());
       }
    }

    public static void doLogin(Map<String, String> cookies) {
       try {
           Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                   .method(Connection.Method.POST)
                   .cookies(cookies)
                   .timeout(SystemConstant.TIME_OUT)
                   .ignoreContentType(true)
               .proxy(proxyUtil2.getProxy())
                   .header("Host", "mall.phicomm.com")
                   .header("Connection", "keep-alive")
                   .header("Accept", "application/json, text/javascript, */*; q=0.01")
                   .header("Origin", "https://mall.phicomm.com")
                   .header("X-Requested-With", "XMLHttpRequest")
                   .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                   .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                   .header("Referer", "https://mall.phicomm.com/passport-login.html")
                   .header("Accept-Encoding", "gzip, deflate, br")
                   .header("Accept-Language", "zh-CN,zh;q=0.9")
                   .data("forward", "")
               .data("uname", "18585816873")
               .data("password", "qq666888")
                   .execute();
           System.out.println(JSON.parseObject(loginResponse.body()));
           cookies.putAll(loginResponse.cookies());
       } catch (Exception e) {
           log.error("登录失败，{}", e.getMessage());
       }
    }

    public synchronized static void updateRsBody(String body) {
        if (rsbody == null) {
            rsbody = body;
            cart_md5 = getCartMd5(body);
            countDownLatch.countDown();
//            initCodeFlag.set(false);
        }
    }

    public static String initBody(Map<String, String> cookies) {
        while (rsbody == null) {
            try {
//                Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
                try {
                    Response response = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-13-1.html")
                        .method(Connection.Method.GET)
                        .proxy(proxyUtil2.getProxy())
                        .timeout(SystemConstant.TIME_OUT)
//                        .header("Host", "mall.phicomm.com")
                        .header("Host", getHosts())
                        .ignoreContentType(true)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
                        .cookies(cookies)
                        .followRedirects(true).execute();
                    if(response.statusCode() == 200) {
                        if(response.body().contains("库存不足,当前最多可售数量")) {
                            System.out.println("库存不足 - " + new Date().toLocaleString());
                        } else if(response.body().contains("返回商品详情") || response.body().contains("cart_md5")) {
                            updateRsBody(response.body());
                        }
                        Thread.sleep(6000);
                    } else {
                        if(response.statusCode() == 403) {
                            System.out.println("换代理");
                            Thread.sleep(10 * 1000);
                        } else if(response.statusCode() == 521) {
                            String __jsl_clearance = getck(response.body());
                            cookies.put("__jsl_clearance", __jsl_clearance.split("=")[1]);
                            System.out.println("重新换__jsl_clearance中");
                            initBody(cookies);
                        }
                    }
                } catch (Exception e) {
                    log.error("初始化>>>body失败--" + e.getMessage());
                }
             /*   new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-14-1.html")
                                .method(Connection.Method.GET)
                                .timeout(SystemConstant.TIME_OUT)
                                .header("Host", "mall.phicomm.com")
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
                                .cookies(cookies)
                                .followRedirects(true).execute();
                            if(response.statusCode() == 200) {
                                if(response.body().contains("库存不足,当前最多可售数量")) {
                                    System.out.println("库存不足 - " + new Date().toLocaleString());
                                } else if(response.body().contains("返回商品详情") || response.body().contains("cart_md5")) {
                                    updateRsBody(response.body());
                                }
                            } else {
                                if(response.statusCode() == 403) {
                                    System.out.println("换代理");
                                } else if(response.statusCode() == 521) {
                                    String __jsl_clearance = getck(response.body());
                                    cookies.put("__jsl_clearance", __jsl_clearance.split("=")[1]);
                                    System.out.println("重新换__jsl_clearance中");
                                    initBody(cookies);
                                }
                            }
                        } catch (Exception e) {
                            log.error("初始化>>>body失败--" + e.getMessage());
                        }
                    }
                }).start();*/
            } catch (Exception e) {
                log.error("初始化body失败--" + e.getMessage());
            }
        }
        return rsbody;
    }

    public static String getBodyCk(Map<String, String> cookies) throws Exception {
        try {
            Response response = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-13-1.html")
                    .method(Connection.Method.GET)
                    .timeout(SystemConstant.TIME_OUT)
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
                    .cookies(cookies)
                .proxy(proxyUtil2.getProxy())
                    .followRedirects(true).execute();
            if(response.statusCode() != 200) {
                try {
                    String __jsl_clearance = getck(response.body());
                    cookies.put("__jsl_clearance", __jsl_clearance.split("=")[1]);
                    Response response2 = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-197-1.html")
                            .method(Connection.Method.GET)
                            .timeout(SystemConstant.TIME_OUT)
                            .header("Host", "mall.phicomm.com")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
//                .header("Cookie", "__jsluid=bee72d940b5fe9e2c9fab4dc084d6328; __jsl_clearance=1541512178.023|0|9C7P1IMlE%2F6FsLaiVSm0hGWAUBM%3D; _VMC_UID=5cf0eb68f3cd35339b9c8b11f8eacbf0; _SID=f170f457eea3e2c664d0880b0d05ae1d; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541512172; UNAME=18585816873; MEMBER_IDENT=6460242; MEMBER_LEVEL_ID=1; c_dizhi=769474; c_peisong=1; c_zhifu=alipay; CACHE_VARY=45cf279e0ac802340e208e1ef8d49b13-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541512280")
                            .cookies(cookies)
                        .proxy(proxyUtil2.getProxy())
                            .followRedirects(true).execute();
                    if (response2.body().contains("库存不足,当前最多可售数量")) {
                        System.out.println("库存不足 - " + new Date().toLocaleString());
                        return getBodyCk(cookies);
                    } else if (response2.body().contains("返回商品详情") || response2.body().contains("cart_md5")) {
                        return response2.body();
                    }
                } catch (Exception e) {
                    log.error("获取 __jsl_clearance 失败 重试中");
                    return getBodyCk(cookies);
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("获取 __jsl_clearance 失败，{}", e.getMessage());
            return getBodyCk(cookies);
        }
        return null;
    }


//    public static String getBodyCk(Map<String, String> cookies) throws Exception {
//        try {
//            Response response = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-197-1.html")
//                    .method(Connection.Method.GET)
//                    .timeout(SystemConstant.TIME_OUT)
//                    .header("Host", "mall.phicomm.com")
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
////                .header("Cookie", "__jsluid=bee72d940b5fe9e2c9fab4dc084d6328; __jsl_clearance=1541512178.023|0|9C7P1IMlE%2F6FsLaiVSm0hGWAUBM%3D; _VMC_UID=5cf0eb68f3cd35339b9c8b11f8eacbf0; _SID=f170f457eea3e2c664d0880b0d05ae1d; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541512172; UNAME=18585816873; MEMBER_IDENT=6460242; MEMBER_LEVEL_ID=1; c_dizhi=769474; c_peisong=1; c_zhifu=alipay; CACHE_VARY=45cf279e0ac802340e208e1ef8d49b13-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541512280")
//                    .cookies(cookies)
//                    .followRedirects(true).execute();
//            if(response.statusCode() != 200) {
//                try {
//                    String __jsl_clearance = getck(response.body());
//                    cookies.put("__jsl_clearance", __jsl_clearance.split("=")[1]);
//                    Response response2 = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-197-1.html")
//                            .method(Connection.Method.GET)
//                            .timeout(SystemConstant.TIME_OUT)
//                            .header("Host", "mall.phicomm.com")
//                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
////                .header("Cookie", "__jsluid=bee72d940b5fe9e2c9fab4dc084d6328; __jsl_clearance=1541512178.023|0|9C7P1IMlE%2F6FsLaiVSm0hGWAUBM%3D; _VMC_UID=5cf0eb68f3cd35339b9c8b11f8eacbf0; _SID=f170f457eea3e2c664d0880b0d05ae1d; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541512172; UNAME=18585816873; MEMBER_IDENT=6460242; MEMBER_LEVEL_ID=1; c_dizhi=769474; c_peisong=1; c_zhifu=alipay; CACHE_VARY=45cf279e0ac802340e208e1ef8d49b13-0f063e018c840f8a56946ecec41a6c18; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541512280")
//                            .cookies(cookies)
//                            .followRedirects(true).execute();
//                    return response2.body();
//                } catch (Exception e) {
//                    log.error("获取 __jsl_clearance 失败 重试中");
//                    return getBodyCk(cookies);
//                }
//            }
//            return response.body();
//        } catch (Exception e) {
//            log.error("获取 __jsl_clearance 失败，{}", e.getMessage());
//            return getBodyCk(cookies);
//        }
//    }


    public static void main(String[] args) throws Exception {
        proxyUtil2.initIps();
        Thread.sleep(5000);
        Map<String, String> cookies = getCookies();
        System.err.println(cookies);

      /*  String bodyCk = getBodyCk(cookies);

        String cart_md5 = getCartMd5(bodyCk);
        System.err.println(bodyCk);*/
        String addr_id = "769474";
        String vcCodeUrl = "https://mall.phicomm.com/vcode-index-passport6460242.html";

        initBody(cookies);
        System.err.println(cart_md5);

        countDownLatch.await();

        String vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(vcCodeUrl)
                .ignoreContentType(true)
                .cookies(cookies)
                .userAgent(UserAgentUtil.get())
                .proxy(proxyUtil2.getProxy())
                .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());
        System.out.println(vcCodeJson);
//
        Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Connection.Method.POST)
                .proxy(proxyUtil2.getProxy())
                .timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
                .cookies(cookies)
                .userAgent(UserAgentUtil.get())
                .header("X-Requested-With", "XMLHttpRequest")
                .data("cart_md5", cart_md5)
                .data("addr_id", addr_id)
                .data("dlytype_id", "1")
                .data("payapp_id", "alipay")
                .data("yougouma", "")
                .data("invoice_type", "")
                .data("invoice_title", "")
                .data("useVcNum", "0")
                .data("need_invoice2", "on")
                .data("useDdwNum", "29900")
                .data("memo", "")
                .data("vcode", vcCodeJson)
                .execute();
        System.out.println(JSON.parseObject(createOrderResponse.body()));

//        Thread.sleep(1000000);


//        Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
//                .timeout(100000)
//                .header("Host", "mall.phicomm.com")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
//                .header("Referer", "https://mall.phicomm.com/passport-login.html")
//                .header("Connection", "keep-alive")
//                .cookies(cookies)
//                .header("Upgrade-Insecure-Requests", "1")
//                .execute();
//        cookies.putAll(execute.cookies());
//        System.out.println(execute.cookies());
//        System.out.println(execute.body());

//        Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
//                .method(Connection.Method.POST)
//                .cookies(cookies)
//                .timeout(SystemConstant.TIME_OUT)
//                .ignoreContentType(true)
//                .header("Host", "mall.phicomm.com")
//                .header("Connection", "keep-alive")
//                .header("Accept", "application/json, text/javascript, */*; q=0.01")
//                .header("Origin", "https://mall.phicomm.com")
//                .header("X-Requested-With", "XMLHttpRequest")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
//                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                .header("Referer", "https://mall.phicomm.com/passport-login.html")
//                .header("Accept-Encoding", "gzip, deflate, br")
//                .header("Accept-Language", "zh-CN,zh;q=0.9")
//                .data("forward", "")
//                .data("uname", "15928676875")
//                .data("password", "li5201314")
//                .execute();
//        System.out.println(loginResponse.body());
//        cookies.putAll(loginResponse.cookies());

    }

    private static String getCartMd5(String bodyCk) {
        for (Element element : Jsoup.parse(bodyCk).getElementsByTag("input")) {
            if (element.attr("name").equals("cart_md5")) {
                return element.attr("value");
            }
        }
        throw new RuntimeException("cart_md5获取失败");
    }

    public static String getck(String s) throws Exception {
        System.err.println(s);
        StringBuilder sb = new StringBuilder()
                .append("function getClearance(){")
                .append(s.split("</script>")[0].replace("<script>", "").replaceAll("try\\{eval", "try{return"))
                .append("};");
        String resHtml = sb.toString().replace("</script>", "").replace("eval", "return").replace("<script>", "");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        resHtml = new String(resHtml);
        engine.eval(resHtml);
        Invocable invocable = (Invocable) engine;
        //一级解密结果
        String resJs = (String) invocable.invokeFunction("getClearance");
        String overJs = "function getClearance2(){ var a" + resJs.split("document.cookie")[1].split("Path=/;'")[0] + "Path=/;';return a;};";
        overJs = overJs.replace("window.headless", "'undefined'");
        engine.eval(overJs);
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");
        return over;
    }

}
