package com.univalle.fpoe.escriturarapida.event;

/**
 * Listener for the events of an "Escritura Rapida" match.
 * <p>
 * The {@code GameModel} fires these events and the controller
 * implements them to update the user interface.
 *
 * @author Santiago Ruiz Vanegas
 * @version 1.0
 */
public interface GameListener {

    /**
     * Called when a new level starts.
     *
     * @param level     the level that starts
     * @param word      the word or phrase the player must type
     * @param timeLimit the time limit for this level, in seconds
     */
    void onLevelStart(int level, String word, double timeLimit);

    /**
     * Called when the player types the word correctly.
     *
     * @param level the level that was completed
     */
    void onLevelSuccess(int level);

    /**
     * Called when an answer is wrong. If there is still time left the
     * player can keep trying; if the time ran out, {@link #onGameOver}
     * is called right after.
     *
     * @param level  the current level
     * @param reason message describing the failure
     */
    void onLevelFailure(int level, String reason);

    /**
     * Called when the match ends because the time ran out without a
     * correct answer.
     *
     * @param levelsCompleted levels completed in the match
     * @param timeRemaining   seconds left on the clock
     */
    void onGameOver(int levelsCompleted, double timeRemaining);
}
