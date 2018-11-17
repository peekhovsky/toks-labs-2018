package by.peekhovsky.lab5tokenring.tokenring;

import by.peekhovsky.lab5tokenring.coding.ByteStuffing;
import by.peekhovsky.lab5tokenring.coding.HammingCode;
import by.peekhovsky.lab5tokenring.messenger.MessengerManager;
import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import jssc.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Rostislav Pekhovsky
 * @version 0.1
 * @link http://github.com/peekhovsky/
 */
public class TokenRingManager {
    /**
     * Logger.
     */
    private static final Logger LOGGER
            = LogManager.getLogger(MessengerManager.class);

    /**
     * Available connection speeds.
     */
    public final static String[] SPEEDS
            = {"110", "300", "600", "1200", "4800", "9600", "14400", "19200",
            "38400", "57600", "115200", "128000", "256000"};
    /**
     * Instance.
     */
    private static TokenRingManager tokenRingManager;
    /**
     * Device tag (name).
     */
    private String deviceTag;
    /**
     * Serial port to get data.
     */
    private SerialPort portInput;
    /**
     * Serial port to send data.
     */
    private SerialPort portOutput;

    /**
     * Future tokens.
     * */
    private List<Token> futureTokens = new ArrayList<>();

    /**
     * @return instance
     */
    public static TokenRingManager getInstance() {
        if (tokenRingManager == null) {
            tokenRingManager = new TokenRingManager();
        }
        return tokenRingManager;
    }

    /**
     * @return available port names
     */
    public static List<String> getPortNames() {
        return new ArrayList<>(Arrays.asList(SerialPortList.getPortNames()));
    }

    /**
     * Private constructor.
     */
    private TokenRingManager() {
    }

    /***
     * @return device tag
     */
    public String getDeviceTag() {
        return deviceTag;
    }

    /***
     * @param deviceTag device tag to set
     */
    public void setDeviceTag(final String deviceTag) {
        this.deviceTag = deviceTag;
    }

    /***
     * @param portName port name (such as COM1, COM2 etc.)
     * @param speed port speed
     * @return true if port has been opened
     */
    @SuppressWarnings("Duplicates")
    public boolean conectToInputPort(final String portName, final int speed) {
        boolean res = false;
        if (Objects.nonNull(portInput) && portInput.isOpened()) {
            LOGGER.warn("Input port is already opened!");
            res = true;
        } else {
            Optional<SerialPort> portOptional = connectToPort(portName, speed);
            if (portOptional.isPresent()) {
                portInput = portOptional.get();
                try {
                    portInput.addEventListener(new InputPortReader(),
                            SerialPort.MASK_RXCHAR);
                    res = true;
                } catch (SerialPortException e) {
                    LOGGER.error("Cannot create port reader listener: "
                            + e.getExceptionType());
                    res = false;
                }
            }
        }
        return res;
    }

    /***
     * @param portName port name
     * @param speed speed (int value)
     * @return true if connection is successful, otherwise false
     */
    public boolean conectToOutputPort(final String portName, final int speed) {
        boolean res = false;
        if (Objects.nonNull(portOutput) && portOutput.isOpened()) {
            LOGGER.warn("Output port is already opened!");
            res = true;
        } else {
            Optional<SerialPort> portOptional = connectToPort(portName, speed);
            if (portOptional.isPresent()) {
                portOutput = portOptional.get();
                res = true;
            }
        }
        return res;
    }

    /***
     * @param addressTag address to send
     * @param message message to send
     */
    public void sendMessage(String addressTag, String message) {
        Token token = Token.builder()
                .addressTag(addressTag)
                .returnAddressTag(deviceTag)
                .message(message)
                .build();
        futureTokens.add(token);
    }

    /***
     * @param portName port name
     * @param baud port baud
     * @return new port
     */
    private Optional<SerialPort> connectToPort(String portName, int baud) {
        Optional<SerialPort> portOptional;
        SerialPort port = new SerialPort(portName);
        try {
            port.openPort();
            port.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                    | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            portOptional = Optional.of(port);
        } catch (SerialPortException e) {
            LOGGER.error("Cannot open port: " + e.getExceptionType());
            portOptional = Optional.empty();
        }
        return portOptional;
    }

    public void sendEmptyToken() {
        send(" ");
    }

    private void send(final String message) {
        Runnable r = () -> {
            try {
                portOutput.writeString(
                        HammingCode.getHammingCodeFromBytes(
                                ByteStuffing.doStuffing((message).getBytes()
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
                LOGGER.trace("Message has been sent.");
            } else {
                LOGGER.error("Error: cannot connect device!");
                LOGGER.error("Port: " + portOutput.getPortName());
                thd.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.debug("Interrupted!");
        }
    }
    /***
     * Event listener class. Listens messages from input port.
     */
    private class InputPortReader implements SerialPortEventListener {
        /**
         * Message buffer to create full message. Message ends with
         * $end$ tag.
         */
        private StringBuilder message = new StringBuilder();

        /***
         * This method executes if message has been arrived.
         * @param event event
         */
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    messageCreator(portInput
                            .readString(event.getEventValue()));
                } catch (SerialPortException e) {
                    LOGGER.error("Cannot read message: "
                            + e.getExceptionType());
                }
            }
        }

        /***
         * Creates and displays message.
         * @param newString new message from listener
         */
        private void messageCreator(String newString) {
            message.append(newString);
            LOGGER.trace("Message draft: " + newString);
            if (newString.length() >= 5
                    && message.substring(message.length() - 5, message.length())
                    .equals("$end$")) {
                String data = new String(
                        ByteStuffing.inject(HammingCode
                                .getBytesFromHammingCode(
                                        message.substring(
                                                0, message.length() - 5)))
                );

                List<Token> tokens = TokenParser.parse(data);
                processToken(tokens);
                message = new StringBuilder();
            }
        }
        private void processToken(final List<Token> tokens) {
            List<Token> deviceTokens = tokens.stream()
                    .filter(token -> token.getAddressTag().equals(deviceTag))
                    .collect(Collectors.toList());
            for (Token token : deviceTokens) {
                printMessage("Message: ");
                printMessage("From: " + token.getReturnAddressTag());
                printMessage("Text: " + token.getMessage());
            }
            List<Token> otherTokens = tokens.stream()
                    .filter(token -> !token.getAddressTag().equals(deviceTag))
                    .collect(Collectors.toList());
            otherTokens.addAll(futureTokens);
            futureTokens.clear();
            sendTokens(otherTokens);
        }

        private void sendTokens(final List<Token> tokens) {
            String tokensSerialized = TokenParser.serialize(tokens);
            send(tokensSerialized);
        }

        private void printMessage(String message) {
            LOGGER.info(message);
        }


    }

}