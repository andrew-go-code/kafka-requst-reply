package com.demo.kafkaclient.config

import com.demo.kafkaclient.model.Reply
import com.demo.kafkaclient.model.Request
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
class KafkaClientConfig {

    @Value("\${kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${kafka.topic.reply-topic}")
    private lateinit var replyTopic: String

    @Value("\${kafka.consumer-group}")
    private lateinit var consumerGroup: String

    @Bean
    fun producerConfigs(): Map<String, Any> {
        return mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to LongSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
    }

    @Bean
    fun consumerConfigs(): Map<String, Any> {
        return mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to LongDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
//                JsonDeserializer.TRUSTED_PACKAGES to "*",
//                JsonDeserializer.VALUE_DEFAULT_TYPE to "com.demo.kafkaclient.model.Reply"

        )
    }

    @Bean
    fun requestProducerFactory(): ProducerFactory<Long, Request> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun replyConsumerFactory(): ConsumerFactory<Long, Reply> {
        val deserializer = JsonDeserializer(Reply::class.java, false)
//        deserializer.setRemoveTypeHeaders(false)
//        deserializer.addTrustedPackages("*")
//        deserializer.setUseTypeMapperForKey(true)
        return DefaultKafkaConsumerFactory(consumerConfigs(), LongDeserializer(), deserializer)
    }

    @Bean
    fun replyListenerContainer(): KafkaMessageListenerContainer<Long, Reply> {
        val containerProperties = ContainerProperties(replyTopic)
        return KafkaMessageListenerContainer(replyConsumerFactory(), containerProperties)
    }

    @Bean
    fun replyKafkaTemplate(
            producerFactory: ProducerFactory<Long, Request>,
            listenerContainer: KafkaMessageListenerContainer<Long, Reply>): ReplyingKafkaTemplate<Long, Request, Reply> {
        return ReplyingKafkaTemplate(producerFactory, listenerContainer)
    }
}