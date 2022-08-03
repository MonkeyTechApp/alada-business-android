package com.poupock.feussom.aladabusiness.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Ronald Jr Feussom
 *
 */
public class MobileDate {
    public int year ;
    public int month ;
    public int day;
    public int Hrs ;
    public int Mins;
    public int Secs ;

    public MobileDate( int year , int month , int day, int Hrs , int Mins, int Secs ) {
        this.day=day;
        this.month =month;
        this.year =year;
        this.Hrs = Hrs;
        this.Mins = Mins;
        this.Secs =Secs;
    }
    public MobileDate( int year , int month , int day ) {
        this.day=day;
        this.month =month;
        this.year =year;

    }
    public MobileDate( ) {

    }

    public static Date ConvertToDate(MobileDate mobileDate) {
        Date date = new Date();
        date.setDate(mobileDate.getDay());
        date.setHours(mobileDate.Hrs);
        date.setMinutes(mobileDate.getMins());
        date.setSeconds(mobileDate.getSecs());
        date.setMonth(mobileDate.getMonth()-1);
        date.setYear(mobileDate.getYear()-1900);

        return date;
    }

    public static long getDifferenceFromToday(MobileDate mobileDate) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        long toDayMilli = calendar.getTime().getTime();
        long startMilli = MobileDate.ConvertToDate(mobileDate).getTime();
        long Diff = startMilli - toDayMilli;
        return Diff / (1000*60*60*24);
    }

    @Override
    public String toString (){
        String res="";
        res = year+"-"+month+"-"+day+" "+Hrs+":"+Mins+":"+Secs;
        return res;

    }

    public int getDay() {
        return day;
    }

    public int getHrs() {
        return Hrs;
    }

    public int getMins() {
        return Mins;
    }

    public int getMonth() {
        return month;
    }

    public int getSecs() {
        return Secs;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHrs(int hrs) {
        Hrs = hrs;
    }

    public void setMins(int mins) {
        Mins = mins;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setSecs(int secs) {
        Secs = secs;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static MobileDate TreatStringFormat(String dateString){
        String DayPart = dateString.substring(0,dateString.indexOf(" "));
        String TimePArt = dateString.substring(dateString.indexOf(" "),dateString.length()).replace(" ", "");


        int year = Integer.valueOf(DayPart.substring(0, DayPart.indexOf("-")));
        int month = Integer.valueOf(DayPart.substring(DayPart.indexOf("-"),DayPart.lastIndexOf("-")).replace("-", ""));
        int day = Integer.valueOf(DayPart.substring( DayPart.lastIndexOf("-"),DayPart.length()).replace("-", ""));


        int Hrs = Integer.valueOf(TimePArt.substring(0, TimePArt.indexOf(":")));
        int Mins = Integer.valueOf(TimePArt.substring(TimePArt.indexOf(":"),TimePArt.lastIndexOf(":")).replace(":", ""));
        int Secs = Integer.valueOf(TimePArt.substring( TimePArt.lastIndexOf(":"),TimePArt.length()).replace(":", ""));
        MobileDate date = new MobileDate(year, month, day, Hrs, Mins, Secs);

        return date;
    }
}
