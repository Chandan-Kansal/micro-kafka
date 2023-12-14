package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Inventory;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ProductRepository productrepo;
    public void createProduct(ProductRequest productRequest){
        Product product = new Product(productRequest.getName(),productRequest.getDescription(),productRequest.getPrice(),productRequest.getQuantity());
        productrepo.save(product);
        log.info("Product {} is saved",product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Inventory> allProducts = List.of(Objects.requireNonNull(webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/allinvent",
                        UriBuilder::build)
                .retrieve()
                .bodyToMono(Inventory[].class)
                .block()));
        for(Inventory i : allProducts){
            System.out.println(i.getSkuCode());
            Product prod = productrepo.findByName(i.getSkuCode());
            prod.setQuantity(i.getQuantity());
            productrepo.save(prod);
        }

        List<Product> products = productrepo.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(product.getId(),product.getName(),product.getDescription(),product.getPrice(),product.getQuantity());
    }
}
