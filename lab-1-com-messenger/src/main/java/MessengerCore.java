
import jssc.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MessengerCore {

    private SerialPort serialPort;
    private boolean isPortOpened = false;

    final static String[] speeds = { "110", "300", "600", "1200", "4800",
            "9600", "14400","19200", "38400", "57600",  "115200", "128000", "256000" };

    ArrayList<String> getPortNames() {
        return new ArrayList<>(Arrays.asList(SerialPortList.getPortNames()));
    }

    boolean connect(String portName, String speedName) {
        if (isPortOpened) {
            Main.print("Port is already opened!");
            return true;
        }

        serialPort = new SerialPort(portName);

        try {
            Main.print("Trying to open port " +  serialPort.getPortName() + "...");

            //set params
            switch (speedName) {
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
                    Main.print("Error: wrong speed.");
                    return false;
            }

            //flow control
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            //listener
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            isPortOpened = true;
            Main.print("Port has been opened. " +
                    "\nBaud: " + speedName);

        } catch (SerialPortException e) {

            if (e.getExceptionType().equals("Port busy")) {
                Main.print("Cannot open port: port is busy.");
            } else {
                Main.print("Cannot open port: " + e.getExceptionType());
            }
            return false;
        }
        return true;
    }

    void stop() {
        if (!isPortOpened) {
            Main.print("Port is already closed!");
            return;
        }

        try {
            Main.print("Trying to close port " + serialPort.getPortName() + "...");
            serialPort.closePort();
            Main.print("Port has been closed.");

            isPortOpened = false;

        } catch (SerialPortException e) {
            e.printStackTrace();
            Main.print("Cannot close port!");
        }
    }

    void sendMessage(String s) {
        try {
            serialPort.writeString(s);
        } catch (SerialPortException e) {
            Main.print("Cannot send message: " + e.getExceptionType());
        }
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    String data = serialPort.readString(event.getEventValue());
                    Main.print("Message: "data);
                }
                catch (SerialPortException e) {
                    e.printStackTrace();
                    Main.print("Cannot read message: serialEvent(SerialPortEvent event)");
                }
            }
        }
    }
}
