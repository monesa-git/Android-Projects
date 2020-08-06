package com.helloworld.inclass07;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class QuizData implements Serializable {
    String id, text, image, answer;
    ArrayList<String> choices = new ArrayList<>();

    @Override
    public String toString() {
        return "QuizData{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", answer='" + answer + '\'' +
                ", choices=" + choices.toString() +
                '}';
    }
}
