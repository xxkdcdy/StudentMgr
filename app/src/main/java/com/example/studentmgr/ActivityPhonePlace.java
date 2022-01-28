package com.example.studentmgr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmgr.settings.Mobile;

public class ActivityPhonePlace extends AppCompatActivity {
    private static String phonePlace = null;
    private static TextView phonePlaceResult;
    private static EditText keywordInput;

    //定义一个Handler用来更新页面：
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    phonePlaceResult.setText("结果显示：\n" + phonePlace);
                    Toast.makeText(ActivityPhonePlace.this, "号码归属地查询成功,内容复制到剪切板", Toast.LENGTH_SHORT).show();
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("phonePlace", phonePlace);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    break;
                case 0x002:
                    phonePlaceResult.setText("结果显示：\n" + phonePlace);
                    Toast.makeText(ActivityPhonePlace.this, "号码归属地查询失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_place);
        phonePlaceResult = findViewById(R.id.textPhonePlace);
        keywordInput = findViewById(R.id.InternetKeywordEdit);
    }

    public void onSearchPhonePlace(View view){
        //网络请求不能放在主线程中
        new Thread(new Runnable(){
            @Override
            public void run() {
                Mobile mobile = new Mobile();
                String input = "15706175232";
                if(!keywordInput.getText().toString().equals("") && keywordInput.getText().toString() != null)
                    input = keywordInput.getText().toString();
                phonePlace = mobile.getMobileNoTrack(input);
                if(phonePlace != null) {
                    handler.sendEmptyMessage(0x001);
                    System.out.println(phonePlace);
                }
                else
                    handler.sendEmptyMessage(0x002);
            }
        }).start();
    }

    public void onClearText(View view){
        keywordInput.setText("");
    }

    public void onCopy(View view){
        if(phonePlace != null) {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("phonePlace", phonePlace);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(ActivityPhonePlace.this, "信息成功复制到剪贴板", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ActivityPhonePlace.this, "尚未获取信息", Toast.LENGTH_SHORT).show();
        }
    }
}
