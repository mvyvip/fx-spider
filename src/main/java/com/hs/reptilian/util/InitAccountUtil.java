package com.hs.reptilian.util;

import com.alibaba.fastjson.JSON;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.Account;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.repository.AccountRepository;
import com.hs.reptilian.repository.OrderAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InitAccountUtil {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderAccountRepository orderAccountRepository;

    @Autowired
    private ProxyUtil proxyUtil;


    private AtomicInteger atomicInteger = new AtomicInteger(0);


    /**
     *
     */
    public void init() throws Exception {
        List<OrderAccount> orderRepositoryByStatus = orderAccountRepository.findByStatus("1");
        log.info("本次需要初始化：[{}], 条数据");
        for (OrderAccount orderAccount : orderRepositoryByStatus) {
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
        }

    }

    private void setAccountCookie(OrderAccount orderAccount) throws Exception {
        try {
            String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            Connection.Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(org.jsoup.Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E302 VMCHybirdAPP-iOS/2.2.4/")
                    .proxy(proxyUtil.getProxy())
                    .userAgent(UserAgentUtil.get())
                    .header("X-WxappStorage-SID", id)
                    .timeout(SystemConstant.TIME_OUT).execute();

            Connection.Response loginResponse = Jsoup.connect("https://mall.phicomm.com/m/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                    .timeout(SystemConstant.TIME_OUT)
                    .cookie("_SID", id)
                    .userAgent(UserAgentUtil.get())
                    .ignoreContentType(true)
                    .proxy(proxyUtil.getProxy())
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
            cks.putAll(loginResponse.cookies());

            com.hs.reptilian.model.Account account = new Account();
            account.setCreateDate(new Date());
            account.setMemberIdent(loginResponse.cookie("MEMBER_IDENT"));
            account.setMobile(orderAccount.getPhone());
            account.setPassword(orderAccount.getPassword());
            account.setRemark(orderAccount.getRemark());
            account.setSid(id);
            account.setStatus(1);
            accountRepository.save(account);

            log.info(">>>>>>> " + atomicInteger.decrementAndGet());

        }catch (Exception e){
            log.error("提前登录失败：{}----{}-----{}" + orderAccount.getPhone(), orderAccount.getPassword(), e.getMessage());
            setAccountCookie(orderAccount);
        }
    }


}
