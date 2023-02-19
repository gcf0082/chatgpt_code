import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SSHClient {
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String COMMANDS = "commands";

    public static void main(String[] args) throws JSchException, IOException {
        // 从命令行参数中读取连接参数
        String host = args[0];
        String user = args[1];
        String password = args[2];
        String newPassword = args[3];
        String commands = args[4];

        JSch jsch = new JSch();

        // 创建 SSH session
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        // 切换用户
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("su -");
        channel.connect();
        InputStream in = channel.getInputStream();
        channel.getOutputStream().write((newPassword + "\n").getBytes());
        channel.getOutputStream().flush();
        in.read(new byte[1024]);

        // 执行多个命令
        for (String cmd : commands.split(";")) {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(cmd);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream out = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (out.available() > 0) {
                    int i = out.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (out.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
        }

        // 关闭连接
        session.disconnect();
    }
}
