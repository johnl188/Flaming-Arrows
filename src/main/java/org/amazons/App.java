package org.amazons;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 800);

        stage.setScene(scene);
        stage.setTitle("Flaming Arrows!");

        stage.setMinWidth(800);
        stage.setMinHeight(800);

        stage.show();
    }
}