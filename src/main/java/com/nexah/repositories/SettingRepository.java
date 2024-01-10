package com.nexah.repositories;

import com.nexah.models.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends MongoRepository<Setting, String> {
    List<Setting> findAll();
}
