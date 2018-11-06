package com;

import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.ProxyUtil2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FxProxyTest {

    public static ProxyUtil2 proxyUtil2 = new ProxyUtil2();

    public static String getck(String s) throws Exception {
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

    public static String initBody(Map<String, String> cookies) {
            try {
                Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/cart-fastbuy-13-1.html")
                                    .method(Connection.Method.GET)
                                    .timeout(SystemConstant.TIME_OUT)
                                    .proxy(proxyUtil2.getProxy())
//                                    .header("Host", "mall.phicomm.com")
                                    .header("Host", "113.107.238.206")
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.75 Safari/537.36")
                                    .cookies(cookies)
                                    .followRedirects(true).execute();
                            if(response.statusCode() == 200) {
                                if(response.body().contains("库存不足,当前最多可售数量")) {
                                    System.out.println("库存不足 - " + new Date().toLocaleString());
                                    initBody(cookies);
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
                            e.printStackTrace();
                            System.out.println("初始化>>>body失败--" + e.getMessage());
                            initBody(cookies);
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("xasdasdasd初始化>>>body失败--" + e.getMessage());
        }
        return "";
    }

    public static void main(String[] args) {
        proxyUtil2.initIps();
        Map<String, String> cookies = new HashMap<>();
        String s = "__jsluid=6d3709ce0c1d71f725f787f3a0bb50c4; _VMC_UID=319a15641a88a47d4b05a4e3bedb58f2; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541512655; _SID=f94a4a4e38754fd6f1dab815026e1edc; UNAME=18585816873; MEMBER_IDENT=6460242; MEMBER_LEVEL_ID=1; CACHE_VARY=46d5e43f688a17ceb1fcf77a3b629ebb-0f063e018c840f8a56946ecec41a6c18; c_peisong=1; c_zhifu=alipay; c_dizhi=null; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541515818; __jsl_clearance=1541516857.396|0|2FDklvlSK4I48FTE37IpVM7D5Ek%3D";
        for (String arg : s.split(";")) {
            String[] split = arg.split("=");
            cookies.put(split[0], split[1]);
        }

        initBody(cookies);
    }

}
