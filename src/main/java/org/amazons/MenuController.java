package org.amazons;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private RadioButton sixBySixBtn;

    @FXML
    private RadioButton eigthByEightBtn;

    @FXML
    private RadioButton tenByTenBtn;

    @FXML
    private RadioButton onePlayerBtn;

    @FXML
    private RadioButton twoPlayerBtn;

    @FXML
    private RadioButton easyAIBtn;

    @FXML
    private RadioButton hardAIBtn;

    @FXML
    private RadioButton aiRandomBtn;

    @FXML
    private RadioButton aiWhiteBtn;

    @FXML
    private RadioButton aiBlackBtn;

    @FXML
    private Pane aiPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        onePlayerBtn.setSelected(true);
        sixBySixBtn.setSelected(true);
        easyAIBtn.setSelected(true);
        aiRandomBtn.setSelected(true);
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

        GoToGameWithOptions(stage, options);
    }

    private void GoToGameWithOptions(Stage stage, GameOptions options) {

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

        aiPane.setVisible(true);

    }

    public void hideAI(ActionEvent actionEvent) {

        aiPane.setVisible(false);
    }
}
