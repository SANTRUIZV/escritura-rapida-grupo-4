package com.univalle.fpoe.escriturarapida.model;

import com.univalle.fpoe.escriturarapida.event.GameListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Game state and rules for "Escritura Rapida".
 * <p>
 * Notifies the registered {@link GameListener}s when a level starts,
 * when the player succeeds or fails, and when the match ends.
 * It does not depend on JavaFX.
 *
 * @author Santiago Ruiz Vanegas
 * @version 1.0
 */
public class GameModel {

    /** Time limit, in seconds, for the first level. */
    public static final double INITIAL_TIME_LIMIT = 20.0;

    /** Seconds subtracted from the time limit every 5 levels. */
    private static final double TIME_DECREMENT = 2.0;

    /** Minimum time limit per level, in seconds. */
    public static final double MIN_TIME_LIMIT = 2.0;

    /** Completed levels needed before the time limit decreases. */
    private static final int LEVELS_PER_DIFFICULTY_STEP = 5;

    /** Source of the random words and phrases. */
    private final WordBank wordBank;

    /** Registered listeners. */
    private final List<GameListener> listeners = new ArrayList<>();

    /** Current level (starts at 1). */
    private int level;

    /** Time limit, in seconds, for the current level. */
    private double currentTimeLimit;

    /** Word or phrase the player must type in the current level. */
    private String currentWord;

    /** Levels completed in the current match. */
    private int levelsCompleted;

    /**
     * Creates a new game model with its own word bank.
     */
    public GameModel() {
        this.wordBank = new WordBank();
        reset();
    }

    /**
     * Registers a listener for the game events.
     *
     * @param listener the listener to add; {@code null} is ignored
     */
    public void addGameListener(GameListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Resets the match: level 1, initial time limit and no completed levels.
     * Call {@link #startLevel()} afterwards to start playing.
     */
    public void reset() {
        this.level = 1;
        this.currentTimeLimit = INITIAL_TIME_LIMIT;
        this.levelsCompleted = 0;
        this.currentWord = null;
    }

    /**
     * Picks a new random word and notifies that the current level started.
     */
    public void startLevel() {
        this.currentWord = wordBank.getRandomWord();
        for (GameListener listener : listeners) {
            listener.onLevelStart(level, currentWord, currentTimeLimit);
        }
    }

    /**
     * Checks the player's answer for the current level.
     * <p>
     * The comparison is exact (letters, spaces, case and punctuation).
     * A wrong answer while there is still time left only reports the
     * error, so the player can try again. The match ends only when the
     * time runs out and the answer is wrong.
     *
     * @param typedAnswer   text written by the player
     * @param timeRemaining seconds left on the clock
     * @param timedOut      {@code true} if the check was triggered because
     *                      the time ran out
     */
    public void submitAnswer(String typedAnswer, double timeRemaining, boolean timedOut) {
        boolean isCorrect = currentWord != null && currentWord.equals(typedAnswer);

        if (isCorrect) {
            handleSuccess();
        } else if (timedOut) {
            handleTimeOut(typedAnswer, timeRemaining);
        } else {
            notifyFailure("Texto incorrecto, intenta de nuevo");
        }
    }

    /**
     * Counts the completed level, moves to the next one and adjusts the
     * difficulty if needed.
     */
    private void handleSuccess() {
        levelsCompleted++;
        for (GameListener listener : listeners) {
            listener.onLevelSuccess(level);
        }

        level++;
        adjustDifficultyIfNeeded();
        startLevel();
    }

    /**
     * Ends the match because the time ran out without a correct answer.
     *
     * @param typedAnswer   text written by the player
     * @param timeRemaining seconds left on the clock
     */
    private void handleTimeOut(String typedAnswer, double timeRemaining) {
        boolean empty = typedAnswer == null || typedAnswer.isEmpty();
        notifyFailure(empty ? "Tiempo agotado, no escribiste nada" : "Tiempo agotado");

        for (GameListener listener : listeners) {
            listener.onGameOver(levelsCompleted, Math.max(0, timeRemaining));
        }
    }

    /**
     * Notifies a failed attempt in the current level.
     *
     * @param reason message describing the failure
     */
    private void notifyFailure(String reason) {
        for (GameListener listener : listeners) {
            listener.onLevelFailure(level, reason);
        }
    }

    /**
     * Lowers the time limit by {@value #TIME_DECREMENT} seconds every
     * {@value #LEVELS_PER_DIFFICULTY_STEP} completed levels, down to
     * {@value #MIN_TIME_LIMIT} seconds.
     */
    private void adjustDifficultyIfNeeded() {
        if (levelsCompleted > 0 && levelsCompleted % LEVELS_PER_DIFFICULTY_STEP == 0) {
            currentTimeLimit = Math.max(MIN_TIME_LIMIT, currentTimeLimit - TIME_DECREMENT);
        }
    }

    /**
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the time limit, in seconds, for the current level
     */
    public double getCurrentTimeLimit() {
        return currentTimeLimit;
    }

    /**
     * @return the word or phrase of the current level
     */
    public String getCurrentWord() {
        return currentWord;
    }

    /**
     * @return the levels completed in the current match
     */
    public int getLevelsCompleted() {
        return levelsCompleted;
    }
}
