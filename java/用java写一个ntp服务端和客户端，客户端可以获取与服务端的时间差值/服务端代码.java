import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.NtpV3Impl;
import org.apache.commons.net.ntp.NtpUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NTPServer {
    public static void main(String[] args) throws Exception {
        // 设置监听端口
        int port = 123;

        // 创建 DatagramSocket 对象
        DatagramSocket socket = new DatagramSocket(port);
        byte[] buffer = new byte[48];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            // 接收请求
            socket.receive(packet);

            // 解析请求
            NtpV3Impl ntpRequest = new NtpV3Impl();
            ntpRequest.setDatagramPacket(packet);
            ntpRequest.parse();

            // 处理请求
            NtpV3Impl ntpResponse = new NtpV3Impl();
            ntpResponse.setMode(NtpV3Packet.MODE_SERVER);
            ntpResponse.setStratum(1);
            ntpResponse.setVersion(ntpRequest.getVersion());
            ntpResponse.setReferenceTime(ntpRequest.getTransmitTimeStamp());
            ntpResponse.setOriginateTimeStamp(ntpRequest.getTransmitTimeStamp());
            ntpResponse.setReceiveTimeStamp(System.currentTimeMillis());
            ntpResponse.setTransmitTimeStamp(System.currentTimeMillis());

            // 发送响应
            byte[] data = ntpResponse.toByteArray();
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(packet);
        }
    }
}
