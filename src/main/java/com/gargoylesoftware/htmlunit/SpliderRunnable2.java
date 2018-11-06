package com.gargoylesoftware.htmlunit;

import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.Account;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.util.CookieUtils;
import com.hs.reptilian.util.PandaProxyUtil;
import com.hs.reptilian.util.ProxyUtil;
import com.hs.reptilian.util.UserAgentUtil;
import com.hs.reptilian.util.feifei.FeiFeiUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Slf4j
@SuppressWarnings("all")
public class SpliderRunnable2 {

    private static PandaProxyUtil proxyUtil = new PandaProxyUtil();


    private static Map<String, String> cookies = new HashMap<>();

    public static void main(String[] args) {
        try {
            String[] split = "__jsluid=8bb86e6252d22cfe38257f373787780e; __jsl_clearance=1541386081.257|0|YFnE9BVmBQaTEVXmtLnXiUYX0a4%3D; _VMC_UID=f17326e3896966bcdbf265ec369b7981; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541386065; _SID=55252b30f8275c966808a42507f84946; UNAME=13648045607; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; CACHE_VARY=79e9a2cfad5b086e9017de76915a505c-0f063e018c840f8a56946ecec41a6c18; c_dizhi=null; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541387029".replace("{", "").replace("}", "").split(",");
            for (String s : split) {
                cookies.put(s.split("=")[0], s.split("=")[1]);
            }

            String cart_md5 = "4be65ab8f483d43070303658a84d740b";
//            proxyUtil.init();
            Thread.sleep(3000);

            // ===========           下单         ==============
            for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                String vcCodeJson = FeiFeiUtil.validate(Jsoup.connect("https://mall.phicomm.com/vcode-index-passport6272948.html")
//                                String vcCodeJson = FeiFeiUtil.validate(Jsoup.connect("http://106.42.25.225/vcode-index-passport6272948.html")
                                    .ignoreContentType(true)
                                    .header("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                                    .header("Accept-Encoding", "gzip, deflate, br")
                                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                                    .header("Connection", "keep-alive")
                                    .header("Cookie", "__jsluid=8bb86e6252d22cfe38257f373787780e; __jsl_clearance=1541386081.257|0|YFnE9BVmBQaTEVXmtLnXiUYX0a4%3D; _VMC_UID=f17326e3896966bcdbf265ec369b7981; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541386065; _SID=55252b30f8275c966808a42507f84946; UNAME=13648045607; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; CACHE_VARY=79e9a2cfad5b086e9017de76915a505c-0f063e018c840f8a56946ecec41a6c18; c_dizhi=null; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1541388674; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541388674; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541388825")
                                    .header("Host", "mall.phicomm.com")
                                    .header("Referer", "https://mall.phicomm.com/checkout-fastbuy.html")
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                                        .followRedirects(true)
//                                    .userAgent(UserAgentUtil.get())
//                                    .proxy(proxyUtil.getProxy())
                                    .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());
                                System.out.println(vcCodeJson);

                                Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Method.POST)
//                                    .proxy(proxyUtil.getProxy())
                                    .timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
                                    .header("", "")
                                    .header("Cookie", "__jsluid=8bb86e6252d22cfe38257f373787780e; __jsl_clearance=1541386081.257|0|YFnE9BVmBQaTEVXmtLnXiUYX0a4%3D; _VMC_UID=f17326e3896966bcdbf265ec369b7981; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541386065; _SID=55252b30f8275c966808a42507f84946; UNAME=13648045607; MEMBER_IDENT=6272948; MEMBER_LEVEL_ID=1; CACHE_VARY=79e9a2cfad5b086e9017de76915a505c-0f063e018c840f8a56946ecec41a6c18; c_dizhi=null; Hm_lvt_d7682ab43891c68a00de46e9ce5b76aa=1541388674; Hm_lpvt_d7682ab43891c68a00de46e9ce5b76aa=1541388674; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541388825")
                                    .header("Host", "mall.phicomm.com")
                                    .header("Referer", "https://mall.phicomm.com/checkout-fastbuy.html")
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                                    .header("X-Requested-With", "XMLHttpRequest")
                                    .data("cart_md5", cart_md5)
                                    .data("addr_id", "769455")
                                    .data("dlytype_id", "1")
                                    .data("payapp_id", "alipay")
                                    .data("yougouma", "")
                                    .data("invoice_type", "")
                                    .data("invoice_title", "")
                                    .data("useVcNum", "29900")
                                    .data("need_invoice2", "on")
                                    .data("useDdwNum", "0")
                                    .data("memo", "")
                                    .data("vcode", vcCodeJson)
                                    .execute();
                                if (createOrderResponse.body().contains("success")) {
                                    System.out.println("抢购成功，请付款!!!!" + cookies.get("_SID"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            log.error("下单失败： " + e.getMessage());
        }
    }


}
