package co.modularbank.banking.controller.error;

import java.util.List;

public class ErrorResponse {
    private final int code;
    private final List<String> message;

    public ErrorResponse(int code, List<String> message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public List<String> getMessage() {
        return message;
    }
}
