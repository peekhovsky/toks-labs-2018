package by.peekhovsky.lab6tsp.tsp;

import by.peekhovsky.lab6tsp.coding.ByteStuffing;
import by.peekhovsky.lab6tsp.coding.HammingCode;
import jssc.*;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.text.html.HTMLDocument;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Rostislav Pekhovsky
 * @version 0.1
 * @link http://github.com/peekhovsky/
 */

@Log4j2
public class TSPManager {

    /**
     * Available connection speeds.
     */
    public static final String[] SPEEDS
            = {"110", "300", "600", "1200", "4800", "9600", "14400", "19200",
            "38400", "57600", "115200", "128000", "256000"};

    /**
     * End message identifier.
     */
    protected static final String END_MESSAGE = "END_MESSAGE";

    /**
     * Instance.
     */
    private static TSPManager manager;

    /**
     * Serial port to get data.
     */
    private SerialPort port;

    /**
     * Packages to send with AN in the key.
     */
    private Map<Integer, Package> packageHashMap = new ConcurrentHashMap<>();

    /**
     * Arrived tokens.
     */
    private List<Package> arrivedPackages = new ArrayList<>();

    /**
     * Randomize value.
     */
    private Random random = new Random();

    /**
     * @return instance
     */
    public static TSPManager getInstance() {
        if (manager == null) {
            manager = new TSPManager();
        }
        return manager;
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
    private TSPManager() {
    }

    /***
     * @param portName port name (such as COM1, COM2 etc.)
     * @param speed port speed
     * @return true if port has been opened
     */
    @SuppressWarnings("Duplicates")
    public boolean connectToPort(final String portName, final int speed) {
        boolean res = false;
        if (Objects.nonNull(port) && port.isOpened()) {
            log.warn("Input port is already opened!");
            res = true;
        } else {
            Optional<SerialPort> portOptional = connect(portName, speed);
            if (portOptional.isPresent()) {
                port = portOptional.get();
                try {
                    port.addEventListener(new InputPortReader(),
                            SerialPort.MASK_RXCHAR);
                    res = true;
                } catch (SerialPortException e) {
                    log.error("Cannot create port reader listener: "
                            + e.getExceptionType());
                    res = false;
                }
            }
        }
        return res;
    }

    /***
     * @param portName port name
     * @param baud port baud
     * @return new port
     */
    private Optional<SerialPort> connect(String portName, int baud) {
        Optional<SerialPort> portOptional;
        SerialPort serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                    | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            portOptional = Optional.of(serialPort);
        } catch (SerialPortException e) {
            log.error("Cannot open port: " + e.getExceptionType());
            portOptional = Optional.empty();
        }
        return portOptional;
    }

