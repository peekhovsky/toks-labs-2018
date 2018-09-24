package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import jssc.SerialPort;


public class Controller {
    @FXML
    TextArea messengerTextArea;

    @FXML
    TextArea messengerTextField;

    @FXML
    ChoiceBox<String> speedChoiceBox;

    @FXML
    ChoiceBox<String> portChoiceBox;

    @FXML
    Label statusLabel;

    @FXML
    private TextArea terminalTextArea;

    @FXML
    Button connectButton;

    @FXML
    Button stopButton;

    private MessengerCore messengerCore;

    ObservableList<String> portsList;

    @FXML
    public void initialize() {
        portChoiceBox.getSelectionModel().select(0);

        speedChoiceBox.getItems().removeAll();
        speedChoiceBox.getItems().addAll("110", "300", "600", "1200", "4800",
                "9600", "14400","19200", "38400", "57600",  "115200", "128000", "256000");
        speedChoiceBox.getSelectionModel().select(5);

        messengerCore = new MessengerCore("COM1", terminalTextArea, messengerTextArea,
                statusLabel, portChoiceBox, speedChoiceBox);
    }

    @FXML
    void stop() {
        messengerCore.stop();
    }


    @FXML
    void pressedConnectButton() {
        messengerCore.connect();
    }

    @FXML
    void pressedStopButton() {
        messengerCore.stop();
    }

    @FXML
    void pressedSendButton() {
        if (!messengerTextField.getText().isEmpty()) {
            messengerCore.sendMessage(messengerTextField.getText());
            messengerTextField.setText("");
        }
    }
}
