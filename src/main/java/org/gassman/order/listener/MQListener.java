package org.gassman.order.listener;

import org.gassman.order.binding.MQBinding;
import org.gassman.order.dto.PaymentDTO;
import org.gassman.order.entity.Order;
import org.gassman.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.util.Optional;

@EnableBinding(MQBinding.class)
public class MQListener {
    @Autowired
    OrderRepository orderRepository;

    @StreamListener(target = MQBinding.ORDER_PAYMENT)
    public void processUserOrder(PaymentDTO msg) {
        Optional<Order> orderPersisted = orderRepository.findById(msg.getOrderId());
        if(orderPersisted.isPresent()){
            orderPersisted.get().setPayed(Boolean.TRUE);
            orderPersisted.get().setPaymentExternalReference(msg.getPaymentId());
            orderPersisted.get().setPaymentExternalDateTime(msg.getPaymentDateTime());
            orderRepository.save(orderPersisted.get());
        }
    }
}
