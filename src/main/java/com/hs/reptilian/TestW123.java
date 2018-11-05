package com.hs.reptilian;


import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.RuoKuaiUtils;
import com.hs.reptilian.util.UserAgentUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("all")
public class TestW123 {

	private static volatile String rsbody = null;

	public static void main(String[] args) throws Exception {
		Map<String, String> cookies = getCookies("13270077763", "123321a");

		initBody(cookies);

		while (rsbody == null) {
//        	System.out.println("等待初始化中");
		}

		Document document = Jsoup.parse(rsbody);
        String cart_md5 = getCartMd5(document);
        String addr_id = getAddrId(document);
		String vcCodeUrl = "https://mall.phicomm.com" + document.getElementById("local-vcode-img").attr("onclick").split("'")[1].split("\\?")[0];
		System.out.println("cart_md5: " + cart_md5 + " addr_id: " + addr_id + " vcCodeUrl: " + vcCodeUrl);

        while(true) {
	        try {
	        	String vcCodeJson = RuoKuaiUtils.createByPost("2980364030", "li5201314", "4030", "9500", "112405", "e68297ecf19c4f418184df5b8ce1c31e",
	    	            Jsoup.connect(vcCodeUrl)
	    	            .ignoreContentType(true)
	    	            .cookies(cookies)
								.userAgent(UserAgentUtil.get())
	    	            .timeout(SystemConstant.TIME_OUT).execute().bodyAsBytes());

				System.out.println(vcCodeJson);

				Thread[] threads = new Thread[SystemConstant.TASK_COUNT];
				for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
					threads[i] = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Response createOrderResponse = Jsoup.connect("https://mall.phicomm.com/order-create-is_fastbuy.html").method(Method.POST).timeout(SystemConstant.TIME_OUT).ignoreContentType(true)
									.cookies(cookies)
									.header("X-Requested-With", "XMLHttpRequest")
									.data("cart_md5", cart_md5)
									.data("addr_id", addr_id)
									.data("dlytype_id", "1")
									.data("payapp_id", "alipay")
									.data("yougouma", "")
										.userAgent(UserAgentUtil.get())
									.data("invoice_type", "")
									.data("invoice_title", "")
									.data("useVcNum", SystemConstant.W3_VC)
									.data("need_invoice2", "on")
									.data("useDdwNum", "0")
									.data("memo", "")
									.data("vcode", JSONObject.parseObject(vcCodeJson).getString("Result"))
									.execute();

								System.err.println("==========================================================");
								System.err.println(createOrderResponse.body());
								if(createOrderResponse.body().contains("success")) {
									System.err.println("success!!!!");
									System.exit(-1);
								}
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
					});
					threads[i].start();
				}
				for (int i = 0; i < SystemConstant.TASK_COUNT; i++) {
					threads[i].join();
				}
			} catch (Exception e) {
				System.err.println("err>> " + e.getMessage());
			}
        }

    }

	private static synchronized void updateRsBody(String body) {
		if(rsbody == null) {
			rsbody = body;
			System.out.println("==============设置成功==============");
		}
	}

	private static String initBody(Map<String, String> cookies) throws Exception {
		while(rsbody == null) {
			Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String body = Jsoup.connect(SystemConstant.W3_URL).method(Method.GET)
								.userAgent(UserAgentUtil.get())
								.timeout(SystemConstant.TIME_OUT).cookies(cookies).followRedirects(true).execute().body();
						if(body.contains("库存不足,当前最多可售数量")) {
							System.err.println("库存不足 - " + new Date().toLocaleString());
						} else if(body.contains("返回商品详情") || body.contains("cart_md5")) {
				        	updateRsBody(body);
				        }
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}).start();
		}
		System.out.println("成功");
		return rsbody;
	}

	private static String getAddrId(Document document) {
		try {
            String addr_id = document.select("input[name=addr_id]").get(0).val();
            if(addr_id != null && addr_id.length() > 0) {
//                System.err.println("addr_id获取成功："  + addr_id);
            	return addr_id;
            }
        } catch (Exception e) {
            for (Element element : document.getElementsByTag("input")) {
                if(element.attr("name").equals("addr_id")) {
                	String addr_id = element.attr("value");
//                    System.err.println("addr_id获取成功："  + addr_id);
                    return addr_id;
                }
            }
        }
		throw new RuntimeException("addr_id获取失败");
	}

	private static String getCartMd5(Document document) {
		for (Element element : document.getElementsByTag("input")) {
            if(element.attr("name").equals("cart_md5")) {
                System.err.println("cart_md5获取成功："  + element.attr("value"));
                return element.attr("value");
            }
        }
		throw new RuntimeException("cart_md5获取失败");
	}

	private static boolean doCheck(String body) {
        if(body.contains("购物车为空")) {
        	System.out.println("购物车为空 - " + new Date().toLocaleString());
        	return true;
        }
        if(body.indexOf("库存不足") != -1) {
        	System.out.println("库存不足 - " + new Date().toLocaleString());
        	return true;
        }
        return false;
	}

    private static Map<String, String> getCookies(String username, String password) throws Exception {


		/*final WebClient webClient = new WebClient(BrowserVersion.CHROME);
		final HtmlPage page = webClient.getPage("https://mall.phicomm.com/passport-login.html");
		final String pageAsXml = page.asXml();
		final String pageAsText = page.asText();
		System.out.println(webClient);
		System.err.println(webClient.getCookieManager().getCookies());
		webClient.close();*/


		WebClient client = new WebClient(BrowserVersion.CHROME);
		client.setJavaScriptTimeout(5000);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setUseInsecureSSL(true);// 接受任何主机连接 无论是否有有效证书
		client.getOptions().setJavaScriptEnabled(true);// 设置支持javascript脚本
		client.getOptions().setCssEnabled(false);// 禁用css支持
		client.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时不抛出异常
		client.getOptions().setTimeout(100000);// 设置连接超时时间
		client.getOptions().setDoNotTrackEnabled(false);
		client.setAjaxController(new NicelyResynchronizingAjaxController());// 设置Ajax异步
		client.waitForBackgroundJavaScript(20000);
		HtmlPage page = (HtmlPage) client.getPage("https://mall.phicomm.com/passport-login.html");
		// String hrefValue = "javascript:add(1,1,'+');";
		// ScriptResult s = page.executeJavaScript(hrefValue);//执行js方法
		// HtmlPage hpm=(HtmlPage) s.getNewPage();//获得执行后的新page对象
		 client.setJavaScriptTimeout(30000);
		Thread.sleep(10000);
		String content = page.asXml();
		System.err.println(content);
		System.err.println(client.getCookieManager().getCookies());
//		client.close();


		Map<String, String> cookies = new HashMap<>();
		System.out.println("================================");
		for (Cookie cookie : client.getCookieManager().getCookies()) {
			System.err.println(cookie.getName() + "====" + cookie.getValue());
			cookies.put(cookie.getName(), cookie.getValue());
		}

		HtmlPage page2 = (HtmlPage) client.getPage("https://mall.phicomm.com/m/passport-login.html");
		System.out.println("================================");
		for (Cookie cookie : client.getCookieManager().getCookies()) {
			System.err.println(cookie.getName() + "====" + cookie.getValue());
			cookies.put(cookie.getName(), cookie.getValue());
		}
		System.err.println("cookies>> " + cookies);

		// ==================================================================

		Response execute2 = Jsoup.connect("https://mall.phicomm.com/m/passport-login.html")
			.userAgent(
				"Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1")
//			.header("Cookie", "Hm_lpvt_806df8bf4f865af1db1c724887359a8c=1541408852; Hm_lvt_806df8bf4f865af1db1c724887359a8c=1541407982; _SID=f7c8fb289bed4d70c4be71783ce2a4a0; CACHE_VARY=79e9a2cfad5b086e9017de76915a505c-e1fbe29478bfe3016b1e59a6be6690fa; _VMC_UID=251276aa1c62240eb6d49d3e5c2781f4; __jsl_clearance=1541407977.869|0|Bxjxe8UvUWPdrirA2Sdya9Rktu8%3D; __jsluid=5c6d1f6741536e1e959cba90b9d4a644")
			.cookies(cookies)
			.execute();
		System.out.println(execute2.body());

		Response execute1 = Jsoup.connect("https://mall.phicomm.com/m/passport-post_login.html")
			.method(Method.POST)
			.timeout(30 * 1000)
			.ignoreContentType(true)
			.cookies(cookies)
//			.header("Cookie", "Hm_lpvt_806df8bf4f865af1db1c724887359a8c=1541408738; Hm_lvt_806df8bf4f865af1db1c724887359a8c=1541407982; _SID=f7c8fb289bed4d70c4be71783ce2a4a0; CACHE_VARY=79e9a2cfad5b086e9017de76915a505c-e1fbe29478bfe3016b1e59a6be6690fa; _VMC_UID=251276aa1c62240eb6d49d3e5c2781f4; __jsl_clearance=1541407977.869|0|Bxjxe8UvUWPdrirA2Sdya9Rktu8%3D; __jsluid=5c6d1f6741536e1e959cba90b9d4a644")
			.header("Host", "mall.phicomm.com")
			.header("Accept", "*/*")
			.header("X-Requested-With", "XMLHttpRequest")
//			.header("Accept-Language", "zh-cn")
//			.header("Accept-Encoding", "br, gzip, deflate")
			.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
			.header("Set-jsonstorage", "jsonstorage")
			.header("Origin", "https://mall.phicomm.com")
			.header("User-Agent",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1")
			.header("Referer", "https://mall.phicomm.com/m/passport-login.html")
//			.header("DNT", "1")
//			.header("X-CSRF-TOKEN", "")
//			.header("Connection", "keep-alive")
			.data("uname", "13655544444")
			.data("password", "12312313213")
			.data("forward", "")
			.execute();
		System.out.println(execute1.body());

		// ==================================================================

//		client.getPage(new WebRequest(new URL("https://mall.phicomm.com/m/passport-post_login.html"), HttpMethod.POST))



		Response execute = Jsoup.connect("https://mall.phicomm.com/m/passport-login.html")
			.header("Host", "mall.phicomm.com")
			.header("Accept-Encoding", "br, gzip, deflate")
//			.header("Cookie", "__jsl_clearance=1541404944.31|0|6RBarko%2BByK0lW%2FOLZCVF6HRijQ%3D; __jsluid=adbc91e375383fd293dace46c1aa0108")
			.header("Cookie", "__jsl_clearance=" + client.getCookieManager().getCookie("__jsl_clearance").getValue() + "; __jsluid=" + client.getCookieManager().getCookie("__jsluid").getValue())
			.header("Connection", "keep-alive")
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("User-Agent",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1")
			.header("Accept-Language", "zh-cn")
			.header("Referer", "https://mall.phicomm.com/m/passport-login.html")
			.header("DNT", "1")
			.timeout(10 * 1000)
			.execute();

//		Map<String, String> cookies = execute.cookies();
		System.err.println(execute.body());

		cookies.put("__jsluid", "033669c3fbed148d3d326d9b43bdfd2d");
		cookies.put("__jsl_clearance", "1541384192.07|0|DJwCT7xACp7jGrUddQgi%2FRuyZew%3D");


        Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Method.POST)
            .cookies(cookies)
            .timeout(SystemConstant.TIME_OUT)
            .ignoreContentType(true)
			.userAgent(UserAgentUtil.get())
            .header("X-Requested-With", "XMLHttpRequest")
            .data("forward", "")
            .data("uname", username)
            .data("password", password)
            .execute();


        if(loginResponse.body().contains("error")) {
            throw new RuntimeException("帐号或密码不正确");
        }

        Map<String, String> cks = new HashMap<>();
        cks.putAll(cookies);
        cks.putAll(loginResponse.cookies());
        return cks;
    }

}
