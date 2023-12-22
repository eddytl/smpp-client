package com.nexah.repositories;

import com.nexah.models.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends MongoRepository<Setting, String> {
    Optional<Setting> findById(String id);
}
