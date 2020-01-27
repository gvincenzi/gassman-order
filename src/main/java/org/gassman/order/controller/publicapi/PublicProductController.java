package org.gassman.order.controller.publicapi;

import org.gassman.order.dto.PublicProductDTO;
import org.gassman.order.entity.Order;
import org.gassman.order.entity.Product;
import org.gassman.order.repository.OrderRepository;
import org.gassman.order.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/products")
public class PublicProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        return new ResponseEntity<>(productRepository.findByActiveTrueAndDeliveryDateTimeAfter(LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicProductDTO> findProductById(@PathVariable Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            PublicProductDTO publicProductDTO = new PublicProductDTO();
            List<Order> orders = orderRepository.findByProduct(product.get());
            Integer quantity = 0;
            for (Order order : orders){
                quantity+=order.getQuantity();
            }

            publicProductDTO.setAvailableQuantity(product.get().getAvailableQuantity());
            publicProductDTO.setBookedQuantity(quantity);
            publicProductDTO.setDeliveryDateTime(product.get().getDeliveryDateTime());
            publicProductDTO.setDescription(product.get().getDescription());
            publicProductDTO.setName(product.get().getName());
            publicProductDTO.setPricePerUnit(product.get().getPricePerUnit());
            publicProductDTO.setProductId(product.get().getProductId());
            publicProductDTO.setUnitOfMeasure(product.get().getUnitOfMeasure());
            publicProductDTO.setActive(product.get().getActive());

            return new ResponseEntity<>(publicProductDTO, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }
}
