filename = "example.txt"
last_modified = File.mtime(filename)
loop do
  if File.mtime(filename) > last_modified
    puts "File has been updated!"
    last_modified = File.mtime(filename)
  end
  sleep 5  # 等待5秒钟再次检查文件是否更新
end
