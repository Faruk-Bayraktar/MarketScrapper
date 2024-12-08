package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.MigrosProduct;

public interface MigrosDataRepository extends MongoRepository<MigrosProduct, String> {
}
