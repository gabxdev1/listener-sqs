package br.com.gabxdev.domain.model;

public class ProcessResult {
    private Exception error;

    private boolean success;

    private ProcessResult() {
    }

    private ProcessResult(Exception error, boolean success) {
        this.success = success;
        this.error = error;
    }

    public static ProcessResult failure(Exception error) {
        return new ProcessResult(error, false);
    }

    public static ProcessResult success() {
        return new ProcessResult(null, true);
    }

    public Exception getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}

