package com.example.studentmgr;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmgr.AIDL.IMyService;
import com.example.studentmgr.DB.StudentDAL;
import com.example.studentmgr.Service.NetworkMonitorService;
import com.example.studentmgr.Service.QueryWeekdayService;
import com.example.studentmgr.settings.CustomScrollView;
import com.example.studentmgr.settings.MessageAdapter;
import com.example.studentmgr.settings.stuMessage;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static List<stuMessage> stuMessageList =new ArrayList<>();
    static List<Integer> indexList = new ArrayList<>();     //记录两个数组的下标关系
    static ListView listView;
    static CustomScrollView scrollView;
    static MessageAdapter adapter;
    static boolean is_search = false;      //标志当前ListView是否处于查询状态
    SharedPreferences sp;
    ServiceConnection sc;
    QueryWeekdayService queryWeekdayService;
    IMyService mBinder; // 接口的一个引用
    String weekDay = "未获取星期数";       //用来保存当前通过service获得的星期数
    static int year,month,day;     //用户输入的年月日，用来获取星期数

    private OnFragmentInteractionListener mListener;
    private StudentDAL studentDAL;

    public FragmentMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMain.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMain newInstance(String param1, String param2) {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_main, null);

        setHasOptionsMenu(true);
        sp = getActivity().getSharedPreferences("mySetting", MODE_PRIVATE);

        // 先拿到数据并放在适配器上
        studentDAL = new StudentDAL(getActivity(), 0);
        initMessages(); //初始化数据
        adapter=new MessageAdapter(getActivity(),R.layout.item, stuMessageList, sp);

        // 将适配器上的数据传递给listView
        listView=layout.findViewById(R.id.listviewFragment);
        listView.setAdapter(adapter);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), stuMessageList.get(position).getName(),Toast.LENGTH_SHORT).show();
            }
        });

        // ListView滑动冲突解决
        scrollView = layout.findViewById(R.id.scrollviewMain);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                final int pos = position;
                Log.e("", "onItemLongClick");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setTitle("长按选项");
                builder.setMessage("选择对该条目的操作");
                builder.setPositiveButton("编辑",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                FragmentStudent.is_edit = true;
                                FragmentStudent.editMsg = stuMessageList.get(pos);
                                Toast.makeText(getActivity(),"请在新界面修改学生信息",Toast.LENGTH_SHORT).show();
                                ActivityPager.viewPager.setCurrentItem(1);
                                CallBackListener listener = (CallBackListener)getActivity();
                                listener.setText(FragmentStudent.editMsg);
                            }
                        });
                builder.setNegativeButton("删除",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                AlertDialog.Builder notice = new AlertDialog.Builder(getActivity());
                                notice.setTitle("确认");
                                notice.setMessage("是否删除选择的项目");
                                notice.setPositiveButton("确认",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface subDialog,
                                                        int subWhich) {
                                            stuMessage stu = stuMessageList.get(pos);
                                            studentDAL.deleteStudent(stu.getNumber());
                                            //stuMessageList = studentDAL.selectStudent(null, 3);
                                            //adapter.notifyDataSetChanged();
                                            makeToast(stu);
                                        stuMessageList = studentDAL.selectStudent(null, 3);
                                        adapter=new MessageAdapter(getActivity(),R.layout.item, stuMessageList, sp);
                                        listView.setAdapter(adapter);
                                    }
                                });
                                notice.setNegativeButton("取消",null);
                                notice.create().show();
                            }
                        });
                builder.create().show();
                // 有人说一定要改成true,但是false也是可以啊
                return false;

            }
        });
        return layout;
    }

    @Override
    public void onResume(){
        configTextSize();
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    // 初始化数据
    private void initMessages(){
        stuMessageList = studentDAL.selectStudent(null, 3);   //全部查询
    }

    //菜单的构建
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.optionmenu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getTitle().equals("增加")){
            FragmentStudent.is_edit = false;
            Toast.makeText(getActivity(),"请在新界面录入学生信息",Toast.LENGTH_SHORT).show();

            ActivityPager.viewPager.setCurrentItem(1);
        }
        else if(item.getTitle().equals("刷新")){
            stuMessageList = studentDAL.selectStudent(null, 3);
            adapter=new MessageAdapter(getActivity(),R.layout.item, stuMessageList, sp);
            listView.setAdapter(adapter);
            is_search = false;
        }
        else if(item.getTitle().equals("归属地")){
            Intent intent = new Intent(FragmentMain.this.getActivity(),ActivityPhonePlace.class);
            startActivity(intent);
        }
        else if(item.getTitle().equals("查询天气")){
            Intent intent = new Intent(FragmentMain.this.getActivity(),ActivityWeather.class);
            startActivity(intent);
        }
        else if(item.getTitle().equals("查询星期数")){
            sc=new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // TODO Auto-generated method stub

                    System.out.println("in onServiceConnected");
                    mBinder = IMyService.Stub.asInterface(service);
                    queryWeekdayService = ((QueryWeekdayService.MyBinder) service)
                            .getService();
                }
            };
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), QueryWeekdayService.class),
                    sc, BIND_AUTO_CREATE);

            showAlertDialog();
        }
        else if(item.getTitle().equals("设置")){
            Intent intent = new Intent(FragmentMain.this.getActivity(),ActivityConfig.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //制作一个自定义吐司
    public void makeToast(stuMessage stu){
        Toast toast = new Toast(getActivity());
        //设置Tosat的属性，如显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //创建线性水平布局管理器,设置内容为垂直居中
        LinearLayout ly = new LinearLayout(getActivity());
        ly.setBackgroundColor(R.drawable.ic_launcher_background);
        ly.setOrientation(LinearLayout.VERTICAL);
        ly.setGravity(Gravity.CENTER_VERTICAL);
        //创建ImageView
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.ic_launcher_foreground);
        //创建TextView
        TextView tv = new TextView(getActivity());
        tv.setTextColor(Color.parseColor( "#FFFFFF"));
        tv.setGravity(Gravity.CENTER);
        tv.setText("删除"+stu.getName());
        //将图标和提示内容add进布局管理器
        ly.addView(tv);
        ly.addView(iv);
        //将布局管理器添加进Toast
        toast.setView(ly);
        //显示提示
        toast.show();
    }

    //设置用于修改文本的回调接口
    public static interface CallBackListener{
        void setText(stuMessage stu);
    }

    public void configTextSize(){
        String fontSize = sp.getString("fontSize", "");
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
                //(LinearLayout.LayoutParams)layout.getLayoutParams();
        if(fontSize.equals("标准") || fontSize.equals("较小")){
            lp.height = 4 * 500;
        }
        else if(fontSize.equals("较大")){
            lp.height = 4 * 650;
        }
        else{}
        adapter.notifyDataSetChanged();
    }

    //弹出带输入框的对话框
    private void showAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etYear = (EditText) dialog.findViewById(R.id.et_year);
        final EditText etMonth = (EditText) dialog.findViewById(R.id.et_month);
        final EditText etDay = (EditText) dialog.findViewById(R.id.et_day);
        final TextView tv = (TextView) dialog.findViewById(R.id.ansWeek);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String strYear = etYear.getText().toString();
                String strMonth = etMonth.getText().toString();
                String strDay = etDay.getText().toString();
                if (isNullEmptyBlank(strYear)) {
                    etYear.setError("输入内容不能为空");
                }
                else if (isNullEmptyBlank(strMonth)) {
                    etMonth.setError("输入内容不能为空");
                }else if (isNullEmptyBlank(strDay)) {
                    etDay.setError("输入内容不能为空");
                }else {
                    year = Integer.parseInt(strYear);
                    month = Integer.parseInt(strMonth);
                    day = Integer.parseInt(strDay);
                    try {
                        weekDay = mBinder.QueryWeekday(year, month, day);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    tv.setText(year + "年 " + month + "月 " + day + "日 #" + weekDay);
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().getApplicationContext().unbindService(sc);
                dialog.dismiss();
            }
        });
    }

    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }
}
