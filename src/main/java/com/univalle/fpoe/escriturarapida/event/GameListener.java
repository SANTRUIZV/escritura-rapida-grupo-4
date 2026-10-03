package com.univalle.fpoe.escriturarapida.event;

/**
 * Contract for objects that want to be notified about relevant
 * events that occur during a "Escritura Rapida" (Speed Typing) match.
 * <p>
 * This interface is the core of the custom, event-driven communication
 * between the {@code GameModel} (event source) and the
 * {@code GameController} (event consumer). Implementing this interface
 * decouples the game logic from the JavaFX user interface.
 *
 * @author FPOE Team
 * @version 1.0
 */
public interface GameListener {

    /**
     * Invoked every time a new level starts and a new word/phrase
     * must be displayed to the player.
     *
     * @param level     the level number that is starting (1-based)
     * @param word      the random word or phrase the player must type
     * @param timeLimit the time limit, in seconds, available for this level
     */
    void onLevelStart(int level, String word, double timeLimit);

    /**
     * Invoked when the player types the exact word/phrase before the
     * time runs out.
     *
     * @param level the level number that was just completed
     */
    void onLevelSuccess(int level);

    /**
     * Invoked when the player fails a level, either because the typed
     * text does not match the target word/phrase or because time ran out.
     *
     * @param level  the level number in which the failure occurred
     * @param reason a short, human readable description of the failure
     */
    void onLevelFailure(int level, String reason);

    /**
     * Invoked once the match is over (i.e. after a failed level),
     * providing a summary of the player's performance.
     *
     * @param levelsCompleted the total amount of levels successfully completed
     * @param timeRemaining   the time remaining on the failed level, in seconds
     */
    void onGameOver(int levelsCompleted, double timeRemaining);
}
