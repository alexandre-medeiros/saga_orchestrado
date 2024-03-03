package br.com.himax.saga.orchestration.inventory.core.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.HashMap;
import java.util.Map;

import static br.com.himax.saga.orchestration.inventory.core.enums.ETopics.BASE_ORCHESTRATOR;
import static br.com.himax.saga.orchestration.inventory.core.enums.ETopics.INVENTORY_FAIL;
import static br.com.himax.saga.orchestration.inventory.core.enums.ETopics.INVENTORY_SUCCESS;

@RequiredArgsConstructor
@Configuration
@EnableKafka
public class KafkaConfig {
    private static final Integer PARTITION_COUNT = 1;
    private static final Integer REPLICA_COUNT = 1;
    
//    @Value("spring.kafka.bootstrapServers")
    private String bootstrapServers = "localhost:9092";
    @Value("spring.kafka.consumer.group-id")
    private String groupId;
    @Value("spring.kafka.consumer.auto-offset-reset")
    private String autoOffsetReset;
    
    @Bean
    public ConsumerFactory<String,String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }
    @Bean
    public ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerProps());
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
    @Bean
    public NewTopic orchestratorTopic(){
        return builderTopic(BASE_ORCHESTRATOR.getTopic());
    }
    @Bean
    public NewTopic inventorySuccessTopic(){
        return builderTopic(INVENTORY_SUCCESS.getTopic());
    }
    @Bean
    public NewTopic inventoryFailTopic(){
        return builderTopic(INVENTORY_FAIL.getTopic());
    }    
    private Map<String, Object> consumerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    private Map<String, Object> producerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    private NewTopic builderTopic(String name){
        return TopicBuilder
                .name(name)
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .build();
    }    
}
