package by.peekhovsky.lab5tokenring.app;


import by.peekhovsky.lab5tokenring.messenger.MessengerManager;
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

    private final static Logger LOGGER
            = LogManager.getLogger(Application.class);

    private MessengerManager messengerManager = new MessengerManager();

    private TokenRingManager tokenRingManager = TokenRingManager.getInstance();

    private boolean isPortOpened = false;

    private Scanner scanner = new Scanner(System.in);

    private synchronized static void print(String s) {
        LOGGER.info(s);
    }

    private String inputPortName;
    private String outputPortName;

    private int inputBaud = SerialPort.BAUDRATE_9600;
    private int outputBaud = SerialPort.BAUDRATE_9600;

    private String computerTag = "DefComputer";

    private List<String> portNames = TokenRingManager.getPortNames();

    private void connect() {
        if (tokenRingManager.conectToInputPort(inputPortName, inputBaud)
            &&  tokenRingManager.conectToOutputPort(outputPortName, outputBaud)
        ) {
            tokenRingManager.setDeviceTag(computerTag);
            isPortOpened = true;
        }
    }

    private Optional<String> changePort() {
        String portName = null;
        print("Available ports: " + portNames);
        print("Print a name of port: ");
        String s = scanner.next();
        if (portNames.contains(s)) {
            portName = s;
            print("Port name has been changed.");
        } else {
            print("Wrong name of a port!");
        }
        return Optional.ofNullable(portName);
    }

    private Optional<Integer> changeBaud() {
        Integer baud = null;
        try {
            print("Available bauds: " + Arrays.toString(MessengerManager.SPEEDS));
            print("Print a baud: ");
            String s = scanner.next();
            if (Arrays.binarySearch(MessengerManager.SPEEDS, s) != -1) {
                baud = Integer.parseInt(s);
                print("The baud has been changed.");
            } else {
                print("Wrong name of a baud!");
            }
        } catch (NumberFormatException e) {
            print("Wrong name of a baud!");
        }
        return Optional.ofNullable(baud);
    }

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
                System.out.println("1 - Connect, \n" +
                        "2 - Change input port, 3 - Change output port, \n" +
                        "4 - Change input baud, 5 - Change output baud, \n" +
                        "6 - Change computer tag, 0 - Exit");
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


    private void executeMessengerMenu() {
        while (isPortOpened) {
            System.out.println("1 - Print message, 2 - Close port, 3 - Send Empty Token, 0 - Exit");
            int t;
            try {
                t = scanner.nextInt();
            } catch (InputMismatchException e) {
                print("Wrong input!");
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
                    messengerManager.stop();
                    break;
                case 3:
                    tokenRingManager.sendEmptyToken();
                    break;
                case 0:
                    isPortOpened = false;
                    if (messengerManager.stop()) {
                        return;
                    }
                    break;
                default:
                    print("Wrong input!");
            }

        }
    }

    public static void main(String[] args) {
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
