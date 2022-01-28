package com.example.studentmgr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmgr.DB.StudentDAL;
import com.example.studentmgr.Service.ClipboardMonitorService;
import com.example.studentmgr.Service.NetworkMonitorService;
import com.example.studentmgr.Service.QueryWeekdayService;
import com.example.studentmgr.settings.MessageAdapter;
import com.example.studentmgr.settings.StudentRecordBroadcastReceiver;
import com.example.studentmgr.settings.stuMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityPager extends AppCompatActivity implements FragmentMain.OnFragmentInteractionListener,
        FragmentStudent.OnFragmentInteractionListener, FragmentMain.CallBackListener, StudentRecordBroadcastReceiver.Cliper{
    private View viewMain,viewStu;
    private FragmentMain actMain;
    static FragmentStudent actStu;
    static List<Fragment> fragmentList;
    static ViewPager viewPager;
    static ListView listView;
    static ScrollView scrollView;
    private boolean is_ORIENTATION_lock;
    SharedPreferences sp;
    StringBuffer date;
    private StudentDAL studentDAL;
    static CharSequence title;

    String[] schools = new String[] {"请选择学院","计算机学院","电气学院"};
    String[] majorCS = new String[] {"请选择专业","软件工程","信息安全","物联网工程"};
    String[] majorDQ = new String[] {"请选择专业","电气工程","电机工程"};
    ArrayAdapter<String> majorAdapter;
    ArrayAdapter<String> collegeAdapter;

    NetworkMonitorService receiveMsgService;
    int IntentID = 666;   //初始默认获取一次网络状态
    ServiceConnection sc;
    Intent networkServiceIntent, clipServiceIntent;
    boolean state;

    StudentRecordBroadcastReceiver receiver;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //接口实现方法，用于回调FragmentMain类中定义的修改文本的方法
    @Override
    public void setText(stuMessage msg) {
        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText numberEdit = findViewById(R.id.stuNumberEdit);
        RadioButton man = findViewById(R.id.radioButtonM);
        RadioButton woman = findViewById(R.id.radioButtonF);
        Spinner spinner1 = findViewById(R.id.spinnerSchool);
        final Spinner spinner2 = findViewById(R.id.spinnerMajor);
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        CheckBox check3 = findViewById(R.id.check3);
        CheckBox check4 = findViewById(R.id.check4);
        EditText birth = findViewById(R.id.birthEdit);
        EditText signature = findViewById(R.id.signatureEdit);

        //修改标题信息
        TextView tvTitle = findViewById(R.id.textViewStudentTitle);
        tvTitle.setText("编辑信息");

        //生日和个性签名
        signature.setText(msg.getSignature());
        birth.setText(msg.getBirth());
        nameEdit.setText(msg.getName());
        numberEdit.setText(msg.getNumber());
        if(msg.getSex().equals("男"))
            man.setChecked(true);
        else
            woman.setChecked(true);
        String editMajor = msg.getMajor();


        spinner1.setOnItemSelectedListener(null);
        //下拉框初始化设置
        //绑定适配器和值
        collegeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, schools);
        spinner1.setAdapter(collegeAdapter);

        if(msg.getSchool().equals("计算机学院")){
            majorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorCS);
            spinner2.setAdapter(majorAdapter);

            spinner1.setSelection(1,true);
            if(editMajor.equals("软件工程")) {
                spinner2.setSelection(1,true);
            }
            else if(editMajor.equals("信息安全"))
                spinner2.setSelection(2,true);
            else if(editMajor.equals("物联网工程"))
                spinner2.setSelection(3,true);
        }
        else if(msg.getSchool().equals("电气学院")) {
            //绑定适配器和值
            majorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorDQ);
            spinner2.setAdapter(majorAdapter);

            spinner1.setSelection(2, true);
            if (editMajor.equals("电气工程"))
                spinner2.setSelection(1, true);
            else if (editMajor.equals("电机工程"))
                spinner2.setSelection(2, true);
        }

        //设置列表项选中监听
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选中项的值
               String  school = adapterView.getItemAtPosition(i).toString();
                //根据选中的不同的值绑定不同的适配器
                if (school.equals("计算机学院")) {
                    majorAdapter = new ArrayAdapter<String>(actStu.getActivity(), android.R.layout.simple_spinner_item, majorCS);
                    spinner2.setAdapter(majorAdapter);
                } else if (school.equals("电气学院")) {
                    majorAdapter = new ArrayAdapter<String>(actStu.getActivity(), android.R.layout.simple_spinner_item, majorDQ);
                    spinner2.setAdapter(majorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //爱好
        String editHobby = msg.getHobby();
        if(editHobby.indexOf("文学") != -1)
            check1.setChecked(true);
        if(editHobby.indexOf("体育") != -1)
            check2.setChecked(true);
        if(editHobby.indexOf("音乐") != -1)
            check3.setChecked(true);
        if(editHobby.indexOf("美术") != -1)
            check4.setChecked(true);
    }

    //构建方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        viewPager = findViewById(R.id.viewPager);

        actMain = new FragmentMain();
        actStu = new FragmentStudent();
        fragmentList = new ArrayList<>();
        fragmentList.add(actMain);
        fragmentList.add(actStu);
        sp = getSharedPreferences("mySetting", MODE_PRIVATE);
        studentDAL = new StudentDAL(this, 0);

        //判断是否锁定横竖屏
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //创建一个适配器

        /**
         * 创建适配器
         */
        class ViewPagerAdapter extends FragmentPagerAdapter {

            public ViewPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            //获取Fragment
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            //显示的个数
            @Override
            public int getCount() {
                return fragmentList.size();

            }
        }


        // adapter获取Fragment管理器
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // viewpager设置adapter
        viewPager.setAdapter(mAdapter);

        initScroll();

        //网络状态服务注册
        title = getTitle();
        sc=new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                System.out.println("network onServiceConnected " + receiveMsgService);
                receiveMsgService = ((NetworkMonitorService.MyBinder) service)
                        .getService();
                receiveMsgService.setOnGetConnectState(new NetworkMonitorService.GetConnectState() { // 添加接口实例获取连接状态
                    @Override
                    public void GetState(int id) {
                        if (IntentID != id) { // 如果当前连接状态与广播服务返回的状态不同才进行通知显示
                            IntentID = id;
                            if (IntentID==0) {// 已连接
                                handler.sendEmptyMessage(0);
                            } else if(IntentID==1){// 未连接
                                handler.sendEmptyMessage(1);
                            }  else if(IntentID==2)
                            {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    }
                });
            }
        };

        networkServiceIntent = new Intent(ActivityPager.this,NetworkMonitorService.class);
        startService(networkServiceIntent);
        bindService(new Intent(ActivityPager.this, NetworkMonitorService.class), sc, getApplicationContext().BIND_AUTO_CREATE);
        state = true;
        System.out.println("network connct " + receiveMsgService);

        //剪贴板监听服务
        clipServiceIntent = new Intent(ActivityPager.this, ClipboardMonitorService.class);
        startService(clipServiceIntent);
        receiver = new StudentRecordBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.studentmgr.ClipboardBroadcast");
        registerReceiver(receiver,intentFilter);//动态注册
        receiver.setCliper(this);
    }

    //增加学生信息按钮事件
    public  void onSubmitInPage(View view){
        FragmentStudent.is_edit = false;
        Toast.makeText(actMain.getActivity(),"请在新界面录入学生信息",Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(actMain.getActivity(),ActivityStudent.class));
        clearEdit();
        viewPager.setCurrentItem(1);
    }

    public  void onSaveFragment(View view){
        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText numberEdit = findViewById(R.id.stuNumberEdit);
        RadioButton man = findViewById(R.id.radioButtonM);
        Spinner spinner1 = findViewById(R.id.spinnerSchool);
        Spinner spinner2 = findViewById(R.id.spinnerMajor);
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        CheckBox check3 = findViewById(R.id.check3);
        CheckBox check4 = findViewById(R.id.check4);
        EditText birth = findViewById(R.id.birthEdit);
        EditText signature = findViewById(R.id.signatureEdit);


        String name = nameEdit.getText().toString();
        String sex;
        String hobby = "";
        boolean is_man = man.isChecked();
        String school = spinner1.getSelectedItem().toString();
        String major = spinner2.getSelectedItem().toString();
        boolean is_check1 = check1.isChecked();
        boolean is_check2 = check2.isChecked();
        boolean is_check3 = check3.isChecked();
        boolean is_check4 = check4.isChecked();

        if(is_man)
            sex = "男";
        else
            sex = "女";
        if(is_check1)
            hobby += "文学";
        if(is_check2)
            hobby += "体育";
        if(is_check3)
            hobby += "音乐";
        if(is_check4)
            hobby += "美术";

        String birthDate = birth.getText().toString();
        String signatureText = signature.getText().toString();
        String number = numberEdit.getText().toString();

        //获取填写信息之后，判断是否符合条件
        if(name.equals("") || school.equals("请选择学院") || major.equals("请选择专业")
        || birthDate.equals("")) {
            Toast.makeText(this,"请完整填写信息后再提交！",Toast.LENGTH_SHORT).show();
            return;
        }

        //检查学号唯一性
        if(!studentDAL.checkUnique(number) && !number.equals(FragmentStudent.editMsg.getNumber())){
            Toast.makeText(this,"请检查学号唯一性！",Toast.LENGTH_SHORT).show();
            return;
        }

        //检查学号格式正确
        if(!ClipboardMonitorService.checkStuNumber(number)){
            Toast.makeText(this,"请检查学号正确性！",Toast.LENGTH_SHORT).show();
            return;
        }

        //保存到结果数组中
        if(hobby.equals("")) hobby += "其他";      //填写完整
        stuMessage d = new stuMessage(name, number, sex, school, major, hobby,birthDate, signatureText);
        if(FragmentStudent.is_edit){
            studentDAL.updateStudent(d, FragmentStudent.editMsg.getNumber());
            FragmentStudent.is_edit = false;

            //修改标题信息
            TextView tvTitle = findViewById(R.id.textViewStudentTitle);
            tvTitle.setText("录入信息");
        }
        else {
            studentDAL.addStudent(d);
            makeToast(d);
        }

        Toast.makeText(this,"保存信息",Toast.LENGTH_SHORT).show();
        FragmentMain.stuMessageList = studentDAL.selectStudent(null, 3);
        setAdapter();
        viewPager.setCurrentItem(0);
        clearEdit();
        //ActivityStudent.this.finish();   //关闭当前activity
    }

    //实现查询功能
    public void onSearch(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                actMain.getActivity());
        builder.setTitle("查询信息");
        builder.setMessage("请输入关键词：");
        final EditText keyword = new EditText(actMain.getActivity());
        builder.setView(keyword);
        builder.setPositiveButton("查询",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        List<stuMessage> result;
                        String key = keyword.getText().toString();
                        result = studentDAL.selectStudent(key, 0);
                        result.addAll(studentDAL.selectStudent(key, 1));
                        result.addAll(studentDAL.selectStudent(key, 2));
                        FragmentMain.stuMessageList = result;

                        Toast.makeText(ActivityPager.this,"共找到"+FragmentMain.stuMessageList.size()+"条结果",Toast.LENGTH_SHORT).show();
                        setAdapter();
                        FragmentMain.is_search = true;
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.create().show();
        // 有人说一定要改成true,但是false也是可以啊
    }

    @Override
    protected void onResume(){
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//设置屏幕按感应设置横竖屏
        super.onResume();
    }

    //选择生日
    public void onBirthPick(View view){
        final TextView tvDate = (TextView) FragmentStudent.layout.findViewById(R.id.birthEdit);
        date = new StringBuffer();
        //初始化时间
        Calendar calendar = Calendar.getInstance();
        FragmentStudent.year = calendar.get(Calendar.YEAR);
        FragmentStudent.month = calendar.get(Calendar.MONTH) + 1;
        FragmentStudent.day = calendar.get(Calendar.DAY_OF_MONTH);

        AlertDialog.Builder builder = new AlertDialog.Builder(actStu.getActivity());
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                tvDate.setText(date.append(String.valueOf(FragmentStudent.year)).append("年").
                        append(String.valueOf(FragmentStudent.month)).append("月").append(FragmentStudent.day).append("日"));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(actStu.getActivity(), R.layout.dialog_date, null);
        FragmentStudent.datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        actStu.initDate();
    }

    //制作一个自定义吐司
    public void makeToast(stuMessage stu){
        Toast toast = new Toast(actStu.getActivity());
        //设置Tosat的属性，如显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //创建线性水平布局管理器,设置内容为垂直居中
        LinearLayout ly = new LinearLayout(actStu.getActivity());
        ly.setBackgroundColor(R.drawable.ic_launcher_background);
        ly.setOrientation(LinearLayout.VERTICAL);
        ly.setGravity(Gravity.CENTER_VERTICAL);
        //创建ImageView
        ImageView iv = new ImageView(actStu.getActivity());
        iv.setImageResource(R.drawable.ic_launcher_foreground);
        //创建TextView
        TextView tv = new TextView(actStu.getActivity());
        tv.setTextColor(Color.parseColor( "#FFFFFF"));
        tv.setGravity(Gravity.CENTER);
        tv.setText("增加"+stu.getName());
        //将图标和提示内容add进布局管理器
        ly.addView(tv);
        ly.addView(iv);
        //将布局管理器添加进Toast
        toast.setView(ly);
        //显示提示
        toast.show();
    }

    //清空信息
    public void clearEdit(){
        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText numberEdit = findViewById(R.id.stuNumberEdit);
        RadioButton man = findViewById(R.id.radioButtonM);
        Spinner spinner1 = findViewById(R.id.spinnerSchool);
        Spinner spinner2 = findViewById(R.id.spinnerMajor);
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        CheckBox check3 = findViewById(R.id.check3);
        CheckBox check4 = findViewById(R.id.check4);
        EditText birth = findViewById(R.id.birthEdit);
        EditText signature = findViewById(R.id.signatureEdit);

        nameEdit.setText(null);
        numberEdit.setText(null);
        man.setChecked(true);
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        check1.setChecked(false);
        check2.setChecked(false);
        check3.setChecked(false);
        check4.setChecked(false);
        birth.setText(null);
        signature.setText(null);

        //修改标题信息
        TextView tvTitle = findViewById(R.id.textViewStudentTitle);
        tvTitle.setText("录入信息");
        FragmentStudent.is_edit = false;
    }

    //检测横竖屏切换，更改布局
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//设置屏幕按感应设置横竖屏
        super.onConfigurationChanged(newConfig);
    }

    //滑动控制
    public void initScroll(){
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        Fragment frag;
        for (int i = 0; frags != null && frags.size() > i; i++) {
            frag = frags.get(i);
            if (frag != null && frag instanceof FragmentMain) {
                scrollView = frag.getView().findViewById(R.id.scrollviewMain);
                listView = frag.getView().findViewById(R.id.listviewFragment);
            }
        }
    }

    //个性签名粘贴方法
    public void onPaste(View view){
        ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence text = clip.getText();
        if(text != null) {
            EditText signatureText = findViewById(R.id.signatureEdit);
            signatureText.setText(text.toString().trim());
        }
    }
    public void onPasteByFile(View view){
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath()
        //        + File.separator + "test" + File.separator + "testSignature.txt";
        String path = "//storage//emulated//0//Android//data//com.example.studentmgr//files//testSignature.txt";
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    if(Build.VERSION.SDK_INT >= 29)
                        inputreader = new InputStreamReader(instream, "GB2312");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                e.printStackTrace();
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("TestFile", e.getMessage());
            }
        }
        System.out.println(path);
        if(!content.equals("")) {
            EditText signatureText = findViewById(R.id.signatureEdit);
            signatureText.setText(content);
        }
    }

    //设置listview的适配器
    public void setAdapter(){
        FragmentMain.adapter=new MessageAdapter(actMain.getActivity(),R.layout.item, FragmentMain.stuMessageList, sp);
        FragmentMain.listView.setAdapter(FragmentMain.adapter);
        FragmentMain.adapter.notifyDataSetChanged();
    }

    // 设置网络状态监听服务
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(state){
            unbindService(sc);
            stopService(networkServiceIntent);
            state = false;
        }
        unregisterReceiver(receiver);
        stopService(clipServiceIntent);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    Toast.makeText(ActivityPager.this, "网络未经连接" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[网络已断开]");
                    break;
                case 1:
                    Toast.makeText(ActivityPager.this, "WIFI已经连接" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[WIFI连接]");
                    break;
                case 2:
                    Toast.makeText(ActivityPager.this, "移动网络已连接" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[移动网络连接]");
                    break;
                default:
                    break;
            }
        }
    };

    // 剪贴板监听接口实现
    @Override
    public void clip(String s){
        clearEdit();
        EditText numberEdit = findViewById(R.id.stuNumberEdit);
        numberEdit.setText(s);
        viewPager.setCurrentItem(1);
    }

}
