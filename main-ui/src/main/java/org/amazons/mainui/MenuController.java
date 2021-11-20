package org.amazons.mainui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private ToggleButton sixBySixBtn;

    @FXML
    private ToggleButton eigthByEightBtn;

    @FXML
    private ToggleButton tenByTenBtn;

    @FXML
    private ToggleButton onePlayerBtn;

    @FXML
    private ToggleButton twoPlayerBtn;

    @FXML
    private ToggleButton easyAIBtn;

    @FXML
    private ToggleButton hardAIBtn;

    @FXML
    private ToggleButton aiRandomBtn;

    @FXML
    private ToggleButton aiWhiteBtn;

    @FXML
    private ToggleButton aiBlackBtn;

    @FXML
    private ToggleGroup playerNumbers;

    @FXML
    private ToggleGroup boardSizeGroup;

    @FXML
    private ToggleGroup aiDifficulty;

    @FXML
    private ToggleGroup aiStart;

    @FXML
    private Pane aiPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        onePlayerBtn.setSelected(true);
        sixBySixBtn.setSelected(true);
        easyAIBtn.setSelected(true);
        aiRandomBtn.setSelected(true);

        playerNumbers.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        boardSizeGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        aiDifficulty.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        aiStart.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });
    }


    public void startGame(ActionEvent actionEvent) {

        int boardSize;
        StartingPosition[] positions;
        AIPlayerType aiPlayerType = AIPlayerType.None;
        boolean isAIPlayerFirst = false;

        if (onePlayerBtn.isSelected()) {

            if (easyAIBtn.isSelected()) {
                aiPlayerType = AIPlayerType.Easy;
            }

            else {
                aiPlayerType = AIPlayerType.Random;
            }

            if (aiRandomBtn.isSelected()) {
                Random rand = new Random();
                int randomInt = rand.nextInt(2);

                isAIPlayerFirst = randomInt % 2 == 0;
            }

            else {
                isAIPlayerFirst = aiWhiteBtn.isSelected();
            }
        }

        if (sixBySixBtn.isSelected()) {
            boardSize = 6;
            positions = BoardController.get6x6StartingPositions();
        }

        else if (eigthByEightBtn.isSelected()) {
            boardSize = 8;
            positions = BoardController.get8x8StartingPositions();
        }

        else {
            boardSize = 10;
            positions = BoardController.get10x10StartingPositions();
        }

        GameOptions options = new GameOptions(boardSize, positions, aiPlayerType, isAIPlayerFirst);

        Node node = (Node) actionEvent.getSource();

        Stage stage = (Stage) node.getScene().getWindow();

        goToGameWithOptions(stage, options);
    }

    private void goToGameWithOptions(Stage stage, GameOptions options) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("board.fxml"));

            Parent root = fxmlLoader.load();
            BoardController controller = fxmlLoader.<BoardController>getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            controller.setGameOptions(options);
        }

        catch (IOException ex) {
            System.out.println("Error loading game board");
        }

    }

    public void showAI(ActionEvent actionEvent) {
        easyAIBtn.setDisable(false);
        hardAIBtn.setDisable(false);
        aiRandomBtn.setDisable(false);
        aiBlackBtn.setDisable(false);
        aiWhiteBtn.setDisable(false);

        GaussianBlur blur = new GaussianBlur(0);
        aiPane.setEffect(blur);
    }

    public void hideAI(ActionEvent actionEvent) {
        easyAIBtn.setDisable(true);
        hardAIBtn.setDisable(true);
        aiRandomBtn.setDisable(true);
        aiBlackBtn.setDisable(true);
        aiWhiteBtn.setDisable(true);

        GaussianBlur blur = new GaussianBlur(10);
        aiPane.setEffect(blur);
    }
}
