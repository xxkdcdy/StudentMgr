// IMyService.aidl
package com.example.studentmgr.AIDL;

// Declare any non-default types here with import statements
interface IMyService {
    String QueryWeekday(int year, int month, int day);
}
