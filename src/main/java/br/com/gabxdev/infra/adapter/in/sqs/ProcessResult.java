package br.com.gabxdev.infra.adapter.in.sqs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessResult {

    private Throwable error;

    private boolean success;

    public static ProcessResult failure(Throwable t) {
        return new ProcessResult(t, false);
    }

    public static ProcessResult success() {
        return new ProcessResult(null, true);
    }
}

