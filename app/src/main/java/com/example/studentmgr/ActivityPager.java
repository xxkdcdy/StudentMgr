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

    String[] schools = new String[] {"???????????????","???????????????","????????????"};
    String[] majorCS = new String[] {"???????????????","????????????","????????????","???????????????"};
    String[] majorDQ = new String[] {"???????????????","????????????","????????????"};
    ArrayAdapter<String> majorAdapter;
    ArrayAdapter<String> collegeAdapter;

    NetworkMonitorService receiveMsgService;
    int IntentID = 666;   //????????????????????????????????????
    ServiceConnection sc;
    Intent networkServiceIntent, clipServiceIntent;
    boolean state;

    StudentRecordBroadcastReceiver receiver;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //?????????????????????????????????FragmentMain????????????????????????????????????
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

        //??????????????????
        TextView tvTitle = findViewById(R.id.textViewStudentTitle);
        tvTitle.setText("????????????");

        //?????????????????????
        signature.setText(msg.getSignature());
        birth.setText(msg.getBirth());
        nameEdit.setText(msg.getName());
        numberEdit.setText(msg.getNumber());
        if(msg.getSex().equals("???"))
            man.setChecked(true);
        else
            woman.setChecked(true);
        String editMajor = msg.getMajor();


        spinner1.setOnItemSelectedListener(null);
        //????????????????????????
        //?????????????????????
        collegeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, schools);
        spinner1.setAdapter(collegeAdapter);

        if(msg.getSchool().equals("???????????????")){
            majorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorCS);
            spinner2.setAdapter(majorAdapter);

            spinner1.setSelection(1,true);
            if(editMajor.equals("????????????")) {
                spinner2.setSelection(1,true);
            }
            else if(editMajor.equals("????????????"))
                spinner2.setSelection(2,true);
            else if(editMajor.equals("???????????????"))
                spinner2.setSelection(3,true);
        }
        else if(msg.getSchool().equals("????????????")) {
            //?????????????????????
            majorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majorDQ);
            spinner2.setAdapter(majorAdapter);

            spinner1.setSelection(2, true);
            if (editMajor.equals("????????????"))
                spinner2.setSelection(1, true);
            else if (editMajor.equals("????????????"))
                spinner2.setSelection(2, true);
        }

        //???????????????????????????
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //?????????????????????
               String  school = adapterView.getItemAtPosition(i).toString();
                //???????????????????????????????????????????????????
                if (school.equals("???????????????")) {
                    majorAdapter = new ArrayAdapter<String>(actStu.getActivity(), android.R.layout.simple_spinner_item, majorCS);
                    spinner2.setAdapter(majorAdapter);
                } else if (school.equals("????????????")) {
                    majorAdapter = new ArrayAdapter<String>(actStu.getActivity(), android.R.layout.simple_spinner_item, majorDQ);
                    spinner2.setAdapter(majorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //??????
        String editHobby = msg.getHobby();
        if(editHobby.indexOf("??????") != -1)
            check1.setChecked(true);
        if(editHobby.indexOf("??????") != -1)
            check2.setChecked(true);
        if(editHobby.indexOf("??????") != -1)
            check3.setChecked(true);
        if(editHobby.indexOf("??????") != -1)
            check4.setChecked(true);
    }

    //????????????
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

        //???????????????????????????
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //?????????????????????

        /**
         * ???????????????
         */
        class ViewPagerAdapter extends FragmentPagerAdapter {

            public ViewPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            //??????Fragment
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            //???????????????
            @Override
            public int getCount() {
                return fragmentList.size();

            }
        }


        // adapter??????Fragment?????????
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // viewpager??????adapter
        viewPager.setAdapter(mAdapter);

        initScroll();

        //????????????????????????
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
                receiveMsgService.setOnGetConnectState(new NetworkMonitorService.GetConnectState() { // ????????????????????????????????????
                    @Override
                    public void GetState(int id) {
                        if (IntentID != id) { // ?????????????????????????????????????????????????????????????????????????????????
                            IntentID = id;
                            if (IntentID==0) {// ?????????
                                handler.sendEmptyMessage(0);
                            } else if(IntentID==1){// ?????????
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

        //?????????????????????
        clipServiceIntent = new Intent(ActivityPager.this, ClipboardMonitorService.class);
        startService(clipServiceIntent);
        receiver = new StudentRecordBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.studentmgr.ClipboardBroadcast");
        registerReceiver(receiver,intentFilter);//????????????
        receiver.setCliper(this);
    }

    //??????????????????????????????
    public  void onSubmitInPage(View view){
        FragmentStudent.is_edit = false;
        Toast.makeText(actMain.getActivity(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
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
            sex = "???";
        else
            sex = "???";
        if(is_check1)
            hobby += "??????";
        if(is_check2)
            hobby += "??????";
        if(is_check3)
            hobby += "??????";
        if(is_check4)
            hobby += "??????";

        String birthDate = birth.getText().toString();
        String signatureText = signature.getText().toString();
        String number = numberEdit.getText().toString();

        //???????????????????????????????????????????????????
        if(name.equals("") || school.equals("???????????????") || major.equals("???????????????")
        || birthDate.equals("")) {
            Toast.makeText(this,"????????????????????????????????????",Toast.LENGTH_SHORT).show();
            return;
        }

        //?????????????????????
        if(!studentDAL.checkUnique(number) && !number.equals(FragmentStudent.editMsg.getNumber())){
            Toast.makeText(this,"???????????????????????????",Toast.LENGTH_SHORT).show();
            return;
        }

        //????????????????????????
        if(!ClipboardMonitorService.checkStuNumber(number)){
            Toast.makeText(this,"???????????????????????????",Toast.LENGTH_SHORT).show();
            return;
        }

        //????????????????????????
        if(hobby.equals("")) hobby += "??????";      //????????????
        stuMessage d = new stuMessage(name, number, sex, school, major, hobby,birthDate, signatureText);
        if(FragmentStudent.is_edit){
            studentDAL.updateStudent(d, FragmentStudent.editMsg.getNumber());
            FragmentStudent.is_edit = false;

            //??????????????????
            TextView tvTitle = findViewById(R.id.textViewStudentTitle);
            tvTitle.setText("????????????");
        }
        else {
            studentDAL.addStudent(d);
            makeToast(d);
        }

        Toast.makeText(this,"????????????",Toast.LENGTH_SHORT).show();
        FragmentMain.stuMessageList = studentDAL.selectStudent(null, 3);
        setAdapter();
        viewPager.setCurrentItem(0);
        clearEdit();
        //ActivityStudent.this.finish();   //????????????activity
    }

    //??????????????????
    public void onSearch(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                actMain.getActivity());
        builder.setTitle("????????????");
        builder.setMessage("?????????????????????");
        final EditText keyword = new EditText(actMain.getActivity());
        builder.setView(keyword);
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        List<stuMessage> result;
                        String key = keyword.getText().toString();
                        result = studentDAL.selectStudent(key, 0);
                        result.addAll(studentDAL.selectStudent(key, 1));
                        result.addAll(studentDAL.selectStudent(key, 2));
                        FragmentMain.stuMessageList = result;

                        Toast.makeText(ActivityPager.this,"?????????"+FragmentMain.stuMessageList.size()+"?????????",Toast.LENGTH_SHORT).show();
                        setAdapter();
                        FragmentMain.is_search = true;
                    }
                });
        builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.create().show();
        // ????????????????????????true,??????false???????????????
    }

    @Override
    protected void onResume(){
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//????????????????????????????????????
        super.onResume();
    }

    //????????????
    public void onBirthPick(View view){
        final TextView tvDate = (TextView) FragmentStudent.layout.findViewById(R.id.birthEdit);
        date = new StringBuffer();
        //???????????????
        Calendar calendar = Calendar.getInstance();
        FragmentStudent.year = calendar.get(Calendar.YEAR);
        FragmentStudent.month = calendar.get(Calendar.MONTH) + 1;
        FragmentStudent.day = calendar.get(Calendar.DAY_OF_MONTH);

        AlertDialog.Builder builder = new AlertDialog.Builder(actStu.getActivity());
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //???????????????????????????
                    date.delete(0, date.length());
                }
                tvDate.setText(date.append(String.valueOf(FragmentStudent.year)).append("???").
                        append(String.valueOf(FragmentStudent.month)).append("???").append(FragmentStudent.day).append("???"));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(actStu.getActivity(), R.layout.dialog_date, null);
        FragmentStudent.datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        dialog.setTitle("????????????");
        dialog.setView(dialogView);
        dialog.show();
        //???????????????????????????
        actStu.initDate();
    }

    //???????????????????????????
    public void makeToast(stuMessage stu){
        Toast toast = new Toast(actStu.getActivity());
        //??????Tosat???????????????????????????
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //?????????????????????????????????,???????????????????????????
        LinearLayout ly = new LinearLayout(actStu.getActivity());
        ly.setBackgroundColor(R.drawable.ic_launcher_background);
        ly.setOrientation(LinearLayout.VERTICAL);
        ly.setGravity(Gravity.CENTER_VERTICAL);
        //??????ImageView
        ImageView iv = new ImageView(actStu.getActivity());
        iv.setImageResource(R.drawable.ic_launcher_foreground);
        //??????TextView
        TextView tv = new TextView(actStu.getActivity());
        tv.setTextColor(Color.parseColor( "#FFFFFF"));
        tv.setGravity(Gravity.CENTER);
        tv.setText("??????"+stu.getName());
        //????????????????????????add??????????????????
        ly.addView(tv);
        ly.addView(iv);
        //???????????????????????????Toast
        toast.setView(ly);
        //????????????
        toast.show();
    }

    //????????????
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

        //??????????????????
        TextView tvTitle = findViewById(R.id.textViewStudentTitle);
        tvTitle.setText("????????????");
        FragmentStudent.is_edit = false;
    }

    //????????????????????????????????????
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        is_ORIENTATION_lock = sp.getBoolean("orientation", true);
        if(!is_ORIENTATION_lock){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//????????????????????????????????????
        super.onConfigurationChanged(newConfig);
    }

    //????????????
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

    //????????????????????????
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
        String content = ""; //?????????????????????
        //????????????
        File file = new File(path);
        //??????path????????????????????????????????????????????????????????????
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
                    //????????????
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

    //??????listview????????????
    public void setAdapter(){
        FragmentMain.adapter=new MessageAdapter(actMain.getActivity(),R.layout.item, FragmentMain.stuMessageList, sp);
        FragmentMain.listView.setAdapter(FragmentMain.adapter);
        FragmentMain.adapter.notifyDataSetChanged();
    }

    // ??????????????????????????????
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
                    Toast.makeText(ActivityPager.this, "??????????????????" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[???????????????]");
                    break;
                case 1:
                    Toast.makeText(ActivityPager.this, "WIFI????????????" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[WIFI??????]");
                    break;
                case 2:
                    Toast.makeText(ActivityPager.this, "?????????????????????" ,Toast.LENGTH_LONG).show();
                    setTitle(title + "[??????????????????]");
                    break;
                default:
                    break;
            }
        }
    };

    // ???????????????????????????
    @Override
    public void clip(String s){
        clearEdit();
        EditText numberEdit = findViewById(R.id.stuNumberEdit);
        numberEdit.setText(s);
        viewPager.setCurrentItem(1);
    }

}
