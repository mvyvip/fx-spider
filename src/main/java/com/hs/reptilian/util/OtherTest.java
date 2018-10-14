package com.hs.reptilian.util;

import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.feifei.FeiFeiUtil;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;

public class OtherTest {
    public static volatile int i = 0;


    public static void main(String[] args) throws Exception {
//         int i = 0;
        for (int j = 0; j < 10; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = Jsoup.connect("https://mall.phicomm.com/vcode-index-passport6340322.html")
                                .ignoreContentType(true)
                                .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes();

                        long start = System.currentTimeMillis();
                        System.out.println("startL " + start);
                        String validate = FeiFeiUtil.validate(bytes);
//                        String validate = RuoKuaiUtils.createByPost("2980364030", "li5201314", "4030", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e", bytes);
                        System.out.println((System.currentTimeMillis() - start) + "---> " + validate);

//                        String code = RuoKuaiUtils.createByPost("2980364030", "li5201314", "4030", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e", bytes);


                        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/img/" + ++i + ".png" ));
                        IOUtils.write(bytes, fileOutputStream);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


}
