package com.example.product_service.service;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    private final Bucket createProductBucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(10))))
            .build();

    public void createProduct(ProductRequest productRequest)
    {
        if(createProductBucket.tryConsume(1)){
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .build();
            productRepository.save(product);
            log.info("Product {} is saved" , product.getId());
        } else{
            throw new RuntimeException("Rate limit exceed.Please wait before creating another product.");
        }

    }


}
