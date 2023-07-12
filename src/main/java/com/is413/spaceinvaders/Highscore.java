package com.is413.spaceinvaders;

import java.io.*;
import java.util.ArrayList;

public class Highscore {
    static ArrayList<Score> highScoreList = new ArrayList<Score>();     //Arraylist storing player score objects
    final static int numScores = 5;                                 //Predefined number of high scores to store

    String filename;                                                //Filename of high score text file

    void newHighScore(int newScore, String name) {                  //This method adds new high score to correct location in list, deletes extra high scores, and calls method to write new scores to text file
        Score nScore = new Score(name, newScore);                   //Make new score object to store new high score

        for (int i = 0; i < highScoreList.size(); i++) {            //Traverse through highScore arraylist
            if (newScore > highScoreList.get(i).score) {            //If new high score is higher than current
                highScoreList.add(i, nScore);                       //Add high score at current index
                boolean done = false;                               //Run loop to remove extraneous high scores
                while (!done) {
                    if (highScoreList.size() > numScores) { //If the size of highScoreList is larger than the numScores we want to store,
                        highScoreList.remove(highScoreList.size()-1); //Remove the last element until the size is correct
                    } else {
                        done = true;
                    }
                }
                break;
            }
        }
        writeToFile();          //Write new high scores list to text file
    }

    void writeToFile() {        //This function simply iterates though highScores list and writes them to text file
        try {
            File scoreFile = new File(filename);
            BufferedWriter scoreWriter = new BufferedWriter(new FileWriter(scoreFile));
            for (Score score: highScoreList) {
                scoreWriter.write(score + "\n");
            }
            scoreWriter.flush();
            scoreWriter.close();

        } catch (IOException ex ) {

        }
    }

    Highscore(String filename) throws IOException {     //When new highscore object is created, open text file, read scores, and store them to arraylist
        this.filename = filename;
        File scoreFile = new File(filename);
        if (!scoreFile.exists()) {
            System.out.print("High Score file not found, creating new one: ");
            if (scoreFile.createNewFile()) {
                System.out.println("Success");
            } else {
                System.out.println("Failed to create new high score file");
            }
        }

        BufferedReader scoreReader = new BufferedReader(new FileReader(scoreFile));

        for (int i = 0; i < numScores; i++) {
            try {
                String lineString = scoreReader.readLine();
                String[] lineArray = lineString.split(",");
                highScoreList.add(new Score(lineArray[0], Integer.valueOf(lineArray[1].trim())));
            } catch (NullPointerException ex) {
                highScoreList.add(new Score(" ", 0));
            }
        }
    }
}
