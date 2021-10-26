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

    private static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));

        Parent root = fxmlLoader.load();

        scene = new Scene(root, 800, 800);

        stage.setScene(scene);
        stage.setTitle("Flaming Arrows!");

        stage.setMinWidth(800);
        stage.setMinHeight(800);

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


    private static GameOptions get10x10GameOptionsRandomAI() {
        return new GameOptions(10, BoardController.get10x10StartingPositions(),
                AIPlayerType.Random, false);
    }

    private static GameOptions get10x10GameOptionsHuman() {
        return new GameOptions(10, BoardController.get10x10StartingPositions(),
                AIPlayerType.None, false);
    }

    private static GameOptions get8x8GameOptionsRandomAI() {
        return new GameOptions(8, BoardController.get8x8StartingPositions(),
                AIPlayerType.Random, false);
    }

    private static GameOptions get8x8GameOptionsHuman() {
        return new GameOptions(8, BoardController.get8x8StartingPositions(),
                AIPlayerType.None, false);
    }

    private static GameOptions get6x6GameOptionsRandomAI() {
        return new GameOptions(6, BoardController.get6x6StartingPositions(),
                AIPlayerType.Random, false);
    }

    private static GameOptions get6x6GameOptionsHuman() {
        return new GameOptions(6, BoardController.get6x6StartingPositions(),
                AIPlayerType.None, false);
    }

}