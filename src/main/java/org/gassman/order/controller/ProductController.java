package org.gassman.order.controller;

import org.gassman.order.entity.Order;
import org.gassman.order.entity.Product;
import org.gassman.order.repository.OrderRepository;
import org.gassman.order.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MessageChannel productUpdateChannel;
    @Autowired
    private MessageChannel productCancellationChannel;
    @Autowired
    private MessageChannel orderCancellationChannel;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        return new ResponseEntity<>(productRepository.findByActiveTrueAndDeliveryDateTimeAfter(LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(productRepository.findByDeliveryDateTimeAfter(LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Product>> getAllPastProducts(){
        return new ResponseEntity<>(productRepository.findByDeliveryDateTimeBeforeOrderByDeliveryDateTimeDesc(LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> findProductOrders(@PathVariable Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()) {
            return new ResponseEntity<>(orderRepository.findByProduct(product.get()), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> findProductById(@PathVariable Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @PostMapping
    public ResponseEntity<Product> postProduct(@RequestBody Product product){
        return new ResponseEntity<>(productRepository.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> putProduct(@PathVariable Long id, @RequestBody Product productToUpdate){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productToUpdate.setProductId(id);
            if(!product.get().equals(productToUpdate)){
                productToUpdate = productRepository.save(productToUpdate);
                sendUserOrdersMessage(orderRepository.findByProduct(productToUpdate));
            }
            return new ResponseEntity<>(productToUpdate, HttpStatus.ACCEPTED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id){
        if(productRepository.existsById(id)){
            Optional<Product> productToDelete = productRepository.findById(id);
            List<Order> orders = orderRepository.findByProduct(productToDelete.get());
            orderRepository.deleteAll(orders);
            sendUserOrdersCancellationMessage(orders);

            productRepository.deleteById(id);

            Message<Product> msg = MessageBuilder.withPayload(productToDelete.get()).build();
            productCancellationChannel.send(msg);

            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    private void sendUserOrdersMessage(List<Order> orders) {
        for(Order order : orders) {
            Message<Order> msg = MessageBuilder.withPayload(order).build();
            productUpdateChannel.send(msg);
        }
    }

    private void sendUserOrdersCancellationMessage(List<Order> orders) {
        for(Order order : orders) {
            Message<Order> msg = MessageBuilder.withPayload(order).build();
            orderCancellationChannel.send(msg);
        }
    }
}
