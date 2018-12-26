package by.peekhovsky.lab6tsp.app;

import by.peekhovsky.lab6tsp.tsp.TSPManager;
import jssc.SerialPort;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/***
 * @author Rostislav Pekhovsky
 * @version 0.1
 */

@Log4j2
public class Application {

    /***
     * Token ring manager.
     */
    private TSPManager manager = TSPManager.getInstance();

    /**
     * True if ports are opened.
     */
    private boolean isPortOpened = false;

    /**
     * Class console scanner.
     */
    private Scanner scanner = new Scanner(System.in);


    /**
     * Port name.
     */
    private String portName;

    /**
     * Baud value.
     */
    private int baud = SerialPort.BAUDRATE_9600;

    /**
     * All available ports.
     */
    private static final List<String> PORT_NAMES = TSPManager.getPortNames();

    private List<String> messagesToSend = new ArrayList<>();

    /***
     * Changes port.
     * @return optional of new valid value of port
     */
    private Optional<String> changePort() {
        String portName = null;

        log.info("Available ports: " + PORT_NAMES);
        log.info("Print a name of port: ");
        String s = scanner.next();
        if (PORT_NAMES.contains(s)) {
            portName = s;
            log.info("Port name has been changed.");
        } else {
            log.error("Wrong name of a port!");
        }
        return Optional.ofNullable(portName);
    }

    /***
     * Changes baud.
     * @return optional of new valid value of baud
     */
    private Optional<Integer> changeBaud() {
        Integer newBaud = null;
        try {
            log.info("Available bauds: "
                    + Arrays.toString(TSPManager.SPEEDS));
            log.info("Print a baud: ");
            String s = scanner.next();
            if (Arrays.binarySearch(TSPManager.SPEEDS, s) != -1) {
                newBaud = Integer.parseInt(s);
                log.info("The baud has been changed.");
            } else {
                throw new NumberFormatException("Illegal baud value");
            }
        } catch (NumberFormatException e) {
            log.error("Wrong name of a baud!");
        }
        return Optional.ofNullable(newBaud);
    }


    /***
     * Connects to both ports.
     */
    private void connect() {
        if (manager.connectToPort(portName, baud)) {
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
            if (PORT_NAMES.isEmpty()) {
                log.fatal("There is no ports in your computer.");
                res = false;
            } else {
                portName = PORT_NAMES.get(0);
            }

            while (!isPortOpened) {
                System.out.println("1 - Connect, \n"
                        + "2 - Change port, 3 - Change baud, \n"
                        + "0 - Exit");

                log.info("Port: " + portName);
                log.info("Baud: " + baud);
                int t;
                try {
                    t = scanner.nextInt();
                } catch (InputMismatchException e) {
                    log.warn("Wrong input!");
                    scanner.next();
                    continue;
                }

                switch (t) {
                    case 1:
                        connect();
                        break;
                    case 2:
                        portName = changePort().orElse(portName);
                        break;
                    case 3:
                        baud = changeBaud().orElse(baud);
                        break;
                    case 0:
                        res = false;
                        break;
                    default:
                        log.warn("Wrong input!");
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

        boolean isErrorInChecksum = false;
        boolean isErrorInOrder = false;

        while (isPortOpened) {
            System.out.println( "1 - Add a message, 2 - Send messages,  3 - Close port, "
                              + "4 - Set error in checksum, 5 - Set error in order, "
                              + "6 - Send message (divide message), 0 - Exit");
            log.debug("Error in checksum: " + isErrorInChecksum);
            log.debug("Error in order: " + isErrorInOrder);

            messagesToSend.forEach(m -> log.info("Message: " + m));
            int t;
            try {
                t = scanner.nextInt();
            } catch (InputMismatchException e) {
                log.error("Wrong input!");
                scanner.next();
                continue;
            }
            switch (t) {
                case 1: {
                    scanner.reset();
                    log.info("Print a message: ");
                    scanner = new Scanner(System.in);
                    String message = scanner.nextLine();
                    messagesToSend.add(message);
                    break;
                }
                case 2: {
                    manager.sendListOfMessages(messagesToSend, isErrorInChecksum, isErrorInOrder);
                    messagesToSend.clear();
                    break;
                }
                case 3:
                    isPortOpened = false;
                    manager.stop();
                    break;

                case 4:
                    isErrorInChecksum = !isErrorInChecksum;
                    break;

                case 5:
                    isErrorInOrder = !isErrorInChecksum;
                    break;

                case 6: {
                    scanner.reset();
                    log.info("Print a message: ");
                    scanner = new Scanner(System.in);
                    String message = scanner.nextLine();
                    messagesToSend = new ArrayList<>();
                    for (int i = 0; i < message.length(); i+=4) {
                        if (i + 4 < message.length()) {
                            messagesToSend.add(message.substring(i, i + 4));
                        } else {
                            messagesToSend.add(message.substring(i));
                        }
                    }
                    manager.sendListOfMessages(messagesToSend, isErrorInChecksum, isErrorInOrder);
                    messagesToSend.clear();
                    break;
                }
                case 0:
                    isPortOpened = false;
                    manager.stop();
                    return;

                default:
                    log.error("Wrong input!");
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
