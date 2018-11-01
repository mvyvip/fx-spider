package com.hs.reptilian.task.runnable;

import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
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
public class SpliderRunnable implements Runnable {

    private String username;

    private String password;

    private ProxyUtil proxyUtil;

    private Integer updateCodeSecond;

    private String goods;

    private String goodsUrl;

    private String vc;

    public SpliderRunnable(String username, String password, ProxyUtil proxyUtil, Integer updateCodeSecond, String goods, String goodsUrl, String vc) {
        this.username = username;
        this.password = password;
        this.proxyUtil = proxyUtil;
        this.updateCodeSecond = updateCodeSecond;
        this.goods = goods;
        this.goodsUrl = goodsUrl;
        this.vc = vc;
    }

    private String vcCodeJson;

    private String vcCodeUrl;

    private String addrId;

    private volatile String rsbody;

    private Map<String, String> cookies;

    private Date initCodeDate = new Date();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private AtomicBoolean initCodeFlag = new AtomicBoolean(true);

    @Override
    public void run() {
        try {
            cookies = getCookies(username, password, 0);
            if (MapUtils.isNotEmpty(cookies) && isNotOrdered(cookies) && vcIsEnough(cookies) && initData(cookies)) {
                CookieUtils.addCookies(new OrderAccount(username, password), cookies);
                initBody(cookies);

                countDownLatch.await();

                Document document = Jsoup.parse(rsbody);
                String cart_md5 = getCartMd5(document);

                AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                while (atomicBoolean.get()) {
                    try {
                        if (StringUtils.isEmpty(vcCodeJson) || (new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 44).getTime())) {
                            vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(vcCodeUrl)
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
                                                .data("addr_id", addrId)
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
                                        CookieUtils.addTask(username, password, vcCodeUrl, addrId, vc, cart_md5, cookies);
                                        if (createOrderResponse.body().contains("success")) {
                                            info("抢购成功，请付款!!!!" + cookies.get("_SID"));
                                            atomicBoolean.set(false);
                                        }
                                    } catch (Exception e) {
                                        info("抢购失败--" + e.getMessage());
                                    } finally {
                                        info("-addrId: " + addrId + ", vcCodeUrl: " + vcCodeUrl + ", cookies: " + cookies + ", cart_md5:" +  cart_md5);
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

    public static void main(String[] args) throws Exception {
        Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET)
                .cookie("_SID", "066165f5377843a6b19d9fcf9ba1907c")
                .timeout(SystemConstant.TIME_OUT).userAgent(UserAgentUtil.get())
                .execute().parse();
        System.out.println(document);
        Elements dds = document.select("dd.clearfix.editing");

        if (dds == null || dds.size() == 0) {
            log.info("----无收货地址，请设置！！");
        }
        String[] split = dds.get(0).getElementsByTag("a").get(0).attr("href").split("-");
        String addrId = split[split.length - 1].split("\\.")[0];

        Elements dts = document.getElementsByTag("dt");
        String address = "";
        for (Element dt : dts) {
            if (dt.text().contains("默认")) {
                address = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                continue;
            }
        }

    }

    private boolean initData(Map<String, String> cookies) {
        try {
            vcCodeUrl = "https://mall.phicomm.com/vcode-index-passport" + cookies.get("MEMBER_IDENT") + ".html";
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT).userAgent(UserAgentUtil.get())
                    .proxy(proxyUtil.getProxy())
                    .execute().parse();
            Elements dds = document.select("dd.clearfix.editing");

            if (dds == null || dds.size() == 0) {
                log.info(username + "----" + password + "----无收货地址，请设置！！");
                return false;
            }
            String[] split = dds.get(0).getElementsByTag("a").get(0).attr("href").split("-");
            addrId = split[split.length - 1].split("\\.")[0];

            Elements dts = document.getElementsByTag("dt");
            String address = "";
            for (Element dt : dts) {
                if (dt.text().contains("默认")) {
                    address = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                    continue;
                }
            }

            log.info(username + "----" + password + "----addrId: {}, address: {}, vcCodeUrl: {}", addrId, address, vcCodeUrl);
            syncCode();
            return true;
        } catch (Exception e) {
            info("--初始化地址失败" + e.getMessage());
            return initData(cookies);
        }
    }

    private void syncCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (initCodeFlag.get()) {
                    try {
                        if (new Date().getMinutes() == 59 && new Date().getSeconds() >= updateCodeSecond
                                || (vcCodeJson != null && new Date().getTime() >= DateUtils.addSeconds(initCodeDate, 45).getTime())) {
                            info("开始提前验证码");
                            initCodeDate = new Date();
                          /*  vcCodeJson = RuoKuaiUtils.createByPost("2980364030", "li5201314", "4030", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e",
                                    Jsoup.connect(vcCodeUrl)
                                            .ignoreContentType(true)
                                            .cookies(cookies)
                                            .proxy(proxyUtil.getProxy())
                                            .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());*/
                            vcCodeJson = FeiFeiUtil.validate(Jsoup.connect(vcCodeUrl)
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

    private boolean vcIsEnough(Map<String, String> cookies) {
        String vc = getVc(cookies);
        if (Integer.parseInt(vc) < Integer.parseInt(vc)) {
            log.info("phone: {}, password: {}, 抢购[{}], 需要vc{}, 可用vc{}",
                    username, password, goods, vc, vc);
            return false;
        }
        return true;
    }

    /**
     * 查询本周是否抢购过
     *
     * @param cookies
     * @return
     */
    private boolean isNotOrdered(Map<String, String> cookies) {
        try {
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .userAgent(UserAgentUtil.get())
                    .proxy(proxyUtil.getProxy())
                    .cookies(cookies)
                    .execute();
            if (response.body().contains("暂无")) {
                log.info(username + "----" + password + "----无订单");
                return true;
            } else {
                Document document = Jsoup.parse(response.body());
                Element table1 = document.getElementsByTag("table").get(1);
                Date date = DateUtils.parseDateStrictly(table1.getElementsByTag("li").get(1).text().trim(), "yyyy-MM-dd HH:mm");
                if (table1.text().contains(goods) && (!table1.text().contains("已取消")) && date.getTime() > DateUtils.addDays(new Date(), -7).getTime()) {
                    log.info(username + "----" + password + "----已抢购---" + goods);
                    return false;
                }
                return true;

            }
        } catch (Exception e) {
            return isNotOrdered(cookies);
        }

    }

    private Map<String, String> getCookies(String username, String password, int tryCount) {
        try {
            Map<String, String> pageCookies = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT)
                    .userAgent(UserAgentUtil.get())
                    .proxy(proxyUtil.getProxy())
                    .execute().cookies();
            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                    .proxy(proxyUtil.getProxy())
                    .method(Connection.Method.POST)
                    .cookies(pageCookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .userAgent(UserAgentUtil.get())
                    .data("uname", username)
                    .data("password", password)
                    .execute();
            if (loginResponse.body().contains("error")) {
                throw new RuntimeException("账号或密码错误");

            }
            info("登录成功");
            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            return cks;
        } catch (Exception e) {
            log.error(e.getMessage());
            if (tryCount < 70) {
                info("第" + (++tryCount) + "次登录重试");
                return getCookies(username, password, tryCount);
            }
            log.error(username + "----" + password + "----超过最大登录次数");
            return null;
        }
    }

    private String getVc(Map<String, String> cookies) {
        try {
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .execute().parse();
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];
            return vc;
        } catch (Exception e) {
            return getVc(cookies);
        }
    }

    private void info(String msg) {
        log.info(username + "----" + password + "----" + msg);
    }

}
