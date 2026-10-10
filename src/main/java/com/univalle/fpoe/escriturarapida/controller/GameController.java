package com.univalle.fpoe.escriturarapida.controller;

import com.univalle.fpoe.escriturarapida.event.GameListenerAdapter;
import com.univalle.fpoe.escriturarapida.model.GameModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller that connects the JavaFX view ({@code game-view.fxml}) with the
 * game model.
 * <p>
 * The view contains three screens stacked in the same FXML file (start, game
 * and summary). This controller shows only one of them at a time, listens to
 * the events fired by {@link GameModel}, handles keyboard, mouse and button
 * events, and drives the countdown timer of each level.
 *
 * @author Juan Manuel Cadena
 * @version 1.0
 */
public class GameController {

    /** Seconds between two ticks of the countdown. */
    private static final double TICK_SECONDS = 1.0;

    /** Start screen with the rules and the start button. */
    @FXML
    private VBox startScreen;

    /** Screen where the match is played. */
    @FXML
    private VBox gameScreen;

    /** Screen shown when the match ends. */
    @FXML
    private VBox summaryScreen;

    /** Label that displays the current level. */
    @FXML
    private Label levelLabel;

    /** Label that displays the word or phrase to type. */
    @FXML
    private Label wordLabel;

    /** Label that displays the remaining time. */
    @FXML
    private Label timerLabel;

    /** Label that displays feedback messages during the match. */
    @FXML
    private Label feedbackLabel;

    /** Label that displays the reason why the match ended. */
    @FXML
    private Label summaryReasonLabel;

    /** Label that displays the levels completed in the summary. */
    @FXML
    private Label summaryLevelsLabel;

    /** Label that displays the remaining time in the summary. */
    @FXML
    private Label summaryTimeLabel;

    /** Text field where the player types the answer. */
    @FXML
    private TextField answerField;

    /** Progress bar that represents the remaining time. */
    @FXML
    private ProgressBar timeProgress;

    /** Button that starts the match. */
    @FXML
    private Button startButton;

    /** Button that validates the player's answer. */
    @FXML
    private Button validateButton;

    /** Button that restarts the match from level one. */
    @FXML
    private Button restartButton;

    /** Button that closes the application. */
    @FXML
    private Button exitButton;

    /** Game model containing the rules and the state of the match. */
    private final GameModel gameModel = new GameModel();

    /** Countdown timeline; {@code null} when no countdown is running. */
    private Timeline timer;

    /** Remaining seconds in the current level. */
    private double timeRemaining;

    /** Time limit, in seconds, of the current level. */
    private double levelTimeLimit;

    /** Indicates whether the current match has ended. */
    private boolean gameOver;

    /** Indicates that the previous level was completed, to show a positive message. */
    private boolean levelWasCompleted;

    /** Last failure message reported by the model, used in the final summary. */
    private String lastFailureReason = "Tiempo agotado";

    /**
     * Listener that receives the model events and updates the controls.
     * It extends the adapter so only the needed events are overridden.
     */
    private final GameListenerAdapter gameListener = new GameListenerAdapter() {

        /**
         * Shows the game screen, updates the labels and starts the countdown.
         *
         * @param level     the level that starts
         * @param word      the word or phrase to type
         * @param timeLimit the time limit of the level, in seconds
         */
        @Override
        public void onLevelStart(int level, String word, double timeLimit) {
            showGameScreen();
            levelLabel.setText("Nivel " + level);
            wordLabel.setText(word);
            feedbackLabel.setText(levelWasCompleted
                    ? "¡Correcto! ¡Nivel superado! Comienza el siguiente nivel."
                    : "Escribe exactamente lo que aparece.");
            levelWasCompleted = false;

            levelTimeLimit = timeLimit;
            timeRemaining = timeLimit;
            gameOver = false;

            answerField.clear();
            answerField.setDisable(false);
            validateButton.setDisable(false);
            updateTimerView();
            startTimer();
            answerField.requestFocus();
        }

        /**
         * Remembers that the level was completed so the next level shows a
         * positive message.
         *
         * @param level the level that was completed
         */
        @Override
        public void onLevelSuccess(int level) {
            feedbackLabel.setText("¡Correcto! Pasando al siguiente nivel...");
            levelWasCompleted = true;
        }

        /**
         * Shows the failure message; the match continues if there is time left.
         *
         * @param level  the current level
         * @param reason message describing the failure
         */
        @Override
        public void onLevelFailure(int level, String reason) {
            lastFailureReason = reason;
            feedbackLabel.setText(reason);
        }

        /**
         * Stops the countdown and shows the summary screen.
         *
         * @param levelsCompleted levels completed in the match
         * @param remaining       seconds left on the clock
         */
        @Override
        public void onGameOver(int levelsCompleted, double remaining) {
            stopTimer();
            gameOver = true;
            summaryReasonLabel.setText(lastFailureReason);
            summaryLevelsLabel.setText(String.valueOf(levelsCompleted));
            summaryTimeLabel.setText(String.format("%.1f s", Math.max(0, remaining)));
            showSummaryScreen();
        }
    };

