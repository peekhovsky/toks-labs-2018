package by.peekhovsky.lab5tokenring.app;

import by.peekhovsky.lab5tokenring.tokenring.TokenRingManager;
import jssc.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/***
 * @author Rostislav Pekhovsky
 * @version 0.1
 */
public class Application {
    /***
     * Logger.
     */
    private static final Logger LOGGER
            = LogManager.getLogger(Application.class);

    /***
     * Token ring manager.
     */
    private TokenRingManager tokenRingManager = TokenRingManager.getInstance();

    /**
     * True if ports are opened.
     */
    private boolean isPortOpened = false;

    /**
     * Class console scanner.
     */
    private Scanner scanner = new Scanner(System.in);

    /**
     * Input port name.
     */
    private String inputPortName;
    /**
     * Output port name.
     */
    private String outputPortName;

    /**
     * Input baud value.
     */
    private int inputBaud = SerialPort.BAUDRATE_9600;
    /**
     * Output baud value.
     */
    private int outputBaud = SerialPort.BAUDRATE_9600;

    /**
     * Computer tag (for token ring manager). Name
     * to send message on this device.
     */
    private String computerTag = "DefComputer";
    /**
     * All available ports.
     */
    private List<String> portNames = TokenRingManager.getPortNames();


    /***
     * Changes port.
     * @return optional of new valid value of port
     */
    private Optional<String> changePort() {
        String portName = null;
        LOGGER.info("Available ports: " + portNames);
        LOGGER.info("Print a name of port: ");
        String s = scanner.next();
        if (portNames.contains(s)) {
            portName = s;
            LOGGER.info("Port name has been changed.");
        } else {
            LOGGER.error("Wrong name of a port!");
        }
        return Optional.ofNullable(portName);
    }

    /***
     * Changes baud.
     * @return optional of new valid value of baud
     */
    private Optional<Integer> changeBaud() {
        Integer baud = null;
        try {
            LOGGER.info("Available bauds: "
                    + Arrays.toString(TokenRingManager.SPEEDS));
            LOGGER.info("Print a baud: ");
            String s = scanner.next();
            if (Arrays.binarySearch(TokenRingManager.SPEEDS, s) != -1) {
                baud = Integer.parseInt(s);
                LOGGER.info("The baud has been changed.");
            } else {
                throw new NumberFormatException("Illegal baud value");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Wrong name of a baud!");
        }
        return Optional.ofNullable(baud);
    }

    /***
     * Changes tag.
     * @return optional of new valid value of tag
     */
    private Optional<String> changeComputerTag() {
        LOGGER.info("Enter new tag for computer: ");
        String tag = scanner.next();
        Optional<String> tagOptional;
        if (tag.isEmpty()) {
            tagOptional = Optional.empty();
        } else {
            tagOptional = Optional.of(tag);
        }
        return tagOptional;
    }

    /***
     * Connects to both ports.
     */
    private void connect() {
        if (tokenRingManager.conectToInputPort(inputPortName, inputBaud)
            && tokenRingManager.conectToOutputPort(outputPortName, outputBaud)
        ) {
            tokenRingManager.setDeviceTag(computerTag);
            isPortOpened = true;
        }
    }

    /**
     * Settings of program. Allows user to change ports, bauds and device tag.
     *
     * @return true if user wants to go to sending and getting message.
     */
    private boolean executeConnectionMenu() {
        boolean res = true;
        while (true) {
            if (portNames.isEmpty()) {
                LOGGER.fatal("There is no ports in your computer.");
                res = false;
            } else {
                inputPortName = portNames.get(1);
                outputPortName = portNames.get(1);
            }

            while (!isPortOpened) {
                System.out.println("1 - Connect, \n"
                        + "2 - Change input port, 3 - Change output port, \n"
                        + "4 - Change input baud, 5 - Change output baud, \n"
                        + "6 - Change computer tag, 0 - Exit");
                LOGGER.info("Input port: " + inputPortName);
                LOGGER.info("Input Baud: " + inputBaud);
                LOGGER.info("Output port: " + outputPortName);
                LOGGER.info("Output Baud: " + outputBaud);
                LOGGER.info("Computer tag: " + computerTag);
                int t;
                try {
                    t = scanner.nextInt();
                } catch (InputMismatchException e) {
                    LOGGER.warn("Wrong input!");
                    scanner.next();
                    continue;
                }

                switch (t) {
                    case 1:
                        connect();
                        break;
                    case 2:
                        inputPortName = changePort().orElse(inputPortName);
                        break;
                    case 3:
                        outputPortName = changePort().orElse(outputPortName);
                        break;
                    case 4:
                        inputBaud = changeBaud().orElse(inputBaud);
                        break;
                    case 5:
                        outputBaud = changeBaud().orElse(outputBaud);
                        break;
                    case 6:
                        computerTag = changeComputerTag().orElse(computerTag);
                        break;
                    case 0:
                        res = false;
                        break;
                    default:
                        LOGGER.warn("Wrong input!");
                        break;
                }
            }
            return res;
        }
    }

    /***
     * Main body of program. Allows user to get and send messages.
     */
    private void executeMessengerMenu() {
        while (isPortOpened) {
            System.out.println("1 - Print message,    2 - Close port, "
                             + "3 - Send Empty Token, 0 - Exit");
            int t;
            try {
                t = scanner.nextInt();
            } catch (InputMismatchException e) {
                LOGGER.error("Wrong input!");
                scanner.next();
                continue;
            }
            switch (t) {
                case 1:
                    LOGGER.info("Enter an address tag: ");
                    String addressTag = scanner.next();
                    scanner.reset();
                    LOGGER.info("Print a message: ");
                    scanner = new Scanner(System.in);
                    String m = scanner.nextLine();
                    tokenRingManager.sendMessage(addressTag, m);
                    break;

                case 2:
                    isPortOpened = false;
                    tokenRingManager.stop();
                    break;
                case 3:
                    tokenRingManager.sendEmptyToken();
                    break;
                case 0:
                    isPortOpened = false;
                    tokenRingManager.stop();
                    return;
                default:
                    LOGGER.error("Wrong input!");
            }

        }
    }

    /***
     * Start point of the program.
     * @param args console arguments
     */
    public static void main(final String[] args) {
        Application application = new Application();
        while (true) {
            boolean res = application.executeConnectionMenu();
            if (res) {
                application.executeMessengerMenu();
            } else {
                break;
            }
        }
    }
}
