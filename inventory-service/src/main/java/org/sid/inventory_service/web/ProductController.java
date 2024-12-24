package org.sid.inventory_service.web;

import org.sid.inventory_service.entities.Product;
import org.sid.inventory_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import graphql.GraphQLException;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @QueryMapping
    public Product getProductById(@Argument Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            throw new GraphQLException("Product with ID " + id + " not found.");
        }
        return product.get();
    }

    @MutationMapping
    public Product createProduct(
            @Argument String name,
            @Argument double price,
            @Argument int quantity) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
        return productService.saveProduct(product);
    }

    @MutationMapping
    public Product updateProduct(
            @Argument Long id,
            @Argument String name,
            @Argument Double price,
            @Argument Integer quantity) {
        Product product = Product.builder()
                .name(name)
                .price(price != null ? price : 0)
                .quantity(quantity != null ? quantity : 0)
                .build();
        return productService.updateProduct(id, product);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if (id == null) {
            throw new GraphQLException("Id cannot be null.");
        }
        try {
            productService.deleteProduct(id);
            return true;
        } catch (Exception e) {
            throw new GraphQLException("Failed to delete product with ID: " + id);
        }
    }
}
