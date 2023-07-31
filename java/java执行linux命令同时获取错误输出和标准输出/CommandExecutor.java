import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class CommandExecutor {
    public static void main(String[] args) {
        try {
            // 创建一个 ProcessBuilder 对象，并设置要执行的命令
            ProcessBuilder processBuilder = new ProcessBuilder("ls", "-l", "/nonexistent_folder");

            // 启动进程
            Process process = processBuilder.start();

            // 异步读取标准输出和错误输出
            CompletableFuture<Void> stdOutputFuture = readStreamAsync(process.getInputStream(), "标准输出:");
            CompletableFuture<Void> errOutputFuture = readStreamAsync(process.getErrorStream(), "错误输出:");

            // 等待两个异步任务执行完成
            CompletableFuture.allOf(stdOutputFuture, errOutputFuture).join();

            // 等待进程执行完成
            int exitCode = process.waitFor();

            // 输出进程的退出码
            System.out.println("命令执行完成，退出码: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static CompletableFuture<Void> readStreamAsync(InputStream inputStream, String prefix) {
        return CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                System.out.println(prefix);
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
