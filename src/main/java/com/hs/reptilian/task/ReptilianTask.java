package com.hs.reptilian.task;

import com.hs.reptilian.constant.SystemConfigConstant;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.model.OrderAccount;
import com.hs.reptilian.model.SystemConfig;
import com.hs.reptilian.repository.OrderAccountRepository;
import com.hs.reptilian.repository.SystemConfigRepository;
import com.hs.reptilian.task.runnable.SpliderRunnable;
import com.hs.reptilian.util.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
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

    @PostConstruct
    public void start() throws Exception {
        proxyUtil.initIps();
        startTask();
    }

    private void startTask() throws Exception {
        log.info("项目初始化中-----------------------------");
        List<OrderAccount> accounts = orderAccountRepository.findAll();

//        accounts.clear();
//        OrderAccount oc = new OrderAccount();
//        oc.setPhone("13694354587");
//        oc.setPassword("li5201314");
//        accounts.add(oc);

//        systemConfigRepository.findByKey(SystemConfigConstant.GOODS_URL).getValue();
        List<Integer> updateCodeSeconds = SystemConstant.UPDATE_CODE_SECONDS;

        for (OrderAccount account : accounts) {
            for (Integer updateCodeSecond : updateCodeSeconds) {
                taskExecutor.execute(new SpliderRunnable(account.getPhone(), account.getPassword(), proxyUtil, updateCodeSecond));
            }
        }

    }




}
