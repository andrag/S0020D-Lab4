package com.example.anders.drawingapp;

/**
 * Created by Anders on 2016-03-15.
 */
public class ScoreObject implements Comparable<ScoreObject>{

    public String date;
    public int totalScore, score, finishTime;

    public ScoreObject(int totalScore, long finishTime, int score, String date){
        this.totalScore = totalScore;
        this.finishTime = (int)finishTime;
        this.score = score;
        this.date = date;

    }

    @Override
    public int compareTo(ScoreObject another) {
        if(totalScore < another.totalScore) return -1;
        else if(totalScore == another.totalScore) return 0;
        else return 1;
    }
}
