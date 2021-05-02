package com.demo.kafkaserver.config.listener

import com.demo.kafkaserver.config.model.Reply
import com.demo.kafkaserver.config.model.Request
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component

@Component
class ServerListener {

    @KafkaListener(topics = ["\${kafka.topic.request-topic}"], containerFactory = "requestListenerContainerFactory")
    @SendTo()
    fun listenAndRepeat(request: Request): Reply {
        return Reply(request.id, "val with id ${request.id}")
    }
}