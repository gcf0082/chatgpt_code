在插件的src/main/resources目录下，创建META-INF/services/org.elasticsearch.ingest.Processor文件，并在其中添加自定义Processor类的全限定名：
com.example.ingest.CustomProcessor

在插件的src/main/resources目录下，创建plugin-descriptor.properties文件，并添加插件描述信息：
description=Custom ingest processor plugin
version=1.0
name=ingest-custom

编译打包插件，并将生成的插件包ingest-custom-1.0.zip上传到Elasticsearch的plugins目录下

在Elasticsearch中创建索引，定义映射，使用Ingest Pipeline处理数据
PUT /my-index
{
  "mappings": {
    "properties": {
      "data": {
        "type": "text"
      }
    }
  }
}

PUT _ingest/pipeline/my-pipeline
{
  "description": "Custom pipeline",
  "processors": [
    {
      "custom": {}
    }
  ]
}

POST my-index/_doc?pipeline=my-pipeline
{
  "data": "{\"field1\": \"value1\", \"field2\": \"value2\"}"
}