    /**
     * Initializes the controller after the FXML file has been loaded.
     * Registers the model listener and all the event handlers.
     */
    @FXML
    private void initialize() {
        gameModel.addGameListener(gameListener);

        startButton.setOnAction(event -> startGame());
        validateButton.setOnAction(event -> validateAnswer());
        restartButton.setOnAction(event -> restartGame());
        exitButton.setOnAction(event -> exitGame());

        startScreen.addEventFilter(KeyEvent.KEY_PRESSED, this::handleStartKey);
        gameScreen.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGameKey);
        summaryScreen.addEventFilter(KeyEvent.KEY_PRESSED, this::handleSummaryKey);

        gameScreen.addEventHandler(MouseEvent.MOUSE_CLICKED, new FocusAnswerFieldHandler());

        showStartScreen();
    }

    /**
     * Starts a new match from level one.
     */
    private void startGame() {
        stopTimer();
        gameModel.reset();
        levelWasCompleted = false;
        lastFailureReason = "Tiempo agotado";
        gameModel.startLevel();
    }

    /**
     * Sends the current answer to the model for validation.
     */
    private void validateAnswer() {
        if (gameOver) {
            return;
        }
        gameModel.submitAnswer(answerField.getText(), timeRemaining, false);
    }

    /**
     * Restarts the match from level one.
     */
    private void restartGame() {
        startGame();
    }

    /**
     * Closes the main window.
     */
    private void exitGame() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Starts the match when Enter is pressed on the start screen.
     *
     * @param event keyboard event
     */
    private void handleStartKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            startGame();
            event.consume();
        }
    }

    /**
     * Validates the answer when Enter is pressed during the match.
     *
     * @param event keyboard event
     */
    private void handleGameKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validateAnswer();
            event.consume();
        }
    }

    /**
     * Restarts the match when Enter is pressed on the summary screen, unless
     * the exit button has the focus (in that case Enter closes the window).
     *
     * @param event keyboard event
     */
    private void handleSummaryKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (exitButton.isFocused()) {
                exitGame();
            } else {
                restartGame();
            }
            event.consume();
        }
    }

    /**
     * Creates and starts the countdown of the current level.
     */
    private void startTimer() {
        stopTimer();

        timer = new Timeline(
                new KeyFrame(Duration.seconds(TICK_SECONDS), new TimerEventHandler())
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Subtracts one second and notifies the model when the time reaches zero.
     */
    private void tickTimer() {
        if (gameOver) {
            return;
        }

        timeRemaining = Math.max(0, timeRemaining - TICK_SECONDS);
        updateTimerView();

        if (timeRemaining <= 0) {
            stopTimer();
            gameModel.submitAnswer(answerField.getText(), 0, true);
        }
    }

    /**
     * Updates the timer label and the progress bar.
     */
    private void updateTimerView() {
        timerLabel.setText(String.format("%.0f s", timeRemaining));

        if (levelTimeLimit > 0) {
            timeProgress.setProgress(timeRemaining / levelTimeLimit);
        }
    }

    /**
     * Stops the current countdown, if any.
     */
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Shows the start screen and hides the others.
     */
    private void showStartScreen() {
        setScreenVisible(startScreen, true);
        setScreenVisible(gameScreen, false);
        setScreenVisible(summaryScreen, false);
        Platform.runLater(() -> startScreen.requestFocus());
    }

    /**
     * Shows the game screen and hides the others.
     */
    private void showGameScreen() {
        setScreenVisible(startScreen, false);
        setScreenVisible(gameScreen, true);
        setScreenVisible(summaryScreen, false);
    }

    /**
     * Shows the summary screen and hides the others.
     */
    private void showSummaryScreen() {
        setScreenVisible(startScreen, false);
        setScreenVisible(gameScreen, false);
        setScreenVisible(summaryScreen, true);
        Platform.runLater(() -> restartButton.requestFocus());
    }

    /**
     * Shows or hides a screen, keeping its layout state consistent.
     *
     * @param screen  the screen to change
     * @param visible {@code true} to show it, {@code false} to hide it
     */
    private void setScreenVisible(VBox screen, boolean visible) {
        screen.setVisible(visible);
        screen.setManaged(visible);
    }

    /**
     * Inner event handler used by the countdown timeline.
     */
    private class TimerEventHandler implements EventHandler<ActionEvent> {

        /**
         * Processes one tick of the countdown.
         *
         * @param event timer event
         */
        @Override
        public void handle(ActionEvent event) {
            tickTimer();
        }
    }

    /**
     * Inner mouse handler that gives the focus back to the answer field, so
     * the player can keep typing after clicking on the window.
     */
    private class FocusAnswerFieldHandler implements EventHandler<MouseEvent> {

        /**
         * Requests the focus for the answer field if it is enabled.
         *
         * @param event mouse event
         */
        @Override
        public void handle(MouseEvent event) {
            if (!answerField.isDisabled()) {
                answerField.requestFocus();
            }
        }
    }
}
