package elements;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

import graphics.*;

public class Controller implements EventHandler<KeyEvent> {
    final private static double FRAMES_PER_SECOND = 8.0;

    //load the IDs from the FXML file
    @FXML private Label scoreLabel; 
    @FXML private Label lifesLabel;
    @FXML private Label levelLabel;
    @FXML private Label centerLabel;
    @FXML private Label headerLabel;
    @FXML private BorderPane borderPane;
    @FXML private PacManView pacManView;

    private static final String[] levelFiles = {"src/res/levels/level1.txt", "src/res/levels/level1.txt", "src/res/levels/level1.txt"}; //todos os niveis são iguais

    private PacManModel pacManModel;
    private Timer timer;
    private static int ghostEatingModeCounter;
    private boolean paused;
    private static int frameCount;

    /**
     * initializes the Controller unpausing the game
     */
    public Controller() {
        this.paused = false;
    }

    /**
     * Initialize and update the model and view from the first txt file and starts the timer.
     */
    public void initialize() {
        this.pacManModel = new PacManModel();
        this.updateController(PacManModel.Direction.NONE);
        ghostEatingModeCounter = 25;
        this.startTimer();
    }

    /**
     * Schedules the model to update based on the timer.
     */
    private void startTimer() {
        this.timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        updateController(pacManModel.getCurrentDirection());
                        frameCount++;
                    }
                });
            }
        };

        long frameTimeInMilliseconds = (long)(1000.0 / FRAMES_PER_SECOND);
        this.timer.schedule(timerTask, 0, frameTimeInMilliseconds);
    }

    /**
     * Steps the PacManModel, updates the view, updates score and level, displays Game Over/You Won, and instructions of how to play
     * @param direction the most recently inputted direction for PacMan to move in
     */
    private void updateController(PacManModel.Direction direction) {
        this.pacManModel.step(direction);
        this.pacManView.updateView(pacManModel);

        Image headerImage = new Image(getClass().getResourceAsStream("/res/images/Pac_man_CM.png"), 560,105,false,false);
        ImageView headerCM = new ImageView(headerImage);
        this.headerLabel.setGraphic(headerCM);

        switch (pacManModel.getLevel()) {
            case 1:
                this.borderPane.setStyle("-fx-background-color: #3E71AF; -fx-padding: 10px, 5px, 10px, 5px");
                break;
            case 2:
                this.borderPane.setStyle("-fx-background-color: #99CC66; -fx-padding: 10px, 5px, 10px, 5px");
                break;
            case 3:
                this.borderPane.setStyle("-fx-background-color: #CA615A; -fx-padding: 10px, 5px, 10px, 5px");
                break;
        }

        this.scoreLabel.setText(String.format("Pontos: %d", this.pacManModel.getScore()));
        this.levelLabel.setText(String.format("Nivel: %d", pacManModel.getLevel()));

        Text life = new Text("Vidas: ");
        life.setStyle("-fx-font-size: 150%");
        life.setFill(Color.WHITE);
        Image heart = new Image(getClass().getResourceAsStream("/res/images/heart.png"), 18, 15, false, false);

        ImageView[] hearts = new ImageView[4];
        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new ImageView(heart);
        }
        TextFlow lifeText = new TextFlow(life);
        for(int i = 0; i < this.pacManModel.getLifes(); i++){
            lifeText.getChildren().add(hearts[i]);
        }

        this.centerLabel.setGraphic(lifeText);
        
        if (pacManModel.isGameOver()) {
            Text gameOver = new Text("FIM DE JOGO");
            gameOver.setStyle("-fx-font-size: 150%");
            gameOver.setFill(Color.RED);
            this.centerLabel.setGraphic(gameOver);
            pause();
        }
        if (pacManModel.isYouWon()) {
            Text youWon = new Text("VOCÊ VENCEU!!");
            youWon.setStyle("-fx-font-size: 150%");
            youWon.setFill(Color.GREEN);
            this.centerLabel.setGraphic(youWon);
        }
        //when PacMan is in ghostEatingMode, count down the ghostEatingModeCounter to reset ghostEatingMode to false when the counter is 0
        if (pacManModel.isGhostEatingMode()) {
            ghostEatingModeCounter--;
        }
        if (ghostEatingModeCounter == 0 && pacManModel.isGhostEatingMode()) {
            pacManModel.setGhostEatingMode(false);
        }
    }

    /**
     * Takes in user keyboard input to control the movement of PacMan and start new games
     * @param keyEvent user's key click
     */
    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        KeyCode code = keyEvent.getCode();
        PacManModel.Direction direction = PacManModel.Direction.NONE;
        if (code == KeyCode.LEFT) {
            direction = PacManModel.Direction.LEFT;
        } else if (code == KeyCode.RIGHT) {
            direction = PacManModel.Direction.RIGHT;
        } else if (code == KeyCode.UP) {
            direction = PacManModel.Direction.UP;
        } else if (code == KeyCode.DOWN) {
            direction = PacManModel.Direction.DOWN;
        } else if (code == KeyCode.G) { 
            pause();
            this.pacManModel.startNewGame();
            this.centerLabel.setText(String.format(""));
            unpause();
        } else if (code == KeyCode.ESCAPE) { 
            if(!paused){
                pause();
            } else {
                unpause();
            }
        }else {
            keyRecognized = false;
        }
        if (keyRecognized) {
            keyEvent.consume();
            pacManModel.setCurrentDirection(direction);
        }
    }

    /**
     * Pause the game
     */
    public void pause() {
        this.timer.cancel();
        this.paused = true;
    }

    /**
     * Unpause the game
     */
    public void unpause() {
        this.startTimer();
        this.paused = false;
    }


    //#region Getters
    /**
     * The width of the playable area
     * @return The width of the board in pixels
     */
    public double getBoardWidth() { return PacManView.CELL_WIDTH * this.pacManView.getColumnCount(); }

    /**
     * The height of the playable area
     * @return The height of the board in pixels
     */
    public double getBoardHeight() {return PacManView.CELL_WIDTH * this.pacManView.getRowCount();}

    /**
     * Frames counter until end of ghost eating mode
     * @return Number of frames until the end of ghost eating mode
     */
    public static int getGhostEatingModeCounter() {return ghostEatingModeCounter;}

    /**
     * Number of frames since the start of the game
     * @return Current frame number
     */
    public static int getFrameCount() {return frameCount;}

    /**
     * The file that represents the X level
     * @param x The level number
     * @return A string with the path of the level file
     */
    public static String getLevelFile(int x){return levelFiles[x];}

    /**
     * If the game is playable
     * @return a boolean that tells if the game is paused or not
     */
    public boolean getPaused() {return paused;}
    //#endregion

    //#region Setters
    /**
     * Set the duration of the Ghost Eating mode based on the current level
     * @param level the current level
     */
    public static void setGhostEatingModeCounter(int level) {
        ghostEatingModeCounter = (8 - level) * (int)FRAMES_PER_SECOND; //7segundos nivel 1, 6 segundos nivel 2, 5 segundos nivel 3
    }
    //#endregion
}
