package com.univalle.fpoe.escriturarapida;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Entry point of the "Escritura Rapida" JavaFX application.
 * <p>
 * It loads the main view from {@code game-view.fxml} (whose controller is
 * {@code GameController}) and shows it in the primary stage.
 *
 * @author Daniela Martinez
 * @version 1.0
 */
public class Main extends Application {

    /** Initial window width, in pixels. */
    private static final double WINDOW_WIDTH = 760;

    /** Initial window height, in pixels. */
    private static final double WINDOW_HEIGHT = 520;

    /**
     * Loads the FXML view and displays the primary stage.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws IOException if {@code game-view.fxml} cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("game-view.fxml"));
        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle("Escritura Rápida");
        stage.setMinWidth(560);
        stage.setMinHeight(440);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}