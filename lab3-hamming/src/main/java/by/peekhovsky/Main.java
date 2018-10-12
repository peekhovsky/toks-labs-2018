package by.peekhovsky;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main  {

    synchronized static void print(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {

        boolean portIsOpened = false;
        Scanner scanner = new Scanner(System.in);

        MessengerCore messengerCore = new MessengerCore();

        String baudName = "9600";
        String portName;

        while (true) {

            ArrayList<String> portNames = messengerCore.getPortNames();

            if (portNames.isEmpty()) {
                print("There is no ports in your computer.");
                return;
            } else {
                portName = portNames.get(0);
            }


            while (!portIsOpened) {

                print("1 - Connect, 2 - Change port, 3 - Change baud, 0 - Exit");
                print("Port: " + portName);
                print("Baud: " + baudName);

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
                        if (messengerCore.connect(portName, baudName)) {
                            portIsOpened = true;
                        }
                        break;
                    case 2: {
                        print("Available ports: " + portNames);
                        print("Print a name of port: ");
                        String s = scanner.next();
                        if (portNames.contains(s)) {
                            portName = s;
                            print("Port name has been changed.");
                        } else {
                            print("Wrong name of a port!");
                        }
                        break;
                    }
                    case 3: {
                        print("Available bauds: " + Arrays.toString(MessengerCore.speeds));
                        print("Print a baud: ");
                        String s = scanner.next();
                        if (Arrays.binarySearch(MessengerCore.speeds, s) != -1) {
                            baudName = s;
                            print("The baud has been changed.");
                        } else {
                            print("Wrong name of a baud!");
                        }
                        break;
                    }
                    case 0:
                        return;

                }
            }


            while (portIsOpened) {
                print("1 - Print message, 2 - Close port, 0 - Exit");
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

                        scanner.reset();
                        print("Print a message: ");

                        String m = scanner.next();
                        messengerCore.sendMessage(m);
                        break;

                    case 2:
                        portIsOpened = false;
                        messengerCore.stop();
                        break;
                    case 0:
                        portIsOpened = false;
                        if (messengerCore.stop()) {
                            return;
                        }
                        break;
                    default: {
                        print("Wrong input!");
                    }

                }

            }
        }
    }
}
