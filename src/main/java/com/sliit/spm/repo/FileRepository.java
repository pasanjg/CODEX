package com.sliit.spm.repo;

import com.sliit.spm.model.Files;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<Files,String> {
}
