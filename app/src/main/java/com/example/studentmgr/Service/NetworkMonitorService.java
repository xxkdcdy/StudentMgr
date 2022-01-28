package com.example.studentmgr.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkMonitorService extends Service {
    public NetworkMonitorService() {
        System.out.println("network cons");
    }
    private Binder binder = new MyBinder();
    int IntentId;
    int NOINTENT = 0;
    int WIFI = 1;
    int GRS = 2;


    // 实时监听网络状态改变
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                Timer timer = new Timer();
                timer.schedule(new QunXTask(getApplicationContext()), new Date());
            }
        }
    };

    public interface GetConnectState
    {
        void GetState(int isConnected); // 网络状态改变之后，通过此接口的实例通知当前网络的状态，此接口在Activity中注入实例对象
    }

    private GetConnectState onGetConnectState;

    public void setOnGetConnectState(GetConnectState onGetConnectState)
    {
        this.onGetConnectState = onGetConnectState;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public void onCreate()
    {// 注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 添加接收网络连接状态改变的Action
        registerReceiver(mReceiver, mFilter);
    }

    class QunXTask extends TimerTask
    {
        private Context context;

        public QunXTask(Context context)
        {
            this.context = context;
        }

        @Override
        public void run()
        {
            if(isNetworkConnected(context)){
                if (isWifiConnected(context)) {
                    IntentId = WIFI;
                } else if (isMobileConnected(context)) {
                    IntentId = GRS;
                } else {
                    IntentId = NOINTENT;
                }
            }
            else {
                IntentId = NOINTENT;
            }


                if (onGetConnectState != null) {
                    onGetConnectState.GetState(IntentId); // 通知网络状态改变
                    Log.i("mylog", "通知网络状态改变:" + IntentId);
                }
        }

        /*
         * 判断是否有网络连接
         */
        public boolean isNetworkConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isConnected();
                }
            }
            return false;
        }
        /*
         * 判断是否有移动网络连接
         */
        public boolean isMobileConnected(Context context) {
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return null != networkInfo && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
            return false;
        }

        /*
         * 判断是否有wifi连接
         */
        public boolean isWifiConnected(Context context) {
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return null != networkInfo && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            }
            return false;
        }
    }

    public class MyBinder extends Binder
    {
        public NetworkMonitorService getService()
        {
            return NetworkMonitorService.this;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver); // 删除广播
    }
}
