package com.devision.application.repository;

import com.devision.application.model.FileReference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileReferenceRepository extends MongoRepository<FileReference,String> {
}
