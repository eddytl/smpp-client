package com.nexah.repositories;

import com.nexah.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Optional<Message> findById(String id);

    List<Message> findAll();
    Page<Message> findAll(Pageable pageable);
    Message findByRequestId(String requestId);
    Message findByRequestIdAndStatus(String requestId, String status);
}
