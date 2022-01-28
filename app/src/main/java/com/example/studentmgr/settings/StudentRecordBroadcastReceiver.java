package com.example.studentmgr.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StudentRecordBroadcastReceiver extends BroadcastReceiver {
    private static final String action = "com.example.studentmgr.ClipboardBroadcast";
    private Cliper cliper;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(action)){
            Toast.makeText(context, "接收到广播消息：" + intent.getStringExtra("HAS_STUDENT_RECORD"), Toast.LENGTH_SHORT).show();
            cliper.clip(intent.getStringExtra("HAS_STUDENT_RECORD"));
        }
    }

    public interface Cliper{        //接口定义
        void clip(String s);
    }

    public void setCliper(Cliper c)
    {
        this.cliper = c;
    }

}
