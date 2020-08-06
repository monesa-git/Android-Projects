package com.helloworld.inclass11;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Expense implements Serializable {

    String title, category;
    String documentId;
    String date;
    double cost;


    public Expense(String title, String category, String documentId, double cost, String date) {
        this.title = title;
        this.category = category;
        this.documentId = documentId;
        this.cost = cost;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDocumentId() {
        return documentId;
    }

    public double getCost() {
        return cost;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Expense{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", documentId='" + documentId + '\'' +
                ", cost=" + cost + '\'' +
                ",date=" + date +
                '}';
    }

    public HashMap<String, Object> toHasMap(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title",this.getTitle());
        hashMap.put("category",this.getCategory());
        hashMap.put("cost",this.getCost());
        hashMap.put("date",this.getDate());
        return hashMap;
    }


}
