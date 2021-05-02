package com.demo.kafkaserver.config.config

import com.demo.kafkaserver.config.model.Reply
import com.demo.kafkaserver.config.model.Request
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.converter.BytesJsonMessageConverter
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaServerConfig {

    @Value("\${kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${kafka.consumer-group}")
    private lateinit var consumerGroup: String

    @Bean
    fun consumerConfigs(): Map<String, Any> {
        return mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to LongDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
//                JsonDeserializer.TRUSTED_PACKAGES to "*",
//                JsonDeserializer.VALUE_DEFAULT_TYPE to "com.demo.kafkaserver.config.model.Request"
        )
    }

    @Bean
    fun producerConfigs(): Map<String, Any> {
        return mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to LongSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )
    }

    @Bean
    fun requestConsumerFactory(): ConsumerFactory<Long, Request> {
        val deserializer = JsonDeserializer(Request::class.java, false)
//        deserializer.setRemoveTypeHeaders(false)
//        deserializer.addTrustedPackages("*")
//        deserializer.setUseTypeMapperForKey(true)
        return DefaultKafkaConsumerFactory(consumerConfigs(), LongDeserializer(), deserializer)
    }

    @Bean
    fun messageConverter(): JsonMessageConverter {
        return BytesJsonMessageConverter()
    }

    @Bean
    fun requestListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, Request>> {
        val factory = ConcurrentKafkaListenerContainerFactory<Long, Request>()
        factory.consumerFactory = requestConsumerFactory()
        factory.setReplyTemplate(replyTemplate())
        return factory
    }

    @Bean
    fun replyProducerFactory(): ProducerFactory<Long, Reply> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun replyTemplate(): KafkaTemplate<Long, Reply> {
        return KafkaTemplate(replyProducerFactory())
    }
}