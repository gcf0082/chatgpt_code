import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class FlinkKafkaExample {

    public static void main(String[] args) throws Exception {

        // set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure Kafka consumer properties
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "flink-consumer-group");
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JSONKeyValueDeserializationSchema.class.getName());
        consumerProps.setProperty(JSONKeyValueDeserializationSchema.JSON_KEY_FIELDS, "name,age,gender");
        consumerProps.setProperty(JSONKeyValueDeserializationSchema.JSON_VALUE_TYPE, "com.example.User");

        // create a Kafka consumer and add it as a data source
        DataStream<User> input = env
                .addSource(new FlinkKafkaConsumer<>("user-topic", new JSONKeyValueDeserializationSchema(false), consumerProps))
                .map(record -> record.value())
                .returns(User.class);

        // apply a simple filter transformation to the input stream
        DataStream<User> filtered = input.filter((FilterFunction<User>) user -> user.getAge() > 18);

        // apply a simple map transformation to the filtered stream
        DataStream<String> transformed = filtered.map(user -> user.getName() + " is an adult");

        // configure Kafka producer properties
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create a Kafka producer and add it as a sink
        transformed.addSink(new FlinkKafkaProducer<>("output-topic", new SimpleStringSchema(), producerProps));

        // execute the Flink job
        env.execute("Flink Kafka Example");
    }

    public static class User {

        private String name;
        private int age;
        private String gender;

        public User() {}

        public User(String name, int age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
  this.age = age;
}
          public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}
}
