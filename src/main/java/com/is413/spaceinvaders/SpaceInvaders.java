package com.is413.spaceinvaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.time.Timer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;


public class SpaceInvaders extends GameApplication {
    static ArrayList<Point2D> enemyWaypoints = new ArrayList<Point2D>();    //Stores waypoints for enemy movement paths
    static int enemyDeathScore = 10;                                        //Base score for enemy deaths

    static Highscore highscore;                                             //Static highscore object to be initialized later

    static boolean armed = true;                                            //This variable is used to limit the fire rate of the missiles, explained later

    final static String highScoreFileName = "highScore.txt";                //Filename of highscore text file
    static int score = 0;                                                   //Static int stores player score to be incremented during gameplay

    static int fireRateMS = 1;                                              //Fire rate of missiles to be adjusted later according to difficutly level

    static Sound fire;
    static Sound explosion;
    public enum Entities {                                                  //Defined entity types used by factory
        SHIP, MISSILE, ENEMY, MISSILE_EXPLOSION, ENEMY_EXPLOSION, SHIP_EXPLOSION, LVLSTAGE
    }

    private final SpaceInvadersFactory entitiesFactory = new SpaceInvadersFactory();        //Initialize entity factory
    private Entity player;                                                                  //Player entity (missle ship)

    public static void main(String[] args) throws IOException {

        enemyWaypoints.add(new Point2D(1000, 0));           //Add predefined waypoints for enemy movement paths
        enemyWaypoints.add(new Point2D(1000, 100));
        enemyWaypoints.add(new Point2D(-100, 100));
        enemyWaypoints.add(new Point2D(-100, 200));
        enemyWaypoints.add(new Point2D(1000, 200));
        enemyWaypoints.add(new Point2D(1000, 300));
        enemyWaypoints.add(new Point2D(-100, 300));
        enemyWaypoints.add(new Point2D(-100, 400));
        enemyWaypoints.add(new Point2D(1000, 400));
        enemyWaypoints.add(new Point2D(1000, 500));
        enemyWaypoints.add(new Point2D(-100, 500));
        enemyWaypoints.add(new Point2D(-100, 600));
        enemyWaypoints.add(new Point2D(1000, 600));
        enemyWaypoints.add(new Point2D(-100, 700));
        enemyWaypoints.add(new Point2D(1000, 700));
        enemyWaypoints.add(new Point2D(1000, 800));
        enemyWaypoints.add(new Point2D(-100, 800));

        highscore = new Highscore(highScoreFileName);  //Initialize new highscore file
        launch(args);
    }



