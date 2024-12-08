package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.A101Product;

public interface A101DataRepository extends MongoRepository<A101Product, String> {
}
