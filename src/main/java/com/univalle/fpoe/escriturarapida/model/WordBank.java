package com.univalle.fpoe.escriturarapida.model;

import java.util.List;
import java.util.Random;

/**
 * Provides random words and short phrases that the player must type
 * during the "Escritura Rapida" game.
 * <p>
 * The bank mixes single words (early levels feel) with short phrases,
 * both selected uniformly at random so that every level shows an
 * unpredictable challenge, as required by HU-1.
 *
 * @author FPOE Team
 * @version 1.0
 */
public class WordBank {

    /** Pool of words and phrases available to be shown to the player. */
    private static final List<String> POOL = List.of(
            "java",
            "evento",
            "teclado",
            "mouse",
            "ventana",
            "compilar",
            "excepcion",
            "interfaz",
            "adaptador",
            "programacion",
            "clase interna",
            "controlador",
            "escritura rapida",
            "manejo de eventos",
            "diseno de interfaces",
            "javafx es genial",
            "el tiempo se agota",
            "presiona enter para validar",
            "la practica hace al maestro",
            "cada nivel es un reto nuevo"
    );

    /** Random generator used to pick entries from the pool. */
    private final Random random = new Random();

    /**
     * Returns a random word or phrase from the internal pool.
     *
     * @return a randomly selected word or phrase, never {@code null}
     */
    public String getRandomWord() {
        int index = random.nextInt(POOL.size());
        return POOL.get(index);
    }
}
