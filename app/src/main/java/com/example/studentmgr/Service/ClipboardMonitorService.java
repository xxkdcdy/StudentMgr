package com.example.studentmgr.Service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ClipboardMonitorService extends Service {
    ClipboardManager clipboardManager;
    private static String clipText = "";    //保存最近一次广播出去的信息

    public ClipboardMonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        clipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData data = clipboardManager.getPrimaryClip();
                if (data != null && data.getItemCount() > 0) {
                    ClipData.Item item = data.getItemAt(0);
                    if (item != null) {
                        CharSequence sequence = item.coerceToText(ClipboardMonitorService.this);
                        if (sequence != null && checkStuNumber(sequence.toString())) {
                            broadcast(sequence.toString());
                        }
                    }
                }
            }
        });
    }




    // 把正确的剪贴板信息广播出去
    public void broadcast(String content){
        Intent intent = new Intent();
        intent.setAction("com.example.studentmgr.ClipboardBroadcast");
        intent.putExtra("HAS_STUDENT_RECORD", content);
        sendBroadcast(intent);
    }

    // 验证是否为正确的学号格式信息
    public static boolean checkStuNumber(String s){
        //首先判断长度是否为8位
        if(s.length() != 8)
            return false;

        //然后判断前两位固定“SE”
        if(s.charAt(0) != 'S' && s.charAt(1) != 'E')
            return false;

        //判断后6位是数字
        char flag;
        for(int i = 2; i < 8; i++){
            flag = s.charAt(i);
            if(flag < '0' || flag > '9')
                return false;
        }

        return true;
    }
}
