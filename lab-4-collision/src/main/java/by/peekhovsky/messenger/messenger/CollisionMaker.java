package by.peekhovsky.messenger.messenger;

import by.peekhovsky.messenger.app.Main;

import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class CollisionMaker {

    private Random random;

    CollisionMaker() {
        random = new Random();
    }

    public static final int CHANNEL_IS_BUSY_FLAG  = 0;
    public static final int COLLISION_IS_DETECTED_FLAG = 1;
    public static final int MESSAGE_HAS_BEEN_SENT = 2;
    public static final String JAM_FLAG = "$$$$$$$$$$$$$$$$";

    int tryCount = 0;

    private boolean isChannelEmpty() {
        return random.nextInt() % 5 == 0;
    }

    private boolean isCollisionDetected() {
        return !(random.nextInt() % 3 == 0);
    }

    public void sendMessage(MessengerCore messengerCore, String mes) throws InterruptedException {
        int tryCount = 0;
        int pauseTime = 0;

        while (true) {

            if ((tryCount > 0) && (tryCount <= 10)) {
                Main.print("Trying again...");
                tryCount++;
            }
            else if (tryCount > 10) {
                Main.print("Cannot solve messenger.");
                break;
            }

            int flag = send(messengerCore, mes, pauseTime);

            if (flag == CHANNEL_IS_BUSY_FLAG) {
                Main.print("Cannot send message, port is busy!");
            }
            else if (flag == COLLISION_IS_DETECTED_FLAG) {
                Main.print("Collision is detected. Trying to solve...");
                pauseTime = random.nextInt() % 1000;
                send(messengerCore, JAM_FLAG, 0);
            }
            else if (flag == MESSAGE_HAS_BEEN_SENT) {
                break;
            }
        }
    }

    private int send(MessengerCore messengerCore, String mes, int pauseTime) throws InterruptedException {

        while (true) {
            if (!isChannelEmpty()) {
                return CHANNEL_IS_BUSY_FLAG;
            }
            Thread.sleep(pauseTime);
            messengerCore.sendMessage(mes);

            if (isCollisionDetected()) {
                return COLLISION_IS_DETECTED_FLAG;
            }
        }
    }
}
