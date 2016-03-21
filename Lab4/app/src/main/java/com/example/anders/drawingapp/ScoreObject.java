package com.example.anders.drawingapp;

import java.io.Serializable;

/**
 * Created by Anders on 2016-03-15.
 */
public class ScoreObject implements Comparable<ScoreObject>, Serializable{

    public String time;
    public int totalScore, score, finishTime;

    public ScoreObject(int totalScore, long finishTime, int score, String time){
        this.totalScore = totalScore;
        this.finishTime = (int)finishTime;
        this.score = score;
        this.time = time;

    }

    @Override
    public int compareTo(ScoreObject another) {
        if(totalScore < another.totalScore) return -1;
        else if(totalScore == another.totalScore) return 0;
        else return 1;
    }
}
