public class FileUtil {
    
    /**
     * 读取文件内容
     * 
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException
     */
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
}
