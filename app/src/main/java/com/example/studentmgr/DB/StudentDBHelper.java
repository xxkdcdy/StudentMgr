package com.example.studentmgr.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDBHelper extends SQLiteOpenHelper {
    //public stuMessage(String name, String sex, String school, String major, String hobby, String birth, String signature)
    final String CREATE_TABLE_SQL =
            "create table student(_id integer primary " +
                    "key autoincrement , name, sex, school, major, hobby, birth, signature)";
    public StudentDBHelper(Context context, String name, int version)
    {
        super(context, name, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 第一次使用数据库时自动建表
        db.execSQL(CREATE_TABLE_SQL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db
            , int oldVersion, int newVersion)
    {
        System.out.println("--------onUpgrade Called--------"
                + oldVersion + "--->" + newVersion);

        //将原有表重命名为临时表
        db.execSQL("ALTER TABLE student RENAME TO student_temp");

        //创建新表
        final String CREATE_TABLE_SQL =
                "create table student(_id integer primary " +
                        "key autoincrement , name, number UNIQUE, sex, school, major, hobby, birth, signature, test INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE_SQL);

        //将临时表的数据导入新表
        db.execSQL("insert into student(_id, name, sex, school, major, hobby, birth, signature) "
                + "select _id, name, sex, school, major, hobby, birth, signature from student_temp");

        //删除临时表
        db.execSQL("DROP TABLE student_temp");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db
            , int oldVersion, int newVersion)
    {
        System.out.println("--------onDowngrade Called--------"
                + oldVersion + "--->" + newVersion);

        //将原有表重命名为临时表
        db.execSQL("ALTER TABLE student RENAME TO student_temp");

        //创建新表
        final String CREATE_TABLE_SQL =
                "create table student(_id integer primary " +
                        "key autoincrement , name, sex, school, major, hobby, birth, signature)";
        db.execSQL(CREATE_TABLE_SQL);

        //将临时表的数据导入新表
        db.execSQL("insert into student(_id, name, sex, school, major, hobby, birth, signature) "
                + "select _id, name, sex, school, major, hobby, birth, signature from student_temp");

        //删除临时表
        db.execSQL("DROP TABLE student_temp");
    }
}
