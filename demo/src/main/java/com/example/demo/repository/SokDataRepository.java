package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.SokProduct;

public interface SokDataRepository extends MongoRepository<SokProduct, String> {
}
