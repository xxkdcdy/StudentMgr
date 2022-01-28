package com.example.studentmgr.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentmgr.settings.stuMessage;

import java.util.ArrayList;
import java.util.List;

public class StudentDAL {
    private StudentDBHelper studentDBHelper;
    private SQLiteDatabase db;
    static int VERSON = 3;
    static String name = "studentDB";


    //public stuMessage(String name, String sex, String school, String major, String hobby, String birth, String signature)
    public StudentDAL(Context context,int mode)
    {
        studentDBHelper=new StudentDBHelper(context, name, VERSON);
        switch (mode)
        {
            case 0:
                clear();
                if(countcal() == 0) {
                    addStudent("ZhangSan1", "SE000001", "男", "计算机学院", "软件工程", "文学", "1993年5月16日", "test");
                    addStudent("ZhangSan2", "SE000002", "女", "计算机学院", "物联网工程", "体育", "1993年5月16日", "test");
                    addStudent("ZhangSan3", "SE000003", "男", "计算机学院", "信息安全", "音乐", "1993年5月16日", "test");
                    addStudent("ZhangSan4", "SE000004", "女", "电气学院", "电气工程", "文学", "1993年5月16日", "test");
                }
                System.out.println(selectStudent(null,3).toString());

                break;
            case 1:
                break;

        }
    }

    public void addStudent(stuMessage stu)
    {
        db = studentDBHelper.getReadableDatabase();
        ContentValues cv=new ContentValues(8);
        cv.put("name",stu.getName());
        cv.put("number",stu.getNumber());
        cv.put("sex",stu.getSex());
        cv.put("school",stu.getSchool());
        cv.put("major",stu.getMajor());
        cv.put("hobby",stu.getHobby());
        cv.put("birth",stu.getBirth());
        cv.put("signature",stu.getSignature());
        db.insert("student","_id",cv);
        db.close();
    }

    public void addStudent(String name, String number, String sex, String school, String major, String hobby, String birth, String signature)
    {
        db = studentDBHelper.getReadableDatabase();
        ContentValues cv=new ContentValues(8);
        cv.put("name",name);
        cv.put("number",number);
        cv.put("sex",sex);
        cv.put("school",school);
        cv.put("major",major);
        cv.put("hobby",hobby);
        cv.put("birth",birth);
        cv.put("signature",signature);
        db.insert("student","_id",cv);
        db.close();
    }

    public int deleteStudent(String number)
    {
        db = studentDBHelper.getReadableDatabase();
        int deleteCount =db.delete("student","number = ?",new String[]{number});
        db.close();
        return deleteCount;
    }

    public int updateStudent(stuMessage stu, String number)
    {
        db = studentDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues(8);
        values.put("name",stu.getName());
        values.put("number",stu.getNumber());
        values.put("sex",stu.getSex());
        values.put("school",stu.getSchool());
        values.put("major",stu.getMajor());
        values.put("hobby",stu.getHobby());
        values.put("birth",stu.getBirth());
        values.put("signature",stu.getSignature());
        int updateCount =db.update("student",values,"number=?",new String[]{number});
        db.close();
        return updateCount;
    }

    public int countcal()
    {
        db = studentDBHelper.getReadableDatabase();
        Cursor cursor =  db.query("student",null,null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void clear()
    {
        db = studentDBHelper.getReadableDatabase();
        int deleteCount =db.delete("student",null,null);
        db.close();
    }

    public List<stuMessage> selectStudent(String content, int i)
    {
        db = studentDBHelper.getReadableDatabase();
        List<stuMessage> studentslist=null;
        String sql="";
        Cursor cursor=null;
        //如果姓名(0)、学院(1)、专业(2)没有包含关键词
        //全部查询(3)
        switch (i)
        {
            case 0: cursor =  db.query("student",null,"name like ?",new String[]{"%" + content + "%"},null,null,null);
                break;
            case 1: cursor =  db.query("student",null,"school like ?",new String[]{"%" + content + "%"},null,null,null);
                break;
            case 2: cursor =  db.query("student",null,"major like ?",new String[]{"%" + content + "%"},null,null,null);
                break;
            case 3:cursor =  db.query("student",null,null,null,null,null,null);
                break;
            case 4:cursor =  db.query("student",null,"number = ?",new String[]{content},null,null,null);
                break;
        }

        //public stuMessage(String name, String sex, String school, String major, String hobby, String birth, String signature)
        if(cursor.moveToFirst())
        {
            studentslist=new ArrayList<>();
            String name=cursor.getString(cursor.getColumnIndex("name"));
            String number=cursor.getString(cursor.getColumnIndex("number"));
            String sex=cursor.getString(cursor.getColumnIndex("sex"));
            String school=cursor.getString(cursor.getColumnIndex("school"));
            String major=cursor.getString(cursor.getColumnIndex("major"));
            String hobby=cursor.getString(cursor.getColumnIndex("hobby"));
            String birth=cursor.getString(cursor.getColumnIndex("birth"));
            String signature=cursor.getString(cursor.getColumnIndex("signature"));
            if(cursor.getColumnIndex("test") != -1)
                System.out.println("当前表存在test列");
            stuMessage temp=new stuMessage(name, number, sex, school, major, hobby, birth, signature);
            studentslist.add(temp);
            while(cursor.moveToNext())
            {
                name=cursor.getString(cursor.getColumnIndex("name"));
                number=cursor.getString(cursor.getColumnIndex("number"));
                sex=cursor.getString(cursor.getColumnIndex("sex"));
                school=cursor.getString(cursor.getColumnIndex("school"));
                major=cursor.getString(cursor.getColumnIndex("major"));
                hobby=cursor.getString(cursor.getColumnIndex("hobby"));
                birth=cursor.getString(cursor.getColumnIndex("birth"));
                signature=cursor.getString(cursor.getColumnIndex("signature"));
                temp=new stuMessage(name, number, sex, school, major, hobby, birth, signature);
                studentslist.add(temp);
            }
        }
        else
        {
            studentslist=new ArrayList<>();
        }
        return studentslist;
    }

    public boolean checkUnique(String number){
        List<stuMessage> list = selectStudent(number, 4);     //查询学号是否有重复
        if(list.size() > 0)
            return false;
        return true;
    }

}
