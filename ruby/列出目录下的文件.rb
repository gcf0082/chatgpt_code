Dir.foreach('/path/to/directory') do |file|
  if file.end_with?('.txt')
    puts file
  end
end
