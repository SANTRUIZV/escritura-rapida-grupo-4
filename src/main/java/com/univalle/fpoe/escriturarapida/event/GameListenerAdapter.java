package com.univalle.fpoe.escriturarapida.event;

/**
 * Adapter class for {@link GameListener}.
 * <p>
 * All the methods are empty, so a subclass only needs to override
 * the events it uses.
 *
 * @author Santiago Ruiz Vanegas
 * @version 1.0
 */
public abstract class GameListenerAdapter implements GameListener {

    @Override
    public void onLevelStart(int level, String word, double timeLimit) {
    }

    @Override
    public void onLevelSuccess(int level) {
    }

    @Override
    public void onLevelFailure(int level, String reason) {
    }

    @Override
    public void onGameOver(int levelsCompleted, double timeRemaining) {
    }
}
