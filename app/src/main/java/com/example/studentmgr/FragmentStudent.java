package com.example.studentmgr;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.studentmgr.settings.stuMessage;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStudent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStudent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStudent extends Fragment implements DatePicker.OnDateChangedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Spinner spcollege,spMajor;
    ArrayAdapter<String> majorAdapter;
    ArrayAdapter<String>  collegeAdapter;
    static boolean is_edit = false;
    static stuMessage editMsg;
    static int year,month,day;
    static DatePicker datePicker;
    String school="请选择学院";
    String[] schools = new String[] {"请选择学院","计算机学院","电气学院"};
    String[] majorCS = new String[] {"请选择专业","软件工程","信息安全","物联网工程"};
    String[] majorDQ = new String[] {"请选择专业","电气工程","电机工程"};
    static View layout;
    SharedPreferences sp;

    EditText nameEdit, numberEdit, birth, signature;
    RadioButton man,woman;
    Spinner spinner1,spinner2;
    CheckBox check1, check2, check3, check4;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentStudent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStudent.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStudent newInstance(String param1, String param2) {
        FragmentStudent fragment = new FragmentStudent();
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
        super.onCreate(savedInstanceState);
        layout = inflater.inflate(R.layout.fragment_student, null);

        nameEdit = layout.findViewById(R.id.nameEdit);
        numberEdit = layout.findViewById(R.id.stuNumberEdit);
        man = layout.findViewById(R.id.radioButtonM);
        woman = layout.findViewById(R.id.radioButtonF);
        spinner1 = layout.findViewById(R.id.spinnerSchool);
        spinner2 = layout.findViewById(R.id.spinnerMajor);
        check1 = layout.findViewById(R.id.check1);
        check2 = layout.findViewById(R.id.check2);
        check3 = layout.findViewById(R.id.check3);
        check4 = layout.findViewById(R.id.check4);
        birth = layout.findViewById(R.id.birthEdit);
        signature = layout.findViewById(R.id.signatureEdit);

        sp = getActivity().getSharedPreferences("mySetting", MODE_PRIVATE);

        configTextSize();   //设置字体大小

        setSpinner();//定义方法设置下拉列表联动
        return layout;
    }

    @Override
    public void onResume(){
        configTextSize();   //设置字体大小
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //监听日期变化
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1;
        this.day = dayOfMonth;
    }

    public void initDate(){
        datePicker.init(year, month - 1, day, this );
    }


    public void setSpinner() {
        //绑定适配器和值
        collegeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, schools);
        spcollege = layout.findViewById(R.id.spinnerSchool);
        spcollege.setAdapter(collegeAdapter);
        spcollege.setSelection(0, true);//设置初始默认值
        //绑定适配器和值
        majorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, majorCS);
        spMajor = layout.findViewById(R.id.spinnerMajor);
        spMajor.setAdapter(majorAdapter);
        spMajor.setSelection(0, true);//设置初始默认值

        //设置列表项选中监听
        spcollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选中项的值
                school = adapterView.getItemAtPosition(i).toString();
                //根据选中的不同的值绑定不同的适配器
                if (school.equals("计算机学院")) {
                    majorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, majorCS);
                    spMajor.setAdapter(majorAdapter);
                } else if (school.equals("电气学院")) {
                    majorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, majorDQ);
                    spMajor.setAdapter(majorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //根据用户设置切换字体大小
    public void configTextSize(){
        String fontSize = sp.getString("fontSize", "");
        if(fontSize.equals("")){
        }
        else if(fontSize.equals("标准")){
            setTextSize(20);
        }
        else if(fontSize.equals("较大")){
            setTextSize(40);
        }
        else if(fontSize.equals("较小")){
            setTextSize(10);
        }
    }
    public void setTextSize(int size){
        nameEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        numberEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        man.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        check1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        check2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        check3.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        check4.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        birth.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
        signature.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
    }
}
