package com.demo.kafkaclient.service

import com.demo.kafkaclient.model.Reply
import com.demo.kafkaclient.model.Request
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.requestreply.RequestReplyFuture
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.stereotype.Service

@Service
class RequestReplyService(
        private val replyingKafkaTemplate: ReplyingKafkaTemplate<Long, Request, Reply>
) {
    @Value("\${kafka.topic.request-topic}")
    private lateinit var requestTopic: String

    @Value("\${kafka.topic.reply-topic}")
    private lateinit var replyTopic: String

    fun getData(id: Long): Reply {
        //create producer record
        val record = ProducerRecord<Long, Request>(requestTopic, Request(id))
        // set topic for response in header
        record.headers().add(RecordHeader (KafkaHeaders.REPLY_TOPIC, replyTopic.toByteArray()))
        // send request and get reply (asynchronously or not)
        val sendAndReceive: RequestReplyFuture<Long, Request, Reply> = replyingKafkaTemplate.sendAndReceive(record)
        val consumerRecord = sendAndReceive.get()
        return consumerRecord.value()
//        sendAndReceive.addCallback(object: ListenableFutureCallback<ConsumerRecord<String, Reply>> {
//            override fun onSuccess(result: ConsumerRecord<String, Reply>?) {
//                // получаем значение consumer record
//                result?.let {
//                    println("Reply: ${it.value()}")
//                }
//            }
//
//            override fun onFailure(ex: Throwable) {
//                println("Exception: ${ex.message}")
//            }
//        })
    }
}