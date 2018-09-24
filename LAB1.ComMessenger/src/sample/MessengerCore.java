package sample;

import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import jssc.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MessengerCore {

    private SerialPort serialPort;
    private TextArea terminal;
    private TextArea messagesField;
    private Label statusLabel;
    private String portName;
    private ChoiceBox<String> portChoiceBox;
    private ChoiceBox<String> speedChoiceBox;

    private boolean isPortOpened = false;

    MessengerCore(String portName, TextArea terminal, TextArea messagesField, Label statusLabel,
                  ChoiceBox<String> portChoiceBox,  ChoiceBox<String> speedChoiceBox) {

        portChoiceBox.getItems().removeAll();
        portChoiceBox.getItems().addAll(SerialPortList.getPortNames());
        this.terminal = terminal;
        this.messagesField = messagesField;
        this.statusLabel = statusLabel;
        this.portName = portName;
        this.portChoiceBox = portChoiceBox;
        this.speedChoiceBox = speedChoiceBox;

        this.statusLabel.setText("Disconnected");
        this.statusLabel.setTextFill(Color.RED);
    }

    void connect() {
        if (isPortOpened) {
            printInTerminal("Port is already opened!");
            return;
        }
        if (portChoiceBox.getValue() == null) {
            printInTerminal("Cannot open port! You should set a name of port.");
            return;
        }

        serialPort = new SerialPort(portChoiceBox.getValue());

        try {
            printInTerminal("Trying to open port " +  serialPort.getPortName() + "...");

            //set params
            switch (speedChoiceBox.getValue()) {
                case "110":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_110,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "300":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_300,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "1200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_1200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "4800":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_4800,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "9600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "14400":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_14400,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "19200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_19200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "38400":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_38400,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                    break;
                case "57600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_57600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "115200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                    break;
                case "128000":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_128000,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "256000":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_256000,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                default:
                    printInTerminal("Error: you should set settings.");
                    return;
            }


            //flow control
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            //listener
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            isPortOpened = true;
            printInTerminal("Port has been opened. " +
                    "\nBaud: " + speedChoiceBox.getValue());
            this.statusLabel.setText("Connected");
            this.statusLabel.setTextFill(Color.GREEN);

        } catch (SerialPortException e) {
            e.printStackTrace();

            if (e.getExceptionType().equals("Port busy")) {
                printInTerminal("Cannot open port: port is busy.");
            } else {
                printInTerminal("Cannot open port: " + e.getExceptionType());
            }
        }
    }

    void stop() {
        if (!isPortOpened) {
            printInTerminal("Port is closed!");
            return;
        }

        try {
            printInTerminal("Trying to close port " + serialPort.getPortName() + "...");
            serialPort.closePort();
            printInTerminal("Port has been closed.");

            isPortOpened = false;
            this.statusLabel.setText("Disconnected");
            this.statusLabel.setTextFill(Color.RED);

        } catch (SerialPortException e) {
            e.printStackTrace();
            printInTerminal("Cannot close port!");
        }
    }

    void sendMessage(String s) {
        try {
            serialPort.writeString(s);
            printMessage("\n" + s + "\n");
        } catch (SerialPortException e) {
            printInTerminal("Cannot send message: " + e.getExceptionType());
        }
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    String data = serialPort.readString(event.getEventValue());

                    printMessage(data);
                    //serialPort.writeString("Get data");
                }
                catch (SerialPortException e) {
                    e.printStackTrace();
                    System.out.println("Cannot read message: serialEvent(SerialPortEvent event)");
                }
            }
        }
    }

    private void printInTerminal(String s) {
        Platform.runLater(() -> terminal.appendText("\n" + s));
    }

    synchronized private void printMessage(String s) {
        Platform.runLater(() -> messagesField.appendText("\n" + s));
    }

    private void printInTerminalWithoutNewLine(String s) {
        Platform.runLater(() -> terminal.appendText(s));
    }


}
