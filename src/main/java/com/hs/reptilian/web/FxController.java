package com.hs.reptilian.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.model.TaskList;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.repository.TaskListRepository;
import com.hs.reptilian.util.CookieUtils;
import com.hs.reptilian.util.ProxyUtil;
import com.hs.reptilian.util.ViewData;
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
                        Map<String, String> cookies = getCookies(orderAccount.getPhone(), orderAccount.getPassword(), 0);
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

    private Map<String, String> getCookies(String username, String password, int tryCount) {
        try {
            Map<String, String> pageCookies = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT)
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
                    .data("uname", username)
                    .data("password", password)
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
            log.error(e.getMessage());
            if (tryCount < 40) {
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


//					.data("maddr[name]", "冷先生")
//					.data("maddr[mobile]", "15879299250")
//					.data("maddr[area]", "mainland:江西省/九江市/武宁县:1407")
//					.data("maddr[addr]", "江西省九江市武宁县小康路19号-诗人")
//					.data("maddr[is_default]", "true")
                    .execute();
            log.info(orderAccount + "---设置默认地址成功");
        } catch (Exception e) {
            setAddr(cookies, orderAccount);
        }
    }

}
