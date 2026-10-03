package com.univalle.fpoe.escriturarapida.model;

import com.univalle.fpoe.escriturarapida.event.GameListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the game state and business rules for "Escritura Rapida"
 * and notifies every registered {@link GameListener} whenever a
 * relevant event occurs (level start, success, failure or game over).
 * <p>
 * This class is intentionally independent from JavaFX: it does not
 * import any UI class, so it can be tested or reused regardless of
 * how the interface is rendered. All the JavaFX wiring lives in the
 * controller layer.
 *
 * @author FPOE Team
 * @version 1.0
 */
public class GameModel {

    /** Time limit, in seconds, used for the very first level. */
    public static final double INITIAL_TIME_LIMIT = 20.0;

    /** Amount of seconds subtracted from the time limit every 5 levels. */
    private static final double TIME_DECREMENT = 2.0;

    /** Lower bound for the time limit, no matter how far the player advances. */
    public static final double MIN_TIME_LIMIT = 2.0;

    /** How many completed levels must pass before the time limit decreases again. */
    private static final int LEVELS_PER_DIFFICULTY_STEP = 5;

    /** Source of random words/phrases shown to the player. */
    private final WordBank wordBank;

    /** Listeners subscribed to this model's events. */
    private final List<GameListener> listeners = new ArrayList<>();

    /** Current level number (1-based). */
    private int level;

    /** Time limit, in seconds, applicable to the current level. */
    private double currentTimeLimit;

    /** Word or phrase the player must type in the current level. */
    private String currentWord;

    /** Total number of levels successfully completed in this match. */
    private int levelsCompleted;

    /**
     * Creates a new game model with its own {@link WordBank} and
     * resets it to its initial state.
     */
    public GameModel() {
        this.wordBank = new WordBank();
        reset();
    }

    /**
     * Registers a listener that will be notified about future game events.
     *
     * @param listener the listener to add; {@code null} values are ignored
     */
    public void addGameListener(GameListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Resets every piece of state to start a brand-new match:
     * level 1, full time limit and zero completed levels. Does not,
     * by itself, notify listeners; call {@link #startLevel()} afterwards.
     */
    public void reset() {
        this.level = 1;
        this.currentTimeLimit = INITIAL_TIME_LIMIT;
        this.levelsCompleted = 0;
        this.currentWord = null;
    }

    /**
     * Starts the current level: picks a new random word/phrase and
     * notifies every listener through {@link GameListener#onLevelStart}.
     */
    public void startLevel() {
        this.currentWord = wordBank.getRandomWord();
        for (GameListener listener : listeners) {
            listener.onLevelStart(level, currentWord, currentTimeLimit);
        }
    }

    /**
     * Evaluates the player's answer for the current level.
     * <p>
     * Comparison is exact (case, spaces and punctuation all matter, per
     * HU-1's acceptance criteria). On success the level and, every
     * {@value #LEVELS_PER_DIFFICULTY_STEP} completed levels, the
     * difficulty (time limit) are increased; on failure the match ends.
     *
     * @param typedAnswer   the text currently written by the player
     * @param timeRemaining the time, in seconds, left on the clock at the
     *                      moment of validation (used for reporting only)
     * @param timedOut      {@code true} if the validation was triggered
     *                      because the timer reached zero
     */
    public void submitAnswer(String typedAnswer, double timeRemaining, boolean timedOut) {
        boolean isCorrect = currentWord != null && currentWord.equals(typedAnswer);

        if (isCorrect) {
            handleSuccess();
        } else {
            String reason = timedOut ? "Tiempo agotado" : "Texto incorrecto";
            handleFailure(reason, timeRemaining);
        }
    }

    /**
     * Applies the success path: increases the completed-level counter,
     * advances the level, adjusts the difficulty and starts the next level.
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
     * Applies the failure path: notifies the failure and then the
     * game-over summary, ending the current match.
     *
     * @param reason        human readable failure reason
     * @param timeRemaining time left on the clock when the failure happened
     */
    private void handleFailure(String reason, double timeRemaining) {
        for (GameListener listener : listeners) {
            listener.onLevelFailure(level, reason);
        }
        for (GameListener listener : listeners) {
            listener.onGameOver(levelsCompleted, Math.max(0, timeRemaining));
        }
    }

    /**
     * Reduces the time limit by {@value #TIME_DECREMENT} seconds every
     * {@value #LEVELS_PER_DIFFICULTY_STEP} completed levels, never going
     * below {@value #MIN_TIME_LIMIT} seconds, as required by HU-3.
     */
    private void adjustDifficultyIfNeeded() {
        if (levelsCompleted > 0 && levelsCompleted % LEVELS_PER_DIFFICULTY_STEP == 0) {
            currentTimeLimit = Math.max(MIN_TIME_LIMIT, currentTimeLimit - TIME_DECREMENT);
        }
    }

    /**
     * @return the current level number (1-based)
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
     * @return the word or phrase the player must type in the current level
     */
    public String getCurrentWord() {
        return currentWord;
    }

    /**
     * @return the total number of levels completed so far in this match
     */
    public int getLevelsCompleted() {
        return levelsCompleted;
    }
}
