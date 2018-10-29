package com.hs.reptilian.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.model.TaskList;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.repository.TaskListRepository;
import com.hs.reptilian.util.CookieUtils;
import com.hs.reptilian.util.MatrixToImageWriter;
import com.hs.reptilian.util.ProxyUtil;
import com.hs.reptilian.util.ViewData;
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
import java.util.concurrent.ConcurrentMap;
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

    @Autowired
    private TaskListRepository taskListRepository;

    @GetMapping("/add")
    public ViewData addAccount() {

        String s = "" +
                "15585178794----xy666888----王永远----522425200107026112\n" +
                "13195107814----xy666888----翁恒先----522132197704207328\n" +
                "13078576283----xy666888----杨德先----522421196407094024\n" +
                "13017482710----xy666888----黄伟----522423198410050032\n" +
                "13007865953----xy666888----赵金益----522422197409104039\n" +
                "13017469257----xy666888----袁忠红----522225199205168126\n" +
                "13007805714----xy666888----班婷----52232719841030002X\n" +
                "18585027178----xy666888----陈阳元----522422199803143414\n" +
                "13195104346----xy666888----陈兴勇----522729197802032116\n" +
                "15519044426----xy666888----彭武江----522502198107214819\n" +
                "13037887563----xy666888----罗振分----522322197712051026\n" +
                "13098511425----xy666888----候雪英----411223194502017525\n" +
                "15585249847----xy666888----张明富----520203197103203235\n" +
                "15519136227----xy666888----李素英----512921196805104588\n" +
                "13007863541----xy666888----许登远----520201196410050470\n" +
                "15585167664----xy666888----龙银----522425198704033919\n" +
                "13078575058----xy666888----唐征仁----520222199309190036\n" +
                "15585160375----xy666888----朱顺勇----520112198501152512\n" +
                "13158034660----xy666888----安得江----522423197304188316\n" +
                "13017468974----xy666888----蓝兴武----352122197106083519\n" +
                "15599136491----xy666888----朱厢厢----522424198307282210\n" +
                "13158014058----xy666888----胡双富----520181198210092137\n" +
                "13195217431----xy666888----陈仕刚----520181198209034810\n" +
                "15585163967----xy666888----魏纪苹----520111198301193620\n" +
                "13195113004----xy666888----王守琴----522423199212072622\n" +
                "15519021849----xy666888----欧莎----522524198707263022\n" +
                "13027810389----xy666888----陈洪----520122199911190017\n" +
                "13027820950----xy666888----黄利娥----421023198611057146\n" +
                "13007840618----xy666888----葛辉----52212619740401157X\n" +
                "13027883445----xy666888----郭礼木----510230197203265174\n" +
                "13027829604----xy666888----赵孝福----522324198002029817\n" +
                "13007807245----xy666888----唐静----522425199406126089\n" +
                "13078513424----xy666888----熊英----522422198507133241\n" +
                "13027827683----xy666888----石军----520113197408232018\n" +
                "13007869301----xy666888----邓广梅----52242719921115324X\n" +
                "13027818869----xy666888----田春----520113197501240813\n" +
                "13007868465----xy666888----邓珍飞----522426198910183682\n" +
                "13098519062----xy666888----高加贵----522426198009193634\n" +
                "13158064854----xy666888----郑朝芬----522524197012191208\n" +
                "15519049461----xy666888----蔡远航----522428199505184814\n" +
                "13017458404----xy666888----张才华----520221199505051870\n" +
                "13195209974----xy666888----赵琼----522424197209281826\n" +
                "15599139010----xy666888----卢兴兰----520203199110095024\n" +
                "15585240389----xy666888----杨新厂----412729196903011410\n" +
                "15585241072----xy666888----叶华锋----332522198410105156\n" +
                "13078556681----xy666888----张和平----512921196504302759\n" +
                "13195104691----xy666888----杨艳珍----52022119850821460X\n" +
                "13158050482----xy666888----贺青飞----52242619701113716X\n" +
                "15585161713----xy666888----赵庆林----520201198511304811\n" +
                "15519136140----xy666888----赵菊----522422199005171824 "
                ;
        String[] split = s.split("\n");
        for (String s1 : split) {
            String[] split1 = s1.split("----");
            OrderAccount orderAccount = new OrderAccount(split1[0], split1[1]);
            orderAccount.setCreateDate(new Date());
            orderAccount.setStatus("1");
            orderRepository.save(orderAccount);
        }


        return ViewData.builder().build();
    }


    @GetMapping("/init")
    public Object initAddress() {
        List<OrderAccount> orderAccountList = orderRepository.findAll(Sort.by("createDate").descending());
        CountDownLatch countDownLatch = new CountDownLatch(orderAccountList.size());
        for (OrderAccount orderAccount : orderAccountList) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try {
                       Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0);
                       Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
                               .timeout(SystemConstant.TIME_OUT)
                               .ignoreContentType(true)
                                .proxy(proxyUtil.getProxy())
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
            String randomIp = getRandomIp();
            Connection.Response response = Jsoup.connect("https://mall.phicomm.com/index.php/my-orders.html")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)

                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
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

    private String getBase64(Element a, Map<String, String> cookies, Integer tryCount) {
        try {
            String payUrl = "https://mall.phicomm.com/" + a.attr("href").replace("payment", "dopayment");
            Document dc = Jsoup.connect(payUrl)
                    .timeout(80 * 1000)
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
                    .timeout(60 * 1000)
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
                    .timeout(120 * 1000)
                    .cookies(cookies)
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
            if (tryCount < 3) {
                return getBase64(a, cookies, tryCount);
            }
        }
        return "#";
    }

    @GetMapping("/fast/orders")
    public Object fastFindAllOrder() throws Exception {
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
                        Connection.Response response = getOrders(entry.getValue());
                        if(!response.body().contains("暂无")) {
                            Document document = Jsoup.parse(response.body());
                            Elements tables = document.getElementsByTag("table");
                            for (int i = 1; i < tables.size(); i++) {
                                OrderAccount order = new OrderAccount();
                                OrderAccount orderAccount = entry.getKey();
                                Element table = tables.get(i);

                                Elements as = table.getElementsByTag("a");
//                                order.setPayBase64("data:image/JPG;base64,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");
                                order.setPayBase64("#");
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
                                order.setCookie(entry.getValue().get("_SID").toString());
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

    private Map<String, String> getCookies(String username, String password, int tryCount) throws InterruptedException {
        try {
            String randomIp = getRandomIp();
            Map<String, String> pageCookies = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT)
                    .proxy(proxyUtil.getProxy())
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
                    .header("REMOTE_ADDR", randomIp)
                    .header("VIA", randomIp)
                    .execute().cookies();
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
                    .proxy(proxyUtil.getProxy())

                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
                    .header("client_ip", randomIp)
                    .header("CLIENT_IP", randomIp)
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
                    .header("VIA", randomIp)
                    .proxy(proxyUtil.getProxy())
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
                    .proxy(proxyUtil.getProxy())
                    .header("x-forward-for", randomIp)
                    .header("X-Forwarded-For", randomIp)
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
