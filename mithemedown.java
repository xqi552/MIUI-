package com.ghboke.mithemedown;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class mithemedown {
    public static String getdownurl(String value) {
        final String url = "http://thm.market.xiaomi.com/thm/download/v2/" + value;
        String Strjson = "";
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
            Request request = new Request.Builder()
                    .url(url)//请求接口。如果需要传参拼接到接口后面。
                    .build();//创建Request 对象
            Response response = null;
            response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                Strjson = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Strjson;
    }

}
