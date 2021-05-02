package com.demo.kafkaclient.controller

import com.demo.kafkaclient.model.Reply
import com.demo.kafkaclient.service.RequestReplyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(
        private val requestReplyService: RequestReplyService
) {
    @GetMapping("/request/{id}")
    fun getData(@PathVariable("id") id: Long): Reply {
       return requestReplyService.getData(id)
    }
}