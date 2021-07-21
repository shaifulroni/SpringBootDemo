package co.modularbank.banking.amqp.message;

public abstract class BaseRabbitMessage {
    public static final String TYPE_ACCOUNT = "account-message";
    public static final String TYPE_TRANSACTION = "transaction-message";

    protected String type;
    protected String message;

    public BaseRabbitMessage(){}

    public BaseRabbitMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseRabbitMessage{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
