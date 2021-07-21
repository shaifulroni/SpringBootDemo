package co.modularbank.banking.amqp.message;

import co.modularbank.banking.controller.model.SaveTransactionResponse;

public class TransactionMessage extends BaseRabbitMessage {
    public SaveTransactionResponse payload;

    public TransactionMessage() { }

    public TransactionMessage(String type, String message, SaveTransactionResponse payload) {
        super(type, message);
        this.payload = payload;
    }

    public TransactionMessage(String message, SaveTransactionResponse payload) {
        super(BaseRabbitMessage.TYPE_TRANSACTION, message);
        this.payload = payload;
    }

    public SaveTransactionResponse getPayload() {
        return payload;
    }

    public void setPayload(SaveTransactionResponse payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "TransactionMessage{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", payload=" + payload +
                '}';
    }
}