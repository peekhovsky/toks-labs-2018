package by.peekhovsky.lab5tokenring.messenger;

import by.peekhovsky.lab5tokenring.coding.ByteStuffing;
import by.peekhovsky.lab5tokenring.coding.HammingCode;
import jssc.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rostislav Pekhovsky
 * @version 0.1
 */
public class MessengerManager {
    /**
     *
     */
    private static final Logger LOGGER
            = LogManager.getLogger(MessengerManager.class);

    public static final String[] SPEEDS
            = {"110", "300", "600", "1200", "4800", "9600", "14400", "19200",
            "38400", "57600", "115200", "128000", "256000"};

    private SerialPort serialPort;

    private boolean isPortOpened = false;

    private StringBuilder message = new StringBuilder();


    public List<String> getPortNames() {
        return new ArrayList<>(Arrays.asList(SerialPortList.getPortNames()));
    }

    public boolean connect(String portName, String speedName) {
        if (isPortOpened) {
            LOGGER.warn("Port is already opened!");
            return true;
        }
        serialPort = new SerialPort(portName);

        LOGGER.info("Trying to open port " + serialPort.getPortName()
                + "...");
        try {
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
                            SerialPort.PARITY_EVEN);
                    break;
                case "14400":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_14400,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_EVEN);
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
                    LOGGER.error("Error: wrong speed.");
                    return false;
            }

            //flow control
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            //listener
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            isPortOpened = true;
            LOGGER.info("Port has been opened. ");
            LOGGER.info("\nBaud: " + speedName);

        } catch (SerialPortException e) {
            if (e.getExceptionType().equals("Port busy")) {
                LOGGER.error("Cannot open port: port is busy.");
            } else {
                LOGGER.error("Cannot open port: " + e.getExceptionType());
            }
            return false;
        }
        return true;
    }

    public boolean stop() {
        boolean res;
        if (!isPortOpened) {
            LOGGER.warn("Port is already closed!");
            res = true;
        } else {
            try {
                LOGGER.info("Trying to close port "
                        + serialPort.getPortName() + "...");
                serialPort.closePort();
                LOGGER.info("Port has been closed.");
                isPortOpened = false;
                res = true;
            } catch (SerialPortException e) {
                LOGGER.error("Cannot close port: "
                        + serialPort.getPortName());
                LOGGER.error("Message: " + e.getMessage());
                LOGGER.error("Error: " + e.getExceptionType());
                res = false;
            }
        }
        return res;
    }

    public void sendMessage(String s) {
        Runnable r = () -> {
            try {
                serialPort.writeString(
                        HammingCode.getHammingCodeFromBytes(
                                ByteStuffing.doStuffing((s).getBytes()
                                )
                        ) + "$end$"
                );

            } catch (SerialPortException e) {
                LOGGER.error("Cannot send message: " + e.getExceptionType());
            }
        };
        Thread thd = new Thread(r);
        thd.start();
        try {
            thd.join(2000);
            if (!thd.isAlive()) {
                LOGGER.info("Message has been sent.");
            } else {
                LOGGER.info("Error: cannot connect to other device!");
                thd.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.debug("Interrupted!");
        }
    }


    private class PortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    messageCreator(
                            serialPort.readString(event.getEventValue())
                    );
                } catch (SerialPortException e) {
                    LOGGER.error("Cannot read message: "
                            + e.getExceptionType());
                }
            }
        }
        private void messageCreator(String newString) {
            message.append(newString);
            if (newString.length() >= 5
                    && message.substring(message.length() - 5, message.length())
                    .equals("$end$")) {
                String str = new String(
                        ByteStuffing.inject(HammingCode
                                .getBytesFromHammingCode(
                                        message.substring(
                                                0, message.length() - 5)))
                );
                LOGGER.info("Message: " + str);
                message = new StringBuilder();
            }
        }
    }
}
