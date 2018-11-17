package by.peekhovsky.messenger.messenger;

import by.peekhovsky.messenger.app.Main;

import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class CollisionMaker {

    private Random random;
    MessengerCore messengerCore;

    public CollisionMaker(MessengerCore messengerCore) {
        this.messengerCore = messengerCore;
        random = new Random();
    }

    public static final int CHANNEL_IS_BUSY_FLAG  = 0;
    public static final int COLLISION_IS_DETECTED_FLAG = 1;
    public static final int MESSAGE_HAS_BEEN_SENT = 2;
    public static final String JAM_FLAG = "$$$$$$$$$$$$$$$$";

    private boolean isChannelEmpty() {
        return random.nextInt() % 5 != 0;
    }

    private boolean isCollisionDetected() {
        return random.nextInt() % 3 != 0;
    }

    public void sendMessage(String mes) {

        int pauseTime = 1;

        int tryCount = 0;

        while (true) {
            if (tryCount > 10) {
                Main.print("Cannot solve messenger.");
                break;
            }
            if (tryCount > 0) {
                Main.print("Trying again...");
            }
            if (tryCount >= 0) {
                tryCount++;
            }

            int flag;
            if (tryCount == 0) {
                flag = send(mes, pauseTime, isChannelEmpty());
            } else {
                flag = send(mes, pauseTime, true);
            }

            if (flag == CHANNEL_IS_BUSY_FLAG) {
                Main.print("Cannot send message, port is busy!");
            }
            else if (flag == COLLISION_IS_DETECTED_FLAG) {
                Main.print("Collision is detected. Trying to solve...");
                pauseTime = Math.abs(random.nextInt() % 1000 + 1);
                Main.print("Pause time: " + pauseTime);
            }
            else if (flag == MESSAGE_HAS_BEEN_SENT) {
                break;
            }
        }
    }

    private int send(String mes, int pauseTime, boolean isChannelEmpty){

        while (true) {
            if (!isChannelEmpty) {
                return CHANNEL_IS_BUSY_FLAG;
            }
            try {
                Thread.sleep(pauseTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isCollisionDetected()) {
                messengerCore.sendMessage(JAM_FLAG + mes);
                return COLLISION_IS_DETECTED_FLAG;
            } else {
                messengerCore.sendMessage(mes);
                return MESSAGE_HAS_BEEN_SENT;
            }
        }
    }
}
