package com.example.studentmgr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {

    private ProgressBar p;
    private int mProcessStatus = 0;
    private Handler mHandler;
    static String username;
    static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        p = findViewById(R.id.progressBar);
        p.setVisibility(View.INVISIBLE);

        //
        EditText user = findViewById(R.id.usernameEdit);
        EditText pass = findViewById(R.id.pwdEdit);


        final SharedPreferences sp = getSharedPreferences("mySetting", MODE_PRIVATE);
        username = sp.getString("username", "");
        password = sp.getString("password", "");
        if(!username.equals("")
                && !password.equals("")){
            Toast.makeText(ActivityLogin.this,"检测到保存用户...",Toast.LENGTH_SHORT).show();
            user.setText(username);
            pass.setText(password);
        }
    }

    public  void onLogin(View view){

        EditText user = findViewById(R.id.usernameEdit);
        EditText pass = findViewById(R.id.pwdEdit);

        username = user.getText().toString();
        password = pass.getText().toString();

        login();
    }

    public void login(){
        if(username.equals("xxkdcdy") && password.equals("c5352948")){
            Toast.makeText(ActivityLogin.this,"登录成功，加载中...",Toast.LENGTH_SHORT).show();

            //登录过程
            p.setVisibility(View.VISIBLE);
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    if(msg.what == 0x111){
                        p.setProgress(mProcessStatus);
                    }else{
                        Toast.makeText(ActivityLogin.this, "完成初始化!", Toast.LENGTH_SHORT).show();
                        //p.setVisibility(View.GONE);
                        startActivity(new Intent(ActivityLogin.this,ActivityPager.class));
                        ActivityLogin.this.finish();
                    }
                }
            };

            //模拟加载过程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        mProcessStatus = doWork();
                        Message m = new Message();
                        if(mProcessStatus < 100){
                            m.what = 0x111;
                            mHandler.sendMessage(m);
                        }
                        else{
                            m.what = 0x110;
                            mHandler.sendMessage(m);
                            break;
                        }
                    }
                }

                private int doWork(){
                    mProcessStatus += Math.random() * 20;
                    try{
                        Thread.sleep(200);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return mProcessStatus;
                }
            }).start();
        }
        else{
            Toast.makeText(this,"账号或密码错误",Toast.LENGTH_SHORT).show();
        }
    }
}
