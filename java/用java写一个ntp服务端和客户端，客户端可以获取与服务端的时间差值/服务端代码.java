import java.net.*;
import java.io.*;
import java.util.*;

public class NTPServer {
    private static final long EPOCH_DIFF = 2208988800L;

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(123);
            byte[] buffer = new byte[48];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                long receiveTime = System.currentTimeMillis() / 1000 + EPOCH_DIFF;
                long transmitTime = System.currentTimeMillis() / 1000 + EPOCH_DIFF;

                buffer[0] = (byte) 0x1B;
                buffer[1] = (byte) 0x00;
                buffer[2] = (byte) 0x00;
                buffer[3] = (byte) 0x00;
                buffer[4] = (byte) 0x00;
                buffer[5] = (byte) 0x00;
                buffer[6] = (byte) 0x00;
                buffer[7] = (byte) 0x00;
                buffer[8] = (byte) ((transmitTime >> 24) & 0xFF);
                buffer[9] = (byte) ((transmitTime >> 16) & 0xFF);
                buffer[10] = (byte) ((transmitTime >> 8) & 0xFF);
                buffer[11] = (byte) (transmitTime & 0xFF);
                buffer[12] = (byte) ((receiveTime >> 24) & 0xFF);
                buffer[13] = (byte) ((receiveTime >> 16) & 0xFF);
                buffer[14] = (byte) ((receiveTime >> 8) & 0xFF);
                buffer[15] = (byte) (receiveTime & 0xFF);

                DatagramPacket response = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
