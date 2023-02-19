import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SSHClient {
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String COMMANDS = "commands";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // 从命令行参数中读取连接参数
        String host = args[0];
        String user = args[1];
        String password = args[2];
        String newPassword = args[3];
        String commands = args[4];

        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        // 创建 SSH session
        ClientSession session = client.connect(user, host, 22).verify(5, TimeUnit.SECONDS).getSession();
        session.addPasswordIdentity(password);
        session.auth().verify();

        // 切换用户
        ChannelExec channel = session.createExecChannel("su -");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        channel.setOut(new NoCloseOutputStream(outputStream));
        InputStream in = new NoCloseInputStream(channel.getInvertedIn());
        channel.open().await();
        channel.getInvertedOut().write((newPassword + "\n").getBytes(StandardCharsets.UTF_8));
        channel.getInvertedOut().flush();
        in.read(new byte[1024]);

        // 执行多个命令
        List<String> cmds = List.of(commands.split(";"));
        for (String cmd : cmds) {
            channel = session.createExecChannel(cmd);
            outputStream = new ByteArrayOutputStream();
            channel.setOut(new NoCloseOutputStream(outputStream));
            channel.open().await();
            channel.getInvertedOut().write(new byte[1]); // send an EOF
            channel.getInvertedOut().flush();
            String output = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            System.out.print(output);
            channel.waitFor(ClientChannel.CLOSED, 0L);
            channel.close(false);
        }

        // 关闭连接
        session.close();
        client.stop();
    }
}
