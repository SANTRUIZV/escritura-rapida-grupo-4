package com.univalle.fpoe.escriturarapida.model;

import java.util.List;
import java.util.Random;

/**
 * Bank of words and short phrases that the player must type.
 * Some entries include capital letters and punctuation, since the
 * answer has to match exactly.
 *
 * @author Santiago Ruiz Vanegas
 * @version 1.0
 */
public class WordBank {

    /** Words and phrases that can be shown to the player. */
    private static final List<String> POOL = List.of(
            "java",
            "evento",
            "teclado",
            "mouse",
            "ventana",
            "compilar",
            "Excepcion",
            "interfaz",
            "adaptador",
            "programacion",
            "clase interna",
            "Controlador",
            "Escritura Rapida",
            "manejo de eventos",
            "Hola, mundo!",
            "JavaFX es genial.",
            "el tiempo se agota...",
            "Presiona Enter para validar",
            "La practica hace al maestro.",
            "Cada nivel es un reto nuevo",
            "Universidad del Valle",
            "Que hora es?",
            "Scene Builder y FXML",
            "Java 17"
    );

    /** Random generator used to pick the entries. */
    private final Random random = new Random();

    /**
     * Returns a random word or phrase from the bank.
     *
     * @return a random word or phrase
     */
    public String getRandomWord() {
        return POOL.get(random.nextInt(POOL.size()));
    }
}
