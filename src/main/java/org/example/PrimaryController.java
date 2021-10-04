package org.example;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class PrimaryController implements Initializable {

    @FXML
    public void initialize(URL location, ResourceBundle resources) {



    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("board");
    }
}
