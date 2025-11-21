package br.com.gabxdev.domain.ports;

import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface SqsBatchProcessor {
    void processBatch(List<Message> messages);
}
