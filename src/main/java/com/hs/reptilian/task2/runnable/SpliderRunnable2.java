package com.hs.reptilian.task2.runnable;

import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.Account;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.repository.TaskListRepository;
import com.hs.reptilian.util.CookieUtils;
import com.hs.reptilian.util.ProxyUtil;
import com.hs.reptilian.util.UserAgentUtil;
import com.hs.reptilian.util.feifei.FeiFeiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("all")
public class SpliderRunnable2 implements Runnable {

    private ProxyUtil proxyUtil;

    private Integer updateCodeSecond;

    private String goods;

    private String goodsUrl;

    private String vc;

    private String vcCodeJson;

    private volatile String rsbody;

    private Map<String, String> cookies = new HashMap<>();

    private Date initCodeDate = new Date();

    private Account account;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private AtomicBoolean initCodeFlag = new AtomicBoolean(true);

    public SpliderRunnable2(Account account, ProxyUtil proxyUtil, Integer updateCodeSecond, String goods, String goodsUrl, String vc) {
        this.account = account;
        this.proxyUtil = proxyUtil;
        this.updateCodeSecond = updateCodeSecond;
        this.goods = goods;
        this.goodsUrl = goodsUrl;
        this.vc = vc;
    }

    @Override
    public void run() {
        try {
            String[] split = account.getSid().replace("{", "").replace("}", "").split(",");
            for (String s : split) {
                cookies.put(s.split("=")[0], s.split("=")[1]);
            }
            CookieUtils.addCookies(new OrderAccount(account.getMobile(), account.getPassword()), cookies);
            info("准备中" + cookies);
            while (!(new Date().getMinutes() == 59 && new Date().getSeconds() >= 50)) {
            }
            info("准备完毕，开始抢购");
            syncCode();

            initBody(cookies);
            countDownLatch.await();

            Document document = Jsoup.parse(rsbody);
            String cart_md5 = getCartMd5(document);

            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            while (atomicBoolean.get()) {
                try {
                    if (StringUtils.isEmpty(vcCodeJson) || (new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 44).getTime())) {
                        vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(account.getMemberIdent())
                                .ignoreContentType(true)
                                .cookies(cookies)
                                .userAgent(UserAgentUtil.get())
                                .proxy(proxyUtil.getProxy())
                                .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

                    }
                    String vcode = new String(vcCodeJson);
                    vcCodeJson = null;
                    // ===========           下单         ==============
                    CountDownLatch cd = new CountDownLatch(SystemConstant.TASK_COUNT);
                    for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Connection.Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Connection.Method.POST)
                                            .proxy(proxyUtil.getProxy())
                                            .timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
                                            .cookies(cookies)
                                            .userAgent(UserAgentUtil.get())
                                            .header("X-Requested-With", "XMLHttpRequest")
                                            .data("cart_md5", cart_md5)
                                            .data("addr_id", account.getAddrId())
                                            .data("dlytype_id", "1")
                                            .data("payapp_id", "alipay")
                                            .data("yougouma", "")
                                            .data("invoice_type", "")
                                            .data("invoice_title", "")
                                            .data("useVcNum", vc)
                                            .data("need_invoice2", "on")
                                            .data("useDdwNum", "0")
                                            .data("memo", "")
                                            .data("vcode", vcode)
                                            .execute();
                                    info(JSONObject.parseObject(createOrderResponse.body()).toJSONString());
                                    CookieUtils.addTask(account.getMobile(), account.getPassword(), account.getMemberIdent(), account.getAddrId(), vc, cart_md5, cookies);
                                    if (createOrderResponse.body().contains("success")) {
                                        info("抢购成功，请付款!!!!" + cookies.get("_SID"));
                                        atomicBoolean.set(false);
                                    }
                                } catch (Exception e) {
                                    info("抢购失败--" + e.getMessage());
                                } finally {
                                    info("-addrId: " + account.getAddrId() + ", vcCodeUrl: " + account.getMemberIdent() + ", cookies: " + cookies + ", cart_md5:" + cart_md5);
                                    cd.countDown();
                                }
                            }
                        }).start();
                    }
                    cd.await();
                } catch (Exception e) {
                    log.error("下单失败： " + e.getMessage());
                }
            }
        } catch (Exception e) {
            info("线程启动失败---" + e.getMessage());
            run();
        }
    }

    private static String getCartMd5(Document document) {
        for (Element element : document.getElementsByTag("input")) {
            if (element.attr("name").equals("cart_md5")) {
                return element.attr("value");
            }
        }
        throw new RuntimeException("cart_md5获取失败");
    }

    private String initBody(Map<String, String> cookies) {
        while (rsbody == null) {
            try {
                Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String body = Jsoup.connect(goodsUrl).method(Connection.Method.GET)
                                    .proxy(proxyUtil.getProxy()).userAgent(UserAgentUtil.get())
                                    .timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
                            if (body.contains("库存不足,当前最多可售数量")) {
                                info("库存不足 - " + new Date().toLocaleString());
                            } else if (body.contains("返回商品详情") || body.contains("cart_md5")) {
                                updateRsBody(body);
                            }
                        } catch (Exception e) {
                            log.error("初始化>>>body失败--" + e.getMessage());
                        }
                    }
                }).start();
            } catch (Exception e) {
                log.error("初始化body失败--" + e.getMessage());
            }
        }
        return rsbody;
    }

    private synchronized void updateRsBody(String body) {
        if (rsbody == null) {
            rsbody = body;
            countDownLatch.countDown();
            initCodeFlag.set(false);
        }
    }

    private void syncCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (initCodeFlag.get()) {
                    try {
                        if (new Date().getMinutes() == 59 && new Date().getSeconds() >= updateCodeSecond
                                || (vcCodeJson != null && new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 44).getTime())) {
                            info("开始提前验证码");
                            initCodeDate = new Date();
                            vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(account.getMemberIdent())
                                    .ignoreContentType(true)
                                    .cookies(cookies)
                                    .userAgent(UserAgentUtil.get())
                                    .proxy(proxyUtil.getProxy())
                                    .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

                            info("提前验证码成功：" + vcCodeJson);
                            /** 防止使用途中被二次更新 */
                            Thread.sleep(44 * 1000);
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vcCodeJson = null;
                        System.out.println(e.getMessage());
                    }
                }

            }
        }).start();
    }

    private void info(String msg) {
        log.info(account.getMobile() + "----" + account.getPassword() + "----" + msg);
    }

}
