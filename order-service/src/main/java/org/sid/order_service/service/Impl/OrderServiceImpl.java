package org.sid.order_service.service.Impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.sid.order_service.entities.Order;
import org.sid.order_service.entities.ProductItem;
import org.sid.order_service.models.Customer;
import org.sid.order_service.models.Product;
import org.sid.order_service.repository.OrderRepository;
import org.sid.order_service.repository.ProductItemRepository;
import org.sid.order_service.service.OrderEventPublisher;
import org.sid.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    private final String CUSTOMER_SERVICE_URL = "http://localhost:8081/customers/";
    private final String PRODUCT_SERVICE_GRAPHQL_URL = "http://localhost:8082/graphql";

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProduct")
    public Order createOrder(Order order) {
        // Fetch customer details
        Customer customer = restTemplate.getForObject(CUSTOMER_SERVICE_URL + order.getCustomerId(), Customer.class);
        if (customer == null) throw new RuntimeException("Customer not found");

        order.setCustomer(customer);
        order.setCreatedAt(new Date());

        // Save the order
        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.sendOrderEvent(savedOrder);

        // Fetch product details and save product items
        for (ProductItem productItem : order.getProductItems()) {
            Product product = fetchProductFromGraphQL(productItem.getProductId());
            if (product == null) throw new RuntimeException("Product not found");

            productItem.setProduct(product);
            productItem.setOrder(savedOrder);
            productItem.setPrice(product.getPrice());
            productItemRepository.save(productItem);
        }

        return savedOrder;
    }

    private Product fetchProductFromGraphQL(Long productId) {
        String query = String.format("{\"query\":\"{ product(id: %d) { id name price quantity } }\"}", productId);
        return restTemplate.postForObject(PRODUCT_SERVICE_GRAPHQL_URL, query, Product.class);
    }

    public Product fallbackGetProduct(Long productId, Throwable throwable) {
        log.error("Fallback triggered for productId {}: {}", productId, throwable.getMessage());
        return null;
    }
}
