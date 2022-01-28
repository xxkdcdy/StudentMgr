package com.example.studentmgr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAds extends Activity {
    private Handler aHandler;
    static int count = 5;
    static boolean is_login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ads);
        is_login = false;
        final TextView countText = findViewById(R.id.countText);
        count = 5;

        aHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0x111){
                    String str = Integer.toString(count);
                    countText.setText(str + "秒后结束广告  点击跳过");
                    count--;
                }else{
                    if(!is_login)
                        startActivity(new Intent(ActivityAds.this,ActivityLogin.class));
                    is_login = true;
                    ActivityAds.this.finish();
                }
            }
        };

        //倒计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message m;
                for(int i = 0; i < 5; i++){
                    m = new Message();
                    m.what = 0x111;
                    aHandler.sendMessage(m);
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                m = new Message();
                m.what = 0x110;
                aHandler.sendMessage(m);
            }
        }).start();
    }

    //跳过等待
    public void onSkip(View view){
        if(!is_login)
            startActivity(new Intent(ActivityAds.this,ActivityLogin.class));
        is_login = true;
        ActivityAds.this.finish();
    }
}
