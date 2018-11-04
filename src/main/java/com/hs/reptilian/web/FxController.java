package com.hs.reptilian.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.Account;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.repository.AccountRepository;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.repository.TaskListRepository;
import com.hs.reptilian.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@SuppressWarnings("all")
public class FxController {

    @Autowired
    private OrderAccountRepository orderRepository;

    @Autowired
    private PandaAutoProxyUtil proxyUtil;

    @Autowired
    private TaskListRepository taskListRepository;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/login")
    public ViewData login() throws Exception {
        List<OrderAccount> orderRepositoryByStatus = orderRepository.findByStatus("1");
        log.info("本次需要初始化：[{}], 条数据", orderRepositoryByStatus.size());

        for (OrderAccount orderAccount : orderRepositoryByStatus) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAccountCookie(orderAccount);
                    }catch (Exception e){
                        log.error(e.getMessage());
                    }
                }
            }).start();
            Thread.sleep(500);
        }

     /*   for (OrderAccount orderAccount : orderRepositoryByStatus) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAccountCookie(orderAccount);
                    } catch (Exception e) {
                        log.info("初始化失败：" + e.getMessage());
                    }
                }
            }).start();
            Thread.sleep(30);
        }*/
        return ViewData.builder().build();
    }

    @GetMapping("/init")
    public Object initAddress() {
        List<OrderAccount> orderAccountList = orderRepository.findByStatus("1");
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());
        for (OrderAccount orderAccount : orderAccountList) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try {
                       Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0);
                       Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
                               .timeout(SystemConstant.TIME_OUT)
                               .ignoreContentType(true).userAgent(UserAgentUtil.get())
                                .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                               .data("maddr[name]", "李涛")
                               .data("maddr[mobile]", "13648045607")
                               .data("maddr[area]", "mainland:四川省/成都市/金牛区:2533")
                               .data("maddr[addr]", "五块石金色港湾1栋")
                               .data("maddr[is_default]", "true")
                               .execute();
                       log.info(orderAccount + "---设置默认地址成功");
                   }catch (Exception e) {
                   } finally {
                       countDownLatch.countDown();
                   }
               }
           }).start();
        }
        return "success";
    }


    private void setAccountCookie(OrderAccount orderAccount) throws Exception {
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
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .header("X-WxappStorage-SID", id)
                    .timeout(SystemConstant.TIME_OUT).execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/m/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                    .cookies(pageResponse.cookies())
                    .timeout(SystemConstant.TIME_OUT)
                    .cookie("_SID", id)

                    .ignoreContentType(true)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
                    .header("Set-jsonstorage", "jsonstorage")
                    .header("Origin", "https://mall.phicomm.com")
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", orderAccount.getPhone())
                    .data("password", orderAccount.getPassword())
                    .execute();

            if(loginResponse.body().contains("error")) {
                throw new RuntimeException(JSON.parseObject(loginResponse.body()).toString());
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageResponse.cookies());
            cks.putAll(loginResponse.cookies());
            System.out.println(cks);

            Account account = new Account();
            account.setCreateDate(new Date());
            account.setMemberIdent("https://mall.phicomm.com/vcode-index-passport" + loginResponse.cookie("MEMBER_IDENT") + ".html");
            account.setMobile(orderAccount.getPhone());
            account.setPassword(orderAccount.getPassword());
            account.setRemark(orderAccount.getRemark());
            account.setSid(cks.toString());
            account.setStatus(1);
            account.setAddrId(getAddrId(cks, account, 0));
            accountRepository.save(account);

            log.info(">>>>>>> " + atomicInteger.incrementAndGet());
        }catch (Exception e){
            System.out.println(e.getMessage());
            setAccountCookie(orderAccount);
        }
    }

   /* private void setAccountCookie(OrderAccount orderAccount) throws Exception {
        try {
            String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            Connection.Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(org.jsoup.Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .userAgent(UserAgentUtil.get())
                    .header("X-WxappStorage-SID", id)
                    .timeout(SystemConstant.TIME_OUT).execute();

            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/m/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                    .timeout(SystemConstant.TIME_OUT)
                    .cookie("_SID", id)
                    .userAgent(UserAgentUtil.get())
                    .ignoreContentType(true)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
                    .header("Set-jsonstorage", "jsonstorage")
                    .header("Origin", "https://mall.phicomm.com")
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", orderAccount.getPhone())
                    .data("password", orderAccount.getPassword())
                    .execute();

            if(loginResponse.body().contains("error")) {
                throw new RuntimeException(JSON.parseObject(loginResponse.body()).toString());
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageResponse.cookies());
            cks.putAll(loginResponse.cookies());
            System.err.println(cks);

            Account account = new Account();
            account.setCreateDate(new Date());
            account.setMemberIdent("https://mall.phicomm.com/vcode-index-passport" + loginResponse.cookie("MEMBER_IDENT") + ".html");
            account.setMobile(orderAccount.getPhone());
            account.setPassword(orderAccount.getPassword());
            account.setRemark(orderAccount.getRemark());
            account.setSid(cks.toString());
            account.setStatus(1);
            account.setAddrId(getAddrId(pageResponse.cookie("_SID").getSid(), account, 0));
            accountRepository.save(account);

            log.info(">>>>>>> " + atomicInteger.incrementAndGet());

        }catch (Exception e){
            log.error("提前登录失败：{}----{}-----{}" + orderAccount.getPhone(), orderAccount.getPassword(), e.getMessage());
            setAccountCookie(orderAccount);
        }
    }*/

    private String getAddrId(Map<String, String> cks, Account account, int tryCount) {
        try {
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cks)
                    .timeout(SystemConstant.TIME_OUT).userAgent(UserAgentUtil.get())
                    .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .execute().parse();
            Elements dds = document.select("dd.clearfix.editing");

            if (dds == null || dds.size() == 0) {
                log.info(account.getMobile() + "----" + account.getPassword() + "----无收货地址，请设置！！");
                return null;
            }
            String[] split = dds.get(0).getElementsByTag("a").get(0).attr("href").split("-");
            return split[split.length - 1].split("\\.")[0];
        } catch (Exception e) {
            tryCount++;
            if(tryCount > 10) {
                return null;
            }
            return getAddrId(cks, account, tryCount);
        }
    }

    private String getVc(Map<String, String> cookies) {
        try {
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .userAgent(UserAgentUtil.get())
                    .timeout(SystemConstant.TIME_OUT)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .execute().parse();
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];
            return vc;
        } catch (Exception e) {
            return getVc(cookies);
        }
    }

    private Connection.Response getOrders2(Map<String, String> cookies, Integer tryCount) {
        try {
            String randomIp = getRandomIp();
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(60000)
                    .userAgent(UserAgentUtil.get())
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .cookies(cookies)
                    .execute();
            return response;
        } catch (Exception e) {
            tryCount++;
            if(tryCount > 2) {
                return null;
            }
            return getOrders2(cookies, tryCount);
        }
    }

    private Connection.Response getOrders(Map<String, String> cookies) {
        try {
            String randomIp = getRandomIp();
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(50000)
                    .userAgent(UserAgentUtil.get())
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .cookies(cookies)
                    .execute();
            return response;
        } catch (Exception e) {
            return getOrders(cookies);
        }
    }

    @GetMapping("/orders")
    public Object findAllOrder(String name, String type) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> needGet = new ArrayList<>();
        List<OrderAccount> orderAccountList = orderRepository.findAll(Sort.by("createDate").descending());

        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (OrderAccount orderAccount : orderAccountList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0);
                        if(MapUtils.isNotEmpty(cookies)) {
                           // String vc = getVc(cookies);
                            String vc = "0";
                            Connection.Response response = getOrders(cookies);
                            orderAccount.setVc(vc);
                            if(response.body().contains("暂无")) {
                                needGet.add(orderAccount);
                                log.info(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----无订单");
                            } else {
                                Document document = Jsoup.parse(response.body());
                                Elements tables = document.getElementsByTag("table");

                                boolean flag = true;
                                for (int i = 1; i < tables.size(); i++) {
                                    OrderAccount order = new OrderAccount();
                                    order.setVc(vc);
                                    order.setRemark(orderAccount.getRemark());
                                    Element table = tables.get(i);

                                    Elements elements = table.getElementsByClass("text-muted");
                                    order.setPhone(orderAccount.getPhone());
                                    order.setPassword(orderAccount.getPassword());
                                    order.setGoodsName(elements.get(1).text().trim());
                                    order.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                    order.setStatus(table.getElementsByTag("span").text().trim());
                                    Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");

                                    Elements as = table.getElementsByTag("a");
                                    for (Element a : as) {
                                        if(a.text().contains("确认收货")) {
                                            String href = a.attr("href");
                                            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com" + href)
                                                    .cookies(cookies).userAgent(UserAgentUtil.get())
                                                    .timeout(SystemConstant.TIME_OUT)
                                                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                                                    .execute();
                                            if(execute.body().contains("订单确认收货成功")) {
                                                log.info(orderAccount.getPhone() + "--- " + "收货成功");
                                            } else {
                                                log.info(orderAccount.getPhone() + " ----" + orderAccount.getPassword() + "--- " + "收货失败");
                                            }
                                        }
                                    }


                                    if(date.getTime() > DateUtils.addDays(new Date(), -7).getTime()) {
                                        Elements aElements = table.getElementsByTag("a");
                                        setDetail(cookies, aElements, order, 0);
                                        order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                        flag = false;

                                        if(StringUtils.isNotEmpty(name) && !order.getAddress().contains(name)) {
                                            continue;
                                        }

                                        if(StringUtils.isNotEmpty(type)) {
                                            if(type.equals("1")  && !order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            } else if(type.equals("2") && order.getLogisticsInfo().contains("签收")) {
                                                continue;
                                            }
                                        }
                                        orders.add(order);
                                    }
                                }
                                if(flag) {
                                    needGet.add(orderAccount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("登录失败： " + e.getMessage());
                    } finally {
                        System.err.println("xxx>>> " + atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }


        while (!(atomicInteger.get() == orderAccountList.size())) {
            // 等待线程执行完毕
        }
        System.out.println("开始");

        needGet.stream().forEach(orderAccount -> {
            System.err.println(orderAccount.getPhone() + "----" + orderAccount.getPassword() + "----" + orderAccount.getVc());
        });

        result.put("code", 0);
        result.put("count", orders.size());
        result.put("data", orders);

        return result;
    }

    private String getBase64(Element a, Map<String, String> cookies, Integer tryCount) {
        try {
            String payUrl = "https://mall.phicomm.com/" + a.attr("href").replace("payment", "dopayment");
            Document dc = Jsoup.connect(payUrl)
                    .timeout(80 * 1000).userAgent(UserAgentUtil.get())
                    .cookies(cookies)
                    .execute().parse();
            Elements inputs = dc.getElementsByTag("input");
            StringBuilder sb = new StringBuilder("https://mapi.alipay.com/gateway.do?_input_charset=utf-8");
            for (Element input : inputs) {
                if(!input.attr("name").equals("_input_charset=utf-8")) {
                    sb.append("&" + input.attr("name") + "=" + input.val());
                }
            }
            Connection.Response execute = Jsoup.connect(sb.toString())
                    .ignoreContentType(true)
                    .timeout(60 * 1000).userAgent(UserAgentUtil.get())
                    .header("Host", "unitradeadapter.alipay.com")
                    .header("Connection", "keep-alive")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6756.400 QQBrowser/10.2.2457.400")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cookie", "zone=RZ24B; ALIPAYJSESSIONID=RZ24YVLcoQ6DSDXkHxzHHnRWDdgSKNsuperapiRZ24; ctoken=eKsmhfG-efHuk-Bu")
                    .execute();

            Elements elementsByAttributeValue = execute.parse().getElementsByAttributeValue("name", "qrCode");
            if(CollectionUtils.isNotEmpty(elementsByAttributeValue)) {
                String qrCode = elementsByAttributeValue.get(0).val();
                String base64 = MatrixToImageWriter.writeToFile(qrCode);
                return base64;
            }
        } catch (Exception e){
            log.error("获取二维码失败: " + e.getMessage());
            tryCount++;
            if (tryCount < 25) {
                return getBase64(a, cookies, tryCount);
            }
        }
        return null;
    }

    private String getPayUrl(Element a, Map<String, String> cookies, Integer tryCount) {
        try {
            String payUrl = "https://mall.phicomm.com/" + a.attr("href").replace("payment", "dopayment");
            Document dc = Jsoup.connect(payUrl)
                    .timeout(60 * 1000)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .cookies(cookies).userAgent(UserAgentUtil.get())
                    .execute().parse();
            Elements inputs = dc.getElementsByTag("input");
            StringBuilder sb = new StringBuilder("https://mapi.alipay.com/gateway.do?_input_charset=utf-8");
            for (Element input : inputs) {
                if(!input.attr("name").equals("_input_charset=utf-8")) {
                    sb.append("&" + input.attr("name") + "=" + input.val());
                }
            }
           return sb.toString();
        } catch (Exception e){
            log.error("获取二维码失败: " + e.getMessage());
            tryCount++;
            if (tryCount < 2) {
                return getPayUrl(a, cookies, tryCount);
            }
            return "---";
        }
    }

    @GetMapping("/fast/orders")
    public Object fastFindAllOrder(Integer page, Integer limit) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> needGet = new ArrayList<>();

        AtomicInteger atomicInteger = new AtomicInteger(0);
        ConcurrentHashMap<OrderAccount, Map<String, String>> cookies = CookieUtils.getCookies();

        Set<Map.Entry<OrderAccount, Map<String, String>>> entries = cookies.entrySet();
        CountDownLatch countDownLatch = new CountDownLatch(entries.size());
        for (Map.Entry<OrderAccount, Map<String, String>> entry : entries) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection.Response response = getOrders2(entry.getValue(), 0);
                        if(response != null && !response.body().contains("暂无")) {
                            Document document = Jsoup.parse(response.body());
                            Elements tables = document.getElementsByTag("table");
                            for (int i = 1; i < tables.size(); i++) {
                                OrderAccount order = new OrderAccount();
                                OrderAccount orderAccount = entry.getKey();
                                Element table = tables.get(i);

                                Elements as = table.getElementsByTag("a");
                                order.setPayBase64("---");
                                for (Element a : as) {
                                    if(a.text().contains("立即付款")) {
//                                        String base64 = getBase64(a, entry.getValue(), 0);
                                        String base64 = getPayUrl(a, entry.getValue(), 0);
                                        order.setPayBase64(base64);
                                    }
                                }

                                Elements elements = table.getElementsByClass("text-muted");
                                order.setPhone(orderAccount.getPhone());
                                order.setPassword(orderAccount.getPassword());
                                order.setGoodsName(elements.get(1).text().trim());
                                order.setOrderNo(elements.get(0).text().replaceAll("  ", "").trim());
                                order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                order.setStatus(table.getElementsByTag("span").text().trim());
//                                order.setCookie(entry.getValue().get("_SID").toString());
                                Date date = DateUtils.parseDateStrictly(order.getOrderCreateDate(), "yyyy-MM-dd HH:mm");
                                if(date.getTime() > DateUtils.addDays(new Date(), -7).getTime() && !order.getStatus().equals("已取消")) {
                                    Elements aElements = table.getElementsByTag("a");
                                    order.setOrderCreateDate(table.getElementsByTag("li").get(1).text().trim());
                                    orders.add(order);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }).start();
        }

        countDownLatch.await();

        result.put("code", 0);
        result.put("count", orders.size());
        result.put("data", orders);

        return result;
    }

    @GetMapping("/upload")
    public ViewData upload(){
        taskListRepository.saveAll(CookieUtils.taskLists);
        return ViewData.builder().build();
    }

    @GetMapping("/cookies")
    public ViewData cookies(){
        return ViewData.builder().total(CookieUtils.getCookies().size()).data(CookieUtils.getCookies()).build();
    }

    private String getWuliu(Element element, Map<String, String> cookies) {
        try {
            String body = Jsoup.connect("https://mall.phicomm.com/order-logistics_tracker-" + element.attr("data-deliveryid") + ".html")
                    .timeout(SystemConstant.TIME_OUT)
                    .cookies(cookies).userAgent(UserAgentUtil.get())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .execute().body();
            return body;
        } catch (Exception e) {
            return getWuliu(element, cookies);
        }
    }

    private void setDetail(Map<String, String> cookies, Elements aElements, OrderAccount order, Integer retryCount) {
        try {
            Document detailOrder = Jsoup.connect("https://mall.phicomm.com" + (aElements.get(aElements.size() - 1).attr("href")))
                    .cookies(cookies).userAgent(UserAgentUtil.get())
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .timeout(SystemConstant.TIME_OUT).execute().parse();

            Elements dd = detailOrder.getElementsByTag("dd");
            order.setAddress(dd.get(2).text());
            for (Element element : dd) {
                if(StringUtils.isNotEmpty(element.attr("data-deliveryid"))) {
                    String body = getWuliu(element, cookies);
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    order.setLogisticsNum(jsonObject.getJSONObject("data").getString("logi_no"));
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("logi_log");
                    if(jsonArray.size() > 0) {
                        order.setLogisticsInfo(JSONObject.parseObject(jsonArray.get(0).toString()).getString("context"));
                    }
                }
            }
        } catch (Exception e){
            setDetail(cookies, aElements, order, retryCount);
        }
    }

    private Map<String, String> getCookies(String username, String password, int tryCount) throws InterruptedException {
        try {
            String randomIp = getRandomIp();
            Map<String, String> pageCookies = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .userAgent(UserAgentUtil.get())
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                    .execute().cookies();
            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .method(Connection.Method.POST)
                    .userAgent(UserAgentUtil.get())
                    .cookies(pageCookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", username)
                    .data("password", password)
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                    .execute();
            if (loginResponse.body().contains("error")) {
                throw new RuntimeException("账号或密码错误");

            }
            log.info(username + "----登录成功");
            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            return cks;
        } catch (Exception e) {
            log.error("登录失败: " + e.getMessage());
            if (tryCount < 25) {
                log.info(username + "第" + (++tryCount) + "次登录重试");
                return getCookies(username, password, tryCount);
            }
            log.error(username + "----" + password + "----超过最大登录次数");
            return null;
        }
    }

    private void setAddr(Map<String, String> cookies, OrderAccount orderAccount) {
        try {
            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .userAgent(UserAgentUtil.get())
                    .ignoreContentType(true)

                    .data("maddr[name]", "李涛")
                    .data("maddr[mobile]", "13648045607")
                    .data("maddr[area]", "mainland:四川省/成都市/金牛区:2533")
                    .data("maddr[addr]", "五块石金色港湾1栋")
                    .data("maddr[is_default]", "true")

                    /*.data("maddr[name]", "刘正周")
                    .data("maddr[mobile]", "13863749494")
                    .data("maddr[area]", "mainland:山东省/济宁市/微山县:1583")
                    .data("maddr[addr]", "山东省济宁市微山县苏园一村32号楼1单元888号-诗人")
                    .data("maddr[is_default]", "true")*/

                    .execute();
            log.info(orderAccount + "---设置默认地址成功");
        } catch (Exception e) {
            setAddr(cookies, orderAccount);
        }
    }

    @GetMapping("/accounts")
    public Object accounts(String name, String type) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<OrderAccount> orders = new ArrayList<>();
        List<OrderAccount> orderAccountList = orderRepository.findAll();
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());

        int i = 0;
        for (OrderAccount orderAccount : orderAccountList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0);
                        orders.add(orderAccount);

                        if(cookies != null) {
                            setDefaultAddress(orderAccount, cookies);

                            orderAccount.setStatus2(orderAccount.getStatus());
                            if(MapUtils.isNotEmpty(cookies)) {
                                String vc = getVc(cookies, orderAccount);
                                Connection.Response response = getOrders(cookies);
                                orderAccount.setVc(vc);
                            }
                        }
                    } catch (Exception e) {
                        log.error("登录失败： " + e.getMessage());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }).start();
            System.err.println(++i);
            Thread.sleep(300);
        }


        countDownLatch.await();
        System.out.println("开始");

        result.put("code", 0);
        result.put("count", orders.size());
        result.put("data", orders);

        return result;
    }

    private String getVc(Map<String, String> cookies, OrderAccount oc) {
        try {
            String randomIp = getRandomIp();
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .userAgent(UserAgentUtil.get())
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                    .execute().parse();
            oc.setVc2(parse.body().text().split("冻结维C ")[1].split(" 维C明细")[0]);
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];

            Document info = Jsoup.connect("https://mall.phicomm.com/my-setting.html")
                    .cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT)

                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .userAgent(UserAgentUtil.get())
                    .header("VIA", randomIp)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .execute().parse();

            if(info.body().text().contains("已认证")) {
                oc.setRenzheng("已认证");
            } else {
                oc.setRenzheng("未认证");
            }

            return vc;
        } catch (Exception e) {
            return getVc(cookies, oc);
        }
    }


    private void setDefaultAddress(OrderAccount orderAccount, Map<String, String> cookie) {
        try {
            String randomIp = getRandomIp();
            Document document = Jsoup.connect("https://mall.phicomm.com/my-receiver.html").method(Connection.Method.GET).cookies(cookie).timeout(SystemConstant.TIME_OUT)
                   .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .userAgent(UserAgentUtil.get())
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                    .execute().parse();
            Elements dts = document.getElementsByTag("dt");
            for (Element dt : dts) {
                if(dt.text().contains("默认")) {
                    String defaultAddress = dt.getElementsByTag("span").get(0).text() + document.getElementsByTag("dd").get(0).text();
                    orderAccount.setDefaultAddress(defaultAddress);
                }
            }
        } catch (Exception e) {
            log.error("获取默认地址失败：" + e.getMessage());
            setDefaultAddress(orderAccount, cookie);
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
