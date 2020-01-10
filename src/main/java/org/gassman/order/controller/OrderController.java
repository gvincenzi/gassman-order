package org.gassman.order.controller;

import org.gassman.order.entity.Order;
import org.gassman.order.entity.Product;
import org.gassman.order.entity.User;
import org.gassman.order.repository.OrderRepository;
import org.gassman.order.repository.ProductRepository;
import org.gassman.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MessageChannel userOrderChannel;
    @Autowired
    private MessageChannel orderCancellationChannel;

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Order>> findAllOrdersByUser(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return new ResponseEntity<>(orderRepository.findByUserAndAndProduct_DeliveryDateTimeAfter(user.get(), LocalDateTime.now()), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(){
        return new ResponseEntity<>(orderRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Order>> findOrderById(@PathVariable Long id){
        return new ResponseEntity<>(orderRepository.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> postOrder(@RequestBody Order order){
        setJoinedEntities(order);
        return createOrder(order);
    }

    private ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Integer updateAvailableQuantity = order.getProduct().getAvailableQuantity() - order.getQuantity();
        if (updateAvailableQuantity < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Quantity reached is not available", null);
        }

        order.getProduct().setAvailableQuantity(updateAvailableQuantity);
        productRepository.save(order.getProduct());
        Order orderPersisted = orderRepository.save(order);
        sendUserOrderMessage(orderPersisted);
        return new ResponseEntity<>(orderPersisted, HttpStatus.CREATED);
    }

    @PostMapping("/telegram")
    public ResponseEntity<Order> postOrderByTelegram(@RequestBody Order order){
        Optional<User> user = userRepository.findByTelegramUserId(order.getUser().getTelegramUserId());
        Optional<Product> product = productRepository.findById(order.getProduct().getProductId());
        if(!user.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with Telegram ID %d does not exists",order.getUser().getTelegramUserId()), null);
        }
        if(!product.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Product ID %d does not exists",order.getProduct().getProductId()), null);
        }
        user.ifPresent(order::setUser);
        product.ifPresent(order::setProduct);
        return createOrder(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> putOrder(@PathVariable Long id, @RequestBody Order order) {
        Optional<Order> orderPersisted = orderRepository.findById(id);
        if(orderPersisted.isPresent()){
            Integer updateAvailableQuantity = orderPersisted.get().getProduct().getAvailableQuantity() + orderPersisted.get().getQuantity() - order.getQuantity();
            if(updateAvailableQuantity < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Quantity reached is not available", null);
            }
            order.setOrderId(id);
            order.setUser(orderPersisted.get().getUser());
            order.setProduct(orderPersisted.get().getProduct());
            order.getProduct().setAvailableQuantity(updateAvailableQuantity);
            productRepository.save(order.getProduct());
            return new ResponseEntity<>(orderRepository.save(order), HttpStatus.ACCEPTED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable Long id){
        Optional<Order> orderPersisted = orderRepository.findById(id);
        if(orderPersisted.isPresent()){
            Integer updateAvailableQuantity = orderPersisted.get().getProduct().getAvailableQuantity() + orderPersisted.get().getQuantity();
            orderPersisted.get().getProduct().setAvailableQuantity(updateAvailableQuantity);
            productRepository.save(orderPersisted.get().getProduct());
            orderRepository.deleteById(id);

            Message<Order> msg = MessageBuilder.withPayload(orderPersisted.get()).build();
            orderCancellationChannel.send(msg);

            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    private void setJoinedEntities(@RequestBody Order order) {
        Optional<User> user = userRepository.findById(order.getUser().getId());
        Optional<Product> product = productRepository.findById(order.getProduct().getProductId());
        if(!user.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User ID %d does not exists",order.getUser().getId()), null);
        }
        if(!product.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Product ID %d does not exists",order.getProduct().getProductId()), null);
        }
        user.ifPresent(order::setUser);
        product.ifPresent(order::setProduct);

        order.setDeliveryReminder(order.getDeliveryReminder() == null ? Boolean.FALSE : order.getDeliveryReminder());
        order.setPaymentReminder(order.getPaymentReminder() == null ? Boolean.FALSE : order.getPaymentReminder());
    }

    private void sendUserOrderMessage(Order order) {
        Message<Order> msg = MessageBuilder.withPayload(order).build();
        userOrderChannel.send(msg);
    }
}
