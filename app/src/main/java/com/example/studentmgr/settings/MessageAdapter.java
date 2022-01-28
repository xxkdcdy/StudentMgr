package com.example.studentmgr.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentmgr.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<stuMessage> {
    private int resourceId;
    SharedPreferences sp;
    String foneSize;

    // 适配器的构造函数，把要适配的数据传入这里
    public MessageAdapter(Context context, int textViewResourceId, List<stuMessage> objects, SharedPreferences sp){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
        this.sp = sp;
    }

    // convertView 参数用于将之前加载好的布局进行缓存
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        stuMessage stuMessage =getItem(position); //获取当前项的Message实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewHolder viewHolder;
        if (convertView==null || !sp.getString("fontSize", "").equals(foneSize)){

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder=new ViewHolder();
            viewHolder.MessageImage=view.findViewById(R.id.img);
            viewHolder.stuName=view.findViewById(R.id.name);
            viewHolder.stuNumber = view.findViewById(R.id.number);
            viewHolder.stuSex=view.findViewById(R.id.sex);
            viewHolder.stuSchool=view.findViewById(R.id.school);
            viewHolder.stuMajor=view.findViewById(R.id.magor);
            viewHolder.stuHobby=view.findViewById(R.id.hobby);
            viewHolder.stuBirth=view.findViewById(R.id.birth);
            viewHolder.layout=view.findViewById(R.id.listViewLayout);

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }

        foneSize = sp.getString("fontSize", "");
        viewHolder.configTextSize();

        // 获取控件实例，并调用set...方法使其显示出来
        viewHolder.MessageImage.setImageResource(R.drawable.ic_launcher_foreground);
        viewHolder.stuName.setText("姓名：  " + stuMessage.getName());
        viewHolder.stuNumber.setText("学号：  " + stuMessage.getNumber());
        viewHolder.stuSex.setText("性别：  " + stuMessage.getSex());
        viewHolder.stuSchool.setText("学院：  " + stuMessage.getSchool());
        viewHolder.stuMajor.setText("专业：  " + stuMessage.getMajor());
        viewHolder.stuHobby.setText("爱好：  " + stuMessage.getHobby());
        viewHolder.stuBirth.setText("生日：  " + stuMessage.getBirth());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder{
        ImageView MessageImage;
        TextView stuName;
        TextView stuNumber;
        TextView stuSex;
        TextView stuSchool;
        TextView stuMajor;
        TextView stuHobby;
        TextView stuBirth;
        LinearLayout layout;

        public void configTextSize(){
            String fontSize = sp.getString("fontSize", "");
            LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)layout.getLayoutParams();
            if(fontSize.equals("标准") || fontSize.equals("较小")){
                setTextSize(20);
                lp.height = 500;
            }
            else if(fontSize.equals("较大")){
                setTextSize(30);
                lp.height = 650;
            }
            else{}
        }
        public void setTextSize(int size){
            stuName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuSex.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuSchool.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuMajor.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuHobby.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
            stuBirth.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        }
    }
}