package com.ghboke.mithemedown;

import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.net.URLDecoder.decode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar mProgressBar;
    private EditText mEditTextUrl;
    private Button mButtonJx;
    private EditText mEditTextDownurl;
    private Button mButtonDown;
    private String downurl;
    private boolean candown=false;
    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEditTextUrl = (EditText) findViewById(R.id.editText_url);
        mButtonJx = (Button) findViewById(R.id.button_jx);
        mButtonJx.setOnClickListener(this);
        mEditTextDownurl = (EditText) findViewById(R.id.editText_downurl);
        mButtonDown = (Button) findViewById(R.id.Button_down);
        mButtonDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.button_jx:
                jx();
                break;
            case R.id.Button_down:
                down();
                break;
        }
    }

    public void down() {
        if (candown==false){
            Toast.makeText(getApplicationContext(), "请解析成功后再使用", Toast.LENGTH_LONG).show();
            return;
        }
        filename=downurl.substring(91);
        try {
            filename=decode(filename, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file=new File(Environment.getExternalStorageDirectory() + "/MIThemeDown/"+filename);
        if (file.exists()){
            Toast.makeText(getApplicationContext(), "文件已经下载过啦", Toast.LENGTH_LONG).show();
            return;
        }
        //http://f4.market.mi-img.com/download/ThemeMarket/0ba9b4e8d3c040d2712ee9b3e9c9bdaf70a411ae1/%E6%98%A5%E6%84%8F-1.0.0.0.mtz
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downurl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(filename);
        request.setAllowedOverRoaming(false);
        request.setDescription("小米主题下载");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("/MIThemeDown/",filename);
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        Toast.makeText(getApplicationContext(), "已经开始下载", Toast.LENGTH_LONG).show();
    }

    public void jx() {
        downurl = mEditTextUrl.getText().toString();
        if (downurl.length() == 0) {

            Toast.makeText(getApplicationContext(), "请输入网址", Toast.LENGTH_LONG).show();
            return;
        }
        downurl = downurl.substring(31);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {


                downurl = mithemedown.getdownurl(downurl);
                try {
                    JSONObject json = new JSONObject(downurl);
                    JSONObject downobj = json.getJSONObject("apiData");
                    downurl = downobj.getString("downloadUrl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downurl.length()==0){
                            Toast.makeText(getApplicationContext(), "解析失败", Toast.LENGTH_LONG).show();
                            candown=false;
                            return;
                        }else {
                            Toast.makeText(getApplicationContext(), "解析成功", Toast.LENGTH_LONG).show();
                        }
                        candown=true;
                        mEditTextDownurl.setText(downurl);
                        mProgressBar.setVisibility(View.INVISIBLE
                        );
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //权限提示
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/MIThemeDown");
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                Toast.makeText(this, "创建文件夹失败，请赋予权限", Toast.LENGTH_SHORT).show();
                            }
                            ;
                        }
                    }
                    break;
                }
        }
    }


}
