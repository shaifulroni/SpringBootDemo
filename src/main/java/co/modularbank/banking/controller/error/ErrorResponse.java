package co.modularbank.banking.controller.error;

import java.util.List;

public class ErrorResponse {
    private int code;
    private List<String> message;

    public ErrorResponse(int code, List<String> message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }
}
