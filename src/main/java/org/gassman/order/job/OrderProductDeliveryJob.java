package org.gassman.order.job;

import org.gassman.order.entity.Order;
import org.gassman.order.repository.OrderRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class OrderProductDeliveryJob extends Thread implements InitializingBean {
    private static OrderProductDeliveryJob instance;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public static OrderProductDeliveryJob getInstance() {
        return instance;
    }

    @Autowired
    OrderRepository orderRepository;

    @Value("${job.orderProductDeliveryReminderTimeout}")
    public Integer orderProductDeliveryReminderTimeout;

    @Value("${job.delay}")
    public Integer delay;

    @Autowired
    private MessageChannel orderProductDeliveryReminderChannel;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }

            LocalDateTime now = LocalDateTime.now();
            List<Order> orderList = orderRepository.findByProduct_DeliveryDateTimeAfter(LocalDateTime.now());
            for (Order order : orderList) {
                long minutes = ChronoUnit.MINUTES.between(now, order.getProduct().getDeliveryDateTime());
                if (minutes <= orderProductDeliveryReminderTimeout && !order.getDeliveryReminder()) {
                    order.setDeliveryReminder(Boolean.TRUE);
                    orderRepository.save(order);
                    Message<Order> msg = MessageBuilder.withPayload(order).build();
                    orderProductDeliveryReminderChannel.send(msg);
                }
            }
        }
    }
}
