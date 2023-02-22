打包插件
gem build logstash-filter-example.gemspec

安装插件
bin/logstash-plugin install /path/to/logstash-filter-example-0.1.0.gem

配置

input {
  # Your input configuration goes here
}

filter {
  example { }
}

output {
  # Your output configuration goes here
}
