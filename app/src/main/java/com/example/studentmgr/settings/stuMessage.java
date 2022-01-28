package com.example.studentmgr.settings;

public class stuMessage {
    
    private String name;
    private String number;
    private String sex;
    private String school;
    private String major;
    private String hobby;
    private String birth;
    private String signature;

    public stuMessage() {
        this.name = "";
        this.number = "";
        this.sex = "";
        this.school = "";
        this.major = "";
        this.hobby = "";
        this.birth = "";
        this.signature = "";
    }

    public stuMessage(String name, String number, String sex, String school, String major, String hobby, String birth, String signature) {
        this.name = name;
        this.number = number;
        this.sex = sex;
        this.school = school;
        this.major = major;
        this.hobby = hobby;
        this.birth = birth;
        this.signature = signature;
    }

    public void changeMessage(String name, String number, String sex, String school, String major, String hobby, String birth, String signature) {
        this.name = name;
        this.number = number;
        this.sex = sex;
        this.school = school;
        this.major = major;
        this.hobby = hobby;
        this.birth = birth;
        this.signature = signature;
    }

    public void cloneMessage(stuMessage msg) {
        this.name = msg.getName();
        this.number = msg.getNumber();
        this.sex = msg.getSex();
        this.school = msg.getSchool();
        this.major = msg.getMajor();
        this.hobby = msg.getHobby();
        this.birth = msg.getBirth();
        this.signature = msg.getSignature();
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getSex() {
        return sex;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }

    public String getHobby() {
        return hobby;
    }

    public String getBirth() {
        return birth;
    }

    public String getSignature() {return signature; }
}
