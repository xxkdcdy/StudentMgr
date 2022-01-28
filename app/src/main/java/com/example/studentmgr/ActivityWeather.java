package com.example.studentmgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmgr.settings.Weather;

import java.lang.reflect.Field;

public class ActivityWeather extends AppCompatActivity {

    int UPDATE_SETTINGS = 0x111;
    Thread worker;
    TextView todayinfo1_updateTime,todayinfo1_humidity,todayinfo1_temperature,todayinfo1_wind,todayinfo1_uv;
    TextView dayinfo1_daytime,dayinfo1_temperature,dayinfo1_wind,
            dayinfo2_daytime,dayinfo2_temperature,dayinfo2_wind,
            dayinfo3_daytime,dayinfo3_temperature,dayinfo3_wind,
            dayinfo4_daytime,dayinfo4_temperature,dayinfo4_wind,
            dayinfo5_daytime,dayinfo5_temperature,dayinfo5_wind;
    ImageView dayinfo1_weatherStatusImg1,dayinfo1_weatherStatusImg2,
            dayinfo2_weatherStatusImg1,dayinfo2_weatherStatusImg2,
            dayinfo3_weatherStatusImg1,dayinfo3_weatherStatusImg2,
            dayinfo4_weatherStatusImg1,dayinfo4_weatherStatusImg2,
            dayinfo5_weatherStatusImg1,dayinfo5_weatherStatusImg2;
    String result = "";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_SETTINGS) {
                Toast.makeText(ActivityWeather.this, "获取天气信息成功", Toast.LENGTH_SHORT).show();
                update();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initSettings();
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Weather weather = new Weather();
                result = weather.getWeather("镇江");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(result);
                System.out.println(worker.getName());
                if(!result.equals("")) {
                    Message msg = handler.obtainMessage();
                    msg.what = UPDATE_SETTINGS;
                    handler.sendMessage(msg);
                }
            }
        });
        worker.start();
        Toast.makeText(ActivityWeather.this, "正在获取天气信息...", Toast.LENGTH_SHORT).show();
    }

    private void initSettings(){
        todayinfo1_updateTime = findViewById(R.id.todayinfo1_updateTime);
        todayinfo1_humidity = findViewById(R.id.todayinfo1_humidity);
        todayinfo1_temperature = findViewById(R.id.todayinfo1_temperature);
        todayinfo1_wind = findViewById(R.id.todayinfo1_wind);
        todayinfo1_uv = findViewById(R.id.todayinfo1_uv);

        dayinfo1_daytime = findViewById(R.id.dayinfo1_daytime);
        dayinfo1_temperature = findViewById(R.id.dayinfo1_temperature);
        dayinfo1_wind = findViewById(R.id.dayinfo1_wind);
        dayinfo1_weatherStatusImg1 = findViewById(R.id.dayinfo1_weatherStatusImg1);
        dayinfo1_weatherStatusImg2 = findViewById(R.id.dayinfo1_weatherStatusImg2);

        dayinfo2_daytime = findViewById(R.id.dayinfo2_daytime);
        dayinfo2_temperature = findViewById(R.id.dayinfo2_temperature);
        dayinfo2_wind = findViewById(R.id.dayinfo2_wind);
        dayinfo2_weatherStatusImg1 = findViewById(R.id.dayinfo2_weatherStatusImg1);
        dayinfo2_weatherStatusImg2 = findViewById(R.id.dayinfo2_weatherStatusImg2);

        dayinfo3_daytime = findViewById(R.id.dayinfo3_daytime);
        dayinfo3_temperature = findViewById(R.id.dayinfo3_temperature);
        dayinfo3_wind = findViewById(R.id.dayinfo3_wind);
        dayinfo3_weatherStatusImg1 = findViewById(R.id.dayinfo3_weatherStatusImg1);
        dayinfo3_weatherStatusImg2 = findViewById(R.id.dayinfo3_weatherStatusImg2);

        dayinfo4_daytime = findViewById(R.id.dayinfo4_daytime);
        dayinfo4_temperature = findViewById(R.id.dayinfo4_temperature);
        dayinfo4_wind = findViewById(R.id.dayinfo4_wind);
        dayinfo4_weatherStatusImg1 = findViewById(R.id.dayinfo4_weatherStatusImg1);
        dayinfo4_weatherStatusImg2 = findViewById(R.id.dayinfo4_weatherStatusImg2);

        dayinfo5_daytime = findViewById(R.id.dayinfo5_daytime);
        dayinfo5_temperature = findViewById(R.id.dayinfo5_temperature);
        dayinfo5_wind = findViewById(R.id.dayinfo5_wind);
        dayinfo5_weatherStatusImg1 = findViewById(R.id.dayinfo5_weatherStatusImg1);
        dayinfo5_weatherStatusImg2 = findViewById(R.id.dayinfo5_weatherStatusImg2);
    }

    private void update(){
        if(!result.equals("")){
            String[] splits = result.split("#");
            todayinfo1_updateTime.setText(splits[3]);
            todayinfo1_uv.setText(splits[5]);
            String[] realtime = splits[4].split("；");
            todayinfo1_temperature.setText(realtime[0].substring(realtime[0].indexOf("气温")));
            todayinfo1_wind.setText(realtime[1]);
            todayinfo1_humidity.setText(realtime[2]);

            //后面是5日天气预报
            dayinfo1_daytime.setText(splits[7]);
            dayinfo1_temperature.setText(splits[8]);
            dayinfo1_wind.setText(splits[9]);
            dayinfo1_weatherStatusImg1.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[10].substring(0,splits[10].indexOf(".gif")))));
            dayinfo1_weatherStatusImg2.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[11].substring(0,splits[11].indexOf(".gif")))));

            dayinfo2_daytime.setText(splits[12]);
            dayinfo2_temperature.setText(splits[13]);
            dayinfo2_wind.setText(splits[14]);
            dayinfo2_weatherStatusImg1.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[15].substring(0,splits[15].indexOf(".gif")))));
            dayinfo2_weatherStatusImg2.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[16].substring(0,splits[16].indexOf(".gif")))));

            dayinfo3_daytime.setText(splits[17]);
            dayinfo3_temperature.setText(splits[18]);
            dayinfo3_wind.setText(splits[19]);
            dayinfo3_weatherStatusImg1.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[20].substring(0,splits[20].indexOf(".gif")))));
            dayinfo3_weatherStatusImg2.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[21].substring(0,splits[21].indexOf(".gif")))));

            dayinfo4_daytime.setText(splits[22]);
            dayinfo4_temperature.setText(splits[23]);
            dayinfo4_wind.setText(splits[24]);
            dayinfo4_weatherStatusImg1.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[25].substring(0,splits[25].indexOf(".gif")))));
            dayinfo4_weatherStatusImg2.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[26].substring(0,splits[26].indexOf(".gif")))));

            dayinfo5_daytime.setText(splits[27]);
            dayinfo5_temperature.setText(splits[28]);
            dayinfo5_wind.setText(splits[29]);
            dayinfo5_weatherStatusImg1.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[30].substring(0,splits[30].indexOf(".gif")))));
            dayinfo5_weatherStatusImg2.setImageDrawable(getDrawable(getResourceByReflect("b_" +
                    splits[30].substring(0,splits[31].indexOf(".gif")))));
        }
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     * @param imageName
     * @return
     */
    public static int getResourceByReflect(String imageName){
        Class drawable  =  R.drawable.class;
        Field field = null;
        int r_id ;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.drawable.b_nothing;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

    public void onUpdate(View v){
        Toast.makeText(ActivityWeather.this, "正在获取天气信息...", Toast.LENGTH_SHORT).show();
        update();
    }

}