    @Override
    protected void initSettings(GameSettings gameSettings) {

        gameSettings.setWidth(1280);                        //Setup and initialize basic game parameters
        gameSettings.setHeight(800);
        gameSettings.setTitle("Space Invaders");
        gameSettings.setVersion("1.0 Beta");
        gameSettings.setIntroEnabled(false);
        gameSettings.setGameMenuEnabled(true);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {
                return new SpaceInvadersScene(width, height);
            }
        });

    }

    public static class SpaceInvadersScene extends StartupScene {
                                                                    //This class defines the initial startup scene
        public SpaceInvadersScene(int appWidth, int appHeight) {
            super(appWidth, appHeight);
            Rectangle bg = new Rectangle(appWidth, appHeight);
            bg.setFill(Color.BLACK);
            Text name = new Text("Space Invaders");
            name.setFill(Color.WHITE);
            name.setFont(Font.font(60));
            getContentRoot().getChildren().addAll(new StackPane(bg, name));

        }
    }



    @Override
    protected void initInput() {
        onKey(KeyCode.A, "Move Left", () -> player.translateX(-5));     //Define keyboard inputs for driving the ship
        onKey(KeyCode.D, "Move Right", () -> player.translateX(5));
        onKey(KeyCode.SPACE, "Fire Missile", () -> {                        //Define keyboard input for firing the missle

            if (armed) {                                                                        //This is the rate limiting function for firing the missile
                spawn("MISSILE", this.player.getCenter().subtract(150, 0));    //This essentially acts as a 'cooldown' to prevent user from holding down the button and firing too quickly
                getAudioPlayer().playSound(fire);                                               //Play sound of missile firing
                armed = false;                                                                  //Once missile is fired, missile is disarmed (armed = false)
                getGameTimer().runOnceAfter(() -> armed = true, Duration.millis(fireRateMS));   //After a predefined time period determined by difficulty, missile is re-armed
            }
        });
//        Sound music = getAssetLoader().loadSound("music.mp3");

//        getAudioPlayer().playSound(music);
        fire = getAssetLoader().loadSound("fire.wav");
        explosion = getAssetLoader().loadSound("explosion.wav");
        Music music = getAssetLoader().loadMusic("music.mp3");
        getAudioPlayer().loopMusic(music);

    }

    @Override
    protected void initGame() {

        int diffBoxWidth = 300;                                             //This block of code defines the difficulty selection box
        int diffBoxHeight = 100;
        Button easyBtn = getUIFactoryService().newButton("Easy");
        Button medBtn = getUIFactoryService().newButton("Medium");
        Button hardBtn = getUIFactoryService().newButton("Hard");
        easyBtn.setPrefWidth(diffBoxWidth);
        medBtn.setPrefWidth(diffBoxWidth);
        hardBtn.setPrefWidth(diffBoxWidth);
        VBox diffBox = new VBox(10);
        diffBox.setAlignment(Pos.CENTER);
        diffBox.setPrefHeight(diffBoxHeight);
        diffBox.setPrefWidth(diffBoxWidth);
        diffBox.setTranslateX(getAppWidth() / 2 - (diffBoxWidth/2));
        diffBox.setTranslateY(getAppHeight()/2 - (diffBoxHeight/2) + 130);
        diffBox.getChildren().addAll(easyBtn, medBtn, hardBtn);
        diffBox.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(5), Insets.EMPTY)));
        getGameScene().addUINode(diffBox);

        Label diffPrompt = new Label("Select a difficulty:");
        diffPrompt.setTextFill(Color.BLACK);
        diffPrompt.setFont(Font.font(40));
        diffPrompt.setTranslateX(getAppWidth() / 2 - (diffBoxWidth/2));
        diffPrompt.setTranslateY(getAppHeight()/2 - (diffBoxHeight/2) + 80);
        getGameScene().addUINode(diffPrompt);




        int scoreBoxWidth = 300;                                //This block of code adds the stored high scores to the game layout
        int scoreBoxHeight = 150;
        VBox scoreBox = new VBox(2);
        scoreBox.setPrefWidth(scoreBoxWidth);
        scoreBox.setPrefHeight(scoreBoxHeight);
        scoreBox.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(5), Insets.EMPTY)));
        scoreBox.setTranslateX(getAppWidth() / 2 - (scoreBoxWidth/2));
        scoreBox.setTranslateY(getAppHeight() / 2 - (diffBoxHeight/2) - 250);
        int scoreFontSize = 20;
        Color scoreFontColor = Color.WHITE;

        for (int i=0; i < Highscore.numScores; i++) {
            BorderPane score1 = new BorderPane();
            score1.setPadding(new Insets(10));
            score1.setPrefWidth(scoreBoxWidth);
            Label score1Name = new Label(highscore.highScoreList.get(i).name);
            score1Name.setAlignment(Pos.CENTER_LEFT);
            score1Name.setFont(Font.font(scoreFontSize));
            score1Name.setTextFill(scoreFontColor);
            Label score1Score = new Label(String.valueOf(highscore.highScoreList.get(i).score));
            score1Score.setAlignment(Pos.CENTER_RIGHT);
            score1Score.setFont(Font.font(scoreFontSize));
            score1Score.setTextFill(scoreFontColor);
            score1.setLeft(score1Name);
            score1.setRight(score1Score);
            scoreBox.getChildren().addAll(score1);

        }
        getGameScene().addUINode(scoreBox);

        Label highScorePrompt = new Label("High Scores:");
        highScorePrompt.setTextFill(Color.BLACK);
        highScorePrompt.setFont(Font.font(40));
        highScorePrompt.setTranslateX(getAppWidth() / 2 - (diffBoxWidth/2) + 20);
        highScorePrompt.setTranslateY(getAppHeight()/2 - (diffBoxHeight/2) - 300);
        getGameScene().addUINode(highScorePrompt);

        getGameWorld().addEntityFactory(entitiesFactory);       //Add entities factory so we can begin spawning entities into the game

        Label scoreLabel = new Label("Score: " + score);    //Add label for player score to the top-right of the screen
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font(60));
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        getGameScene().addGameView(new GameView(scoreLabel, 100));


        Timer scoreUpdater = getGameTimer();                //This function updates the score text from the score variable every 100 millis
        scoreUpdater.runAtInterval(() -> {
            scoreLabel.setText("Score: " + score);
        }, Duration.millis(100));


        easyBtn.setOnAction(e -> {                          //On action for difficulty button defines parameters and passes them on to the game start function
            fireRateMS = 250;                               //Fire rate of missiles is set in global variable and read by above fire rate limiting code
            armed = true;
            try {
                start(1000, 300, 1);        //parameters such as rate of enemy spawn, enemy movement speed, enemy death score multiplier are adjusted based on difficulty level
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        medBtn.setOnAction(e -> {
            fireRateMS = 500;
            armed = true;
            try {
                start(750, 500, 2.5);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        });

        hardBtn.setOnAction(e -> {
            fireRateMS = 500;
            armed = true;
            try {
                start(500, 600, 5);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });






    }

    private void start(int spawnRateMS, int moveSpeed, double scoreMultiplier) throws InterruptedException {
        score = 0;                                                      //set score to 0 at start of game
        SpawnData data = new SpawnData();                               //define a new data set which we will fill with data to pass to the spawn function
        data.put("x", Double.valueOf(500));                          //spawn position of enemy vehicles
        data.put("moveSpeed", moveSpeed);                               //movement speed of enemy vehicles
        data.put("scoreMultiplier", scoreMultiplier);                   //score multiplier for enemy deaths
        getGameScene().clearUINodes();                                  //Clear UI nodes (high score box and difficulty selection box) from the game scene
        spawn("LVLSTAGE");                                    //This just spawns the background image as a passive game entity
        this.player = spawn("SHIP", getAppWidth()/2, getAppHeight() - getAppHeight()/10);           //Spawn the player entity at a predefined location
//        run(() -> spawn("ENEMY", data), Duration.millis(spawnRateMS), 10);
//        getGameWorld().getEntitiesByType(Entities.ENEMY);
        run(() -> spawn("ENEMY", data), Duration.millis(spawnRateMS));      //Indefinitely run lambda function to spawn enemies at predefined spawn rate until player dies
    }

    @Override
    protected void initPhysics() {

        onCollisionBegin(Entities.MISSILE, Entities.ENEMY, (missile, enemy) -> {        //Following lambda function is run when a missile contacts the enemy ship
            SpawnData missileData = new SpawnData();                                    //Create new spawndata object to store location of missle at point of contact
            Point2D missilePos = missile.getPosition();                                 //get current position of missile
            missileData.put("pos", missilePos);                                         //Store missile position in spawnData object
            missile.removeFromWorld();                                                  //Delete missle from world
            spawn("MISSILE_EXPLOSION", missileData);                          //Spawn a missile explosion entity at the previously stored location of the deleted missile
                                                                                        //This essentially makes it look like enemy ship and missile disappear in a firey explosion
            getAudioPlayer().playSound(explosion);                                     //play Explosion sound
//            SpawnData enemyData = new SpawnData();
//            Point2D enemyPos = enemy.getPosition();
//            enemyData.put("pos", enemyPos);
            enemy.removeFromWorld();                                                     //Delete enemy from world
            score += enemyDeathScore*enemy.getDouble("scoreMultiplier");            //Increment player score by base enemy death score rate by scoreMultiplier (adjust based on difficulty)

//            spawn("ENEMY_EXPLOSION", enemyData);

        });

        onCollisionBegin(Entities.ENEMY, Entities.SHIP, (enemy, ship) -> {              //This lambda function runs if the enemy ship contacts the player ship (game over)

            if (score >= Highscore.highScoreList.get(Highscore.highScoreList.size()-1).score) {  //If current score is higher than the last score on the list (lowest score)
                getDialogService().showInputBox("Game Over! Your score: " + score + "\nNew High Score! Enter name:", name -> {
                    highscore.newHighScore(score, name);                                        //Call newHighScore method and pass score + player name
                    getGameController().startNewGame();
                });
            } else {
                showMessage("Game Over! Your Score: " + score, () -> { //If player score is not high enough to meet high score
                    getGameController().startNewGame();
                });
            }
        });
    }



}

