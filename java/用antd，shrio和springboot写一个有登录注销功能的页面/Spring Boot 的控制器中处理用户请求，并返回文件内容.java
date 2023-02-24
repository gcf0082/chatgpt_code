@GetMapping("/file")
@ResponseBody
public String getFileContent(@RequestParam("path") String filePath) throws IOException {
    // 读取文件内容
    String fileContent = FileUtil.readFile(filePath);
    return fileContent;
}
