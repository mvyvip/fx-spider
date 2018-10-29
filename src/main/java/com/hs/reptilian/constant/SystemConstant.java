package com.hs.reptilian.constant;

import java.util.Arrays;
import java.util.List;

public interface SystemConstant {

    List<Integer> UPDATE_CODE_SECONDS = Arrays.asList(52);

    int TASK_COUNT = 1;

    int TIME_OUT = 30000;

    int THREAD_WAIT_TIME = 350;

    int SIZE = 70;

    int ONE_IP_COUNT = 100;

//    int ALL_IP_COUNT = 500;


    String K2_VC = "16000";
    String T1_VC = "29900";
    String S7_VC = "23900";
    String W3_VC = "0";
    String W1_VC = "23900";


    String S7_URL = "https://mall.phicomm.com/cart-fastbuy-12-1.html";
    String K2_URL = "https://mall.phicomm.com/cart-fastbuy-5-1.html";
    String T1_URL = "https://mall.phicomm.com/cart-fastbuy-13-1.html";
    String W3_URL = "https://mall.phicomm.com/cart-fastbuy-197-1.html";
    String W1_URL = "https://mall.phicomm.com/cart-fastbuy-14-1.html";

    String IP_URL = "http://h.wandouip.com/get/ip-list?pack=0&num=" + ONE_IP_COUNT + "&xy=2&type=2&lb=\\r\\n&mr=2&app_key=";

    String GOODS_URL = "https://mall.phicomm.com/cart-fastbuy-{0}-1.html";

    //    String URL = W1_URL;
//    String VC = W1_VC;
//    String DESC = "W1";

}
