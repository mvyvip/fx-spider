package com.hs.reptilian.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.util.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@SuppressWarnings("all")
public class FxController {

    @Autowired
    private OrderAccountRepository orderRepository;

    @Autowired
    private ProxyUtil proxyUtil;

    @GetMapping("/init")
    public Object initAddress() {
        List<OrderAccount> orderAccountList = orderRepository.findAll(Sort.by("createDate").descending());
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());
        for (OrderAccount orderAccount : orderAccountList) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try {
                       Map<String, String> cookies = getCookies(orderAccount.getUsername(), orderAccount.getPassword());
                       Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
                               .timeout(SystemConstant.TIME_OUT)
                               .ignoreContentType(true)
                               .data("maddr[name]", "张雷")
                               .data("maddr[mobile]", "17074639890")
                               .data("maddr[area]", "mainland:湖北省/黄石市/铁山区:1891")
                               .data("maddr[addr]", "湖北省黄石市铁山区龙渠湾横城超市代收")
                               .data("maddr[is_default]", "true")
//					.data("maddr[name]", "冷先生")
//					.data("maddr[mobile]", "15879299250")
//					.data("maddr[area]", "mainland:江西省/九江市/武宁县:1407")
//					.data("maddr[addr]", "江西省九江市武宁县小康路19号-诗人")
//					.data("maddr[is_default]", "true")
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


    private String getVc(Map<String, String> cookies) {
        try {
            Document parse = Jsoup.connect("https://mall.phicomm.com/my-vclist.html")
                    .cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .execute().parse();
            String vc = parse.body().text().split("可用维C ")[1].split(" 冻结维C")[0];
            return vc;
        } catch (Exception e) {
            return getVc(cookies);
        }
    }

    private Connection.Response getOrders(Map<String, String> cookies) {
        try {
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .proxy(proxyUtil.getProxy())
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
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword());
                        if(MapUtils.isNotEmpty(cookies)) {
                            String vc = getVc(cookies);
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
                                                    .cookies(cookies)
                                                    .timeout(SystemConstant.TIME_OUT)
                                                    .proxy(proxyUtil.getProxy())
                                                    .execute();
                                            if(execute.body().contains("订单确认收货成功")) {
                                                log.info(orderAccount.getPhone() + "--- " + "收货成功");
                                            } else {
                                                log.info(orderAccount.getPhone() + " ----" + orderAccount.getPassword() + "--- " + "收货失败");
                                            }
                                        }
                                    }


                                    if(date.getTime() > DateUtils.addDays(new Date(), -7).getTime() && !order.getStatus().equals("已取消")) {
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

    private String getWuliu(Element element, Map<String, String> cookies) {
        try {
            String body = Jsoup.connect("https://mall.phicomm.com/order-logistics_tracker-" + element.attr("data-deliveryid") + ".html")
                    .timeout(SystemConstant.TIME_OUT)
                    .cookies(cookies)
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
                    .cookies(cookies)
                    .proxy(proxyUtil.getProxy())
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

    private Map<String, String> getCookies(String username, String password)  {
        try {
            Connection.Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html")
                    .proxy(proxyUtil.getProxy())
                    .method(Connection.Method.POST)
                    .cookies(pageCookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("forward", "")
                    .data("uname", username)
                    .data("password", password)
                    .execute();

            if(loginResponse.body().contains("error")) {
                log.error("帐号或密码不正确----" + username + "----" + password);
                return null;
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            log.info(username + "--登录成功");
            return cks;
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("登陆失败：" + e.getMessage());
            return getCookies(username, password);
        }
    }

}
