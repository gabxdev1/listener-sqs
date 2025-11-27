package br.com.gabxdev.domain.ports;

import br.com.gabxdev.domain.model.ProcessResult;
import software.amazon.awssdk.services.sqs.model.Message;

public interface SqsMessageProcessor {

    ProcessResult process(Message message);
}
