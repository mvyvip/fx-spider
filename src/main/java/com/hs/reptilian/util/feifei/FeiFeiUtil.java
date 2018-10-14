package com.hs.reptilian.util.feifei;

public class FeiFeiUtil {

    public static Api api = new Api();

    public static String app_id = "307427";
    public static String app_key = "jn/uP+/nGI2P7t7a+uuX3zUhD0L9nrFg";
    public static String pd_id = "107427";
    public static String pd_key = "WwLgR3rJkHRd2uYBY/vgvnIEbRbArlxL";

    public static String validate(byte[] img_data) throws Exception {
        api.Init(app_id, app_key, pd_id, pd_key);
        String pred_type = "40300";
        return api.PredictExtend(pred_type, img_data);
    }

}
