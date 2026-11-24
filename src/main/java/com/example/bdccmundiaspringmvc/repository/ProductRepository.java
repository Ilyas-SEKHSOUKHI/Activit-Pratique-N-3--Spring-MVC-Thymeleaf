package com.example.bdccmundiaspringmvc.repository;

import com.example.bdccmundiaspringmvc.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product , Long> {
}
