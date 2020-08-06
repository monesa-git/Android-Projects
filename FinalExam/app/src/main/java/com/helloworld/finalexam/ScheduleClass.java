package com.helloworld.finalexam;

import java.io.Serializable;
import java.util.Date;

public class ScheduleClass implements Serializable {
    String meeting_name, meeting_location, meeting_date;
    Date date;

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_location() {
        return meeting_location;
    }

    public void setMeeting_location(String meeting_location) {
        this.meeting_location = meeting_location;
    }

    public String getMeeting_date() {
        return meeting_date;
    }

    public void setMeeting_date(String meeting_date) {
        this.meeting_date = meeting_date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ScheduleClass{" +
                "meeting_name='" + meeting_name + '\'' +
                ", meeting_location='" + meeting_location + '\'' +
                ", meeting_date='" + meeting_date + '\'' +
                ", date=" + date +
                '}';
    }
}
