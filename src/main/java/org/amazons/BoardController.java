package org.amazons;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BoardController implements Initializable {

    @FXML private AnchorPane anchorPane;

    @FXML private GridPane boardGridPane;

    @FXML private Label lblInfo;

    @FXML private StackPane centerStackPane;

    private GameOptions gameOptions;
    private GameInfo gameInfo;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {


//        Image image = new Image("download.jpg");
//        ImageViewPane backgroundImage = new ImageViewPane();
//        backgroundImage.setImageView(new ImageView(image));
//
//        backgroundImage.toBack();

        centerStackPane.setStyle("-fx-background-color: rgba(255, 140, 0, 1)");


//        centerStackPane.getChildren().clear();
//        centerStackPane.getChildren().add(backgroundImage);
//        centerStackPane.getChildren().add(boardGridPane);

    }

    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;

        gameInfo = new GameInfo(gameOptions);

        lblInfo.textProperty().bind(gameInfo.gameInfoLabelProperty());

        createSquares(gameOptions.getGameSize());
        addPieces(gameOptions.getPositions());

        if (gameOptions.getAIPlayerType() != AIPlayerType.None) {

            Stage popupStage = new Stage();
            Stage thisStage = (Stage) anchorPane.getScene().getWindow();

            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(thisStage);

            VBox dialogVbox = createPopup(gameOptions);
            dialogVbox.setAlignment(Pos.CENTER);

            Scene dialogScene = new Scene(dialogVbox, 300, 200);

            popupStage.setResizable(false);
            popupStage.initStyle(StageStyle.UNDECORATED);

            popupStage.setScene(dialogScene);
            popupStage.showAndWait();
        }

        gameInfo.startGame();
    }

    private VBox createPopup(GameOptions gameOptions) {
        VBox dialogVbox = new VBox(20);

        String dialogText = "You are playing ";
        dialogText += gameOptions.getIsAIFirst() ? "Black" : "White";
        dialogText += " and will go ";
        dialogText += gameOptions.getIsAIFirst() ? "second." : "first.";


        dialogVbox.getChildren().add(new Text(dialogText));

        Button button = new Button("Ok");
        button.setOnAction((actionEvent -> {
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            stage.close();
        }));

        dialogVbox.getChildren().add(button);

        return dialogVbox;
    }

    private void createSquares(int perSide) {

        boolean isWhite = true;
        boardGridPane.getChildren().clear();
        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();

        for(byte i = 0; i < perSide; i++) {

            for(byte j = 0; j < perSide; j++) {

                GameSquare square = new GameSquare(gameInfo, i, j, isWhite);

                gameInfo.addGameSquare(square);
                boardGridPane.add(square, j, i);

                isWhite = !isWhite;
            }

            if (perSide % 2 == 0) {
                isWhite = !isWhite;
            }
        }

        for (int i = 0; i < perSide; i++) {
            boardGridPane.getColumnConstraints().add(new ColumnConstraints(5, 500, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            boardGridPane.getRowConstraints().add(new RowConstraints(5, 200, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
    }

    private void addPieces(StartingPosition[] positions) {

        for (StartingPosition position: positions) {
            GameSquare square = gameInfo.getGameSquare(position.getRow(), position.getColumn());
            square.addAmazon(position.getIsWhite());
        }
    }

    public void resetGame(ActionEvent actionEvent) {

        if (!gameInfo.getIsOkToMovePiece()) {
            return;
        }

        gameInfo.resetGame();
        addPieces(gameOptions.getPositions());
        gameInfo.startGame();
    }

    public void undoLastMove(ActionEvent actionEvent) {

        gameInfo.undoLastMove();
    }

    public void goBackToMenu(ActionEvent actionEvent) {

        try {
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
        }

        catch (IOException ex) {
            System.out.println("Error loading menu");
        }
    }

    public static StartingPosition[] get10x10StartingPositions() {
        StartingPosition[] positions = new StartingPosition[8];

        positions[0] = new StartingPosition(3,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(0,6, false);
        positions[3] = new StartingPosition(3,9, false);
        positions[4] = new StartingPosition(6,0, true);
        positions[5] = new StartingPosition(9,3, true);
        positions[6] = new StartingPosition(9,6, true);
        positions[7] = new StartingPosition(6,9, true);

        return  positions;
    }

    public static StartingPosition[] get8x8StartingPositions() {
        StartingPosition[] positions = new StartingPosition[6];

        positions[0] = new StartingPosition(2,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(2,7, false);
        positions[3] = new StartingPosition(5,0, true);
        positions[4] = new StartingPosition(7,4, true);
        positions[5] = new StartingPosition(5,7, true);

        return  positions;
    }

    public static StartingPosition[] get6x6StartingPositions() {
        StartingPosition[] positions = new StartingPosition[4];

        positions[0] = new StartingPosition(0,2, false);
        positions[1] = new StartingPosition(2,5, false);
        positions[2] = new StartingPosition(3,0, true);
        positions[3] = new StartingPosition(5,3, true);

        return  positions;
    }
}