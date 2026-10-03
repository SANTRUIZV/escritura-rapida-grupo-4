package com.univalle.fpoe.escriturarapida.event;

/**
 * Adapter class for {@link GameListener}.
 * <p>
 * Following the classic Adapter pattern used across the JDK
 * (e.g. {@code java.awt.event.WindowAdapter}, {@code MouseAdapter}),
 * this class provides empty, no-op implementations for every method
 * declared in {@link GameListener}. Consumers that only care about a
 * subset of the game events can extend this class instead of
 * implementing the whole interface, overriding only the methods they
 * actually need.
 *
 * @author FPOE Team
 * @version 1.0
 */
public abstract class GameListenerAdapter implements GameListener {

    /** {@inheritDoc} Default implementation does nothing. */
    @Override
    public void onLevelStart(int level, String word, double timeLimit) {
        // Intentionally left blank - to be overridden by subclasses if needed.
    }

    /** {@inheritDoc} Default implementation does nothing. */
    @Override
    public void onLevelSuccess(int level) {
        // Intentionally left blank - to be overridden by subclasses if needed.
    }

    /** {@inheritDoc} Default implementation does nothing. */
    @Override
    public void onLevelFailure(int level, String reason) {
        // Intentionally left blank - to be overridden by subclasses if needed.
    }

    /** {@inheritDoc} Default implementation does nothing. */
    @Override
    public void onGameOver(int levelsCompleted, double timeRemaining) {
        // Intentionally left blank - to be overridden by subclasses if needed.
    }
}
