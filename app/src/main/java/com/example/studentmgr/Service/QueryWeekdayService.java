package com.example.studentmgr.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.studentmgr.AIDL.IMyService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryWeekdayService extends Service {

    public QueryWeekdayService(){
        System.out.println("constructor");
    }
    private Binder binder = new MyBinder();
    public class MyBinder extends IMyService.Stub
    {
        public QueryWeekdayService getService()
        {
            return QueryWeekdayService.this;
        }

        public String QueryWeekday(int year, int month, int day){
            Date date = new Date();
            String strDate = year + "-" + month + "-" + day;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date =  dateFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String week = sdf.format(date);
            return week;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

}
