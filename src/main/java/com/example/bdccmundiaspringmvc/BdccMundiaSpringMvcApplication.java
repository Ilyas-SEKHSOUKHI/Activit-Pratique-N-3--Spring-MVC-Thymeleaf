package com.example.bdccmundiaspringmvc;

import com.example.bdccmundiaspringmvc.entities.Product;
import com.example.bdccmundiaspringmvc.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BdccMundiaSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BdccMundiaSpringMvcApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(ProductRepository productRepository){
        return args -> {
          productRepository.save(Product.builder()
                  .name("iphone 13").price(3200).quantity(100).build());

          productRepository.save(Product.builder()
                    .name("iphone 15").price(3700).quantity(100).build());

          productRepository.save(Product.builder()
                    .name("iphone 17").price(3900).quantity(100).build());

          productRepository.findAll().forEach(p -> {
              System.out.println(p.toString());
          });
        };


    }
}
