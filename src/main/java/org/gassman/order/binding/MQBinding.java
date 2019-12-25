package org.gassman.order.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MQBinding {
    String USER_REGISTRATION = "userRegistrationChannel";
    String USER_ORDER = "userOrderChannel";
    String ORDER_PAYMENT = "orderPaymentChannel";
    String ORDER_PAYMENT_CONFIRMATION = "orderPaymentConfirmationChannel";

    @Output(USER_REGISTRATION)
    MessageChannel userRegistrationChannel();

    @Output(USER_ORDER)
    MessageChannel userOrderChannel();

    @Input(ORDER_PAYMENT)
    SubscribableChannel orderPaymentChannel();

    @Output(ORDER_PAYMENT_CONFIRMATION)
    MessageChannel userOrderPaymentConfirmationChannel();
}
