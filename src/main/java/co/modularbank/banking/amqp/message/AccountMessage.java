package co.modularbank.banking.amqp.message;

import co.modularbank.banking.controller.model.AccountResponse;

public class AccountMessage extends BaseRabbitMessage {
    public AccountResponse payload;

    public AccountMessage() {}

    public AccountMessage(String type, String message, AccountResponse payload) {
        super(type, message);
        this.payload = payload;
    }

    public AccountMessage(String message, AccountResponse payload) {
        super(BaseRabbitMessage.TYPE_ACCOUNT, message);
        this.payload = payload;
    }

    public AccountResponse getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "AccountMessage{" +
                "payload=" + payload +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}