package com.is413.spaceinvaders;

//This class just acts as an object to store player scores + player names
public class Score {
    int score;
    String name;

    Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + "," + score;
    }
}
