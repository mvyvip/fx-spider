package com.hs.reptilian.task;

import com.hs.reptilian.constant.SystemConfigConstant;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.model.SystemConfig;
import com.hs.reptilian.model.TaskList;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.repository.SystemConfigRepository;
import com.hs.reptilian.repository.TaskListRepository;
import com.hs.reptilian.task.runnable.SpliderRunnable;
import com.hs.reptilian.util.ProxyUtil;
import java.security.acl.LastOwnerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ReptilianTask {

    @Autowired
    private OrderAccountRepository orderAccountRepository;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ProxyUtil proxyUtil;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Value("${start}")
    private Integer start;

    @PostConstruct
    public void start() throws Exception {
        startTask();
    }

    private void startTask() throws InterruptedException {
        String[] values = systemConfigRepository.findByKey(SystemConfigConstant.GOODS_URL).getValue().split("-");
        String goods = values[0];
        String goodsUrl = MessageFormat.format(SystemConstant.GOODS_URL, values[1]);
        String vc = values[2];

        log.info("===========================================================================");
        log.info("今日抢购:{}, vc:{}, url:{}", goods, vc, goodsUrl);
        log.info("===========================================================================");

        Thread.sleep(1000 * 12);
        for (int i = 0; i < 3; i++) {
            proxyUtil.initIps();
            Thread.sleep(10 * 1000);
        }
        
        Thread.sleep(5 * 1000);

        List<OrderAccount> accounts = orderAccountRepository.findByStatus("1");
        if((start + SystemConstant.SIZE) > accounts.size()) {
            start = accounts.size() - SystemConstant.SIZE;
        }
        List<Integer> updateCodeSeconds = SystemConstant.UPDATE_CODE_SECONDS;
        log.info("参与总数：{}, 开始下标：{}, 结束下标：{}", accounts.size(), start, start + SystemConstant.SIZE);
        for (int i = start; i < start + SystemConstant.SIZE; i++) {
            OrderAccount account = accounts.get(i);
            try {
                for (Integer updateCodeSecond : updateCodeSeconds) {
                        new Thread(new SpliderRunnable(account.getPhone(), account.getPassword(), proxyUtil, updateCodeSecond, goods, goodsUrl, vc)).start();
                }
            } catch (Exception e){
                e.printStackTrace();
                log.info("初始化抢购失败：" + e.fillInStackTrace());
            }
        }

 /*       accounts.clear();
        OrderAccount oc = new OrderAccount();
        oc.setPhone("13282083462");
        oc.setPassword("li5201314");
        accounts.add(oc);


        OrderAccount oc2 = new OrderAccount();
        oc2.setPhone("13843273254");
        oc2.setPassword("li5201314");
        accounts.add(oc2);


        OrderAccount oc3 = new OrderAccount();
        oc3.setPhone("13944599642");
        oc3.setPassword("li5201314");
        accounts.add(oc3);*/

       /* List<Integer> updateCodeSeconds = SystemConstant.UPDATE_CODE_SECONDS;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (OrderAccount account : accounts) {
                    try {
                        if(StringUtils.isNotEmpty(account.getStatus()) && account.getStatus().equals("2")) {
                            log.info("被禁用:[{}]", account);
                        } else {
                            for (Integer updateCodeSecond : updateCodeSeconds) {
                                new Thread(new SpliderRunnable(account.getPhone(), account.getPassword(), proxyUtil, updateCodeSecond, goods, goodsUrl, vc)).start();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        log.info("初始化抢购失败：" + e.fillInStackTrace());
                    }
                }
            }
        }).start();*/
    }

}
