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
    String ORDER_NON_PAID_REMINDER = "orderNonPaidReminderChannel";
    String ORDER_PRODUCT_DELIVERY_REMINDER = "orderProductDeliveryReminderChannel";
    String PRODUCT_UPDATE = "productUpdateChannel";
    String USER_CANCELLATION = "userCancellationChannel";
    String ORDER_CANCELLATION = "orderCancellationChannel";
    String PRODUCT_CANCELLATION = "productCancellationChannel";

    @Output(USER_REGISTRATION)
    MessageChannel userRegistrationChannel();

    @Output(USER_ORDER)
    MessageChannel userOrderChannel();

    @Input(ORDER_PAYMENT)
    SubscribableChannel orderPaymentChannel();

    @Output(ORDER_PAYMENT_CONFIRMATION)
    MessageChannel userOrderPaymentConfirmationChannel();

    @Output(ORDER_NON_PAID_REMINDER)
    MessageChannel orderNonPaidReminderChannel();

    @Output(ORDER_PRODUCT_DELIVERY_REMINDER)
    MessageChannel orderProductDeliveryReminderChannel();

    @Output(PRODUCT_UPDATE)
    MessageChannel productUpdateChannel();

    @Output(USER_CANCELLATION)
    MessageChannel userCancellationChannel();

    @Output(ORDER_CANCELLATION)
    MessageChannel orderCancellationChannel();

    @Output(PRODUCT_CANCELLATION)
    MessageChannel productCancellationChannel();
}
