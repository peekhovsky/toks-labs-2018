import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main  {

    synchronized static void print(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        MessengerCore messengerCore = new MessengerCore();

        String baudName = "9600";
        String portName;

        ArrayList<String> portNames = messengerCore.getPortNames();

        if (portNames.isEmpty()) {
            print("There is no ports in your computer.");
            return;
        } else {
            portName = portNames.get(0);
        }

        boolean menuIsOpened = true;
        while (menuIsOpened) {
            print("1 - Connect, 2 - Change port, 3 - Change baud, 0 - Exit");
            print("Port: " + portName);
            print("Baud: " + baudName);

            int t = scanner.nextInt();
            switch (t) {
                case 1:
                   if (messengerCore.connect(portName, baudName)) {
                       menuIsOpened = false;
                   }
                    break;
                case 2: {
                    print("Available ports: " + portNames);
                    print("Print a name of port: ");
                    String s = scanner.nextLine();
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
                    String s = scanner.nextLine();
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
    }
}