    private void send(final String message) {
        Runnable r = () -> {
            try {
                port.writeString(
                        (message
                        ) + "$end$"
                );

            } catch (SerialPortException e) {
                log.error("Cannot send message: " + e.getExceptionType());
            }
        };

        Thread thd = new Thread(r);
        thd.start();
        try {
            thd.join(2000);
            if (!thd.isAlive()) {
                log.debug("Message has been sent.");
            } else {
                log.error("Error: cannot connect device!");
                log.error("Port: " + port.getPortName());
                thd.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("Interrupted!");
        }
    }

    /***
     * Closes all ports.
     **/
    public void stop() {

        log.info("Trying to close port "
                + port.getPortName() + "...");

        try {
            port.closePort();
            log.info("Ports has been closed.");
        } catch (SerialPortException e) {
            log.error("Cannot close port: " + e.getPortName());
            log.error("Message: " + e.getMessage());
            log.error("Error: " + e.getExceptionType());
        }

    }

    public void sendListOfMessages(@NonNull List<String> messages,
                                   boolean isErrorInChecksum,
                                   boolean isErrorInOrder) {

        List<Package> packages = new ArrayList<>(messages.size());
        int sn = 0;
        for (String message : messages) {
            int an = random.nextInt(100000);
            Package pack = Package.builder()
                    .sn(sn)
                    .an(an)
                    .message(message)
                    .build();
            PackageCheckSumCreator.addCheckSumToPackage(pack);
            if (isErrorInChecksum && sn == 0) {
                pack.setCheckSum(1);
            }
            packages.add(pack);
            sn++;
        }
        if (isErrorInOrder) {
            packages = packages.stream()
                    .sorted((p1, p2) -> Integer.compare(p2.getSn(), p1.getSn()))
                    .collect(Collectors.toList());
        }
        sendListOfPackages(packages);
    }


    private synchronized void sendListOfPackages(@NonNull List<Package> packages) {
        Runnable r = () -> {
            try {
                packageHashMap = packages.stream().collect(
                        Collectors.toMap(Package::getAn, x -> x));
                while (!packageHashMap.isEmpty()) {
                    for (Package aPackage : new ArrayList<>(packageHashMap.values())) {
                        sendPackage(aPackage);
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                    TimeUnit.SECONDS.sleep(2);
                    log.debug(packageHashMap);
                }
                sendPackage(Package.builder().message(END_MESSAGE).build());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("Packages have been send!");
        };

        Thread thd = new Thread(r);
        thd.start();
        try {
            thd.join(100000);
            if (thd.isAlive()) {
                thd.interrupt();
                log.info("Cannot send packages (timeout).");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("Interrupted!");
        }
    }

    private void sendPackage(final Package p) {
        send(PackageSerializer.serialize(p));
        if (p.getSn() == 0) {
            p.setCheckSum(PackageCheckSumCreator.addCheckSumToPackage(p));
        }
    }


    /***
     * Event listener class. Listens messages deserialize input port.
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
                    messageCreator(port.readString(event.getEventValue()));
                } catch (SerialPortException e) {
                    log.error("Cannot read message: " + e.getExceptionType());
                }
            }
        }

        /***
         * Creates and displays message.
         * @param newString new message deserialize listener
         */
        private synchronized void messageCreator(String newString) {
            message.append(newString);
            log.trace("Message draft: " + newString);
            if (newString.length() >= 5
                    && message.substring(message.length() - 5, message.length())
                    .equals("$end$")) {
                String data = message.substring(0, message.length() - 5);

                Optional<Package> packageOptional = PackageSerializer.deserialize(data);
                packageOptional.ifPresent(this::processPackage);
                message = new StringBuilder();
            }
        }

        private void sendReturnPackage(int an) {
            Package pack = Package.builder()
                    .an(an)
                    .isReturn(true)
                    .build();
            sendPackage(pack);
        }

        private void processPackage(@NonNull final Package pack) {
            if (pack.isReturn()) {
                int an = pack.getAn();
                packageHashMap.remove(an);

                log.debug("Return tag is arrived, an = " + an);
            } else if (pack.getMessage().equals(END_MESSAGE)) {
                printPackages();
            } else {
                if (PackageCheckSumCreator.validateCheckSumToPackage(pack)) {
                    arrivedPackages.add(pack);
                    sendReturnPackage(pack.getAn());
                    log.debug("Package tag is arrived, checksum is valid, sn = " + pack.getSn());
                    log.debug("Package message: " + pack.getMessage());
                } else {
                    log.warn("Sum is not valid!");
                }
            }
        }

        private void printPackages() {
            List<Package> packages = arrivedPackages.stream()
                    .sorted(Comparator.comparingInt(Package::getSn))
                    .collect(Collectors.toList());
            Set<Package> packageSet = new TreeSet<>(arrivedPackages);
            packages.forEach(p -> {
                log.info("Package #" + p.getSn() + ": ");
                log.info("Message: " + p.getMessage());
            });

            StringBuilder sb = new StringBuilder();
            packageSet.forEach(p -> sb.append(p.getMessage()));

            log.info("Whole message: " + sb);
        }
    }
}