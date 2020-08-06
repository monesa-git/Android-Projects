package com.helloworld.inclass08;

import java.io.Serializable;

public class Emails implements Serializable {
    String sender_fname, sender_lname, id, sender_id, receiver_id, message, subject, created_at, updated_at;


    @Override
    public String toString() {
        return "Emails{" +
                "sender_fname='" + sender_fname + '\'' +
                ", sender_lname='" + sender_lname + '\'' +
                ", id='" + id + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", receiver_id='" + receiver_id + '\'' +
                ", message='" + message + '\'' +
                ", subject='" + subject + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}


//        "sender_fname": "Sai",
//                "sender_lname": "Krishna",
//                "id": "111",
//                "sender_id": "192",
//                "receiver_id": "191",
//                "message": "Hi",
//                "subject": "Hello2",
//                "created_at": "2020-03-11 23:55:56",
//                "updated_at": "2020-03-11 23:55:56"