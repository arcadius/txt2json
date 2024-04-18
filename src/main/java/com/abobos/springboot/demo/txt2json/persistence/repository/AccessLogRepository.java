package com.abobos.springboot.demo.txt2json.persistence.repository;

import java.util.UUID;

import com.abobos.springboot.demo.txt2json.persistence.entity.AccessLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends CrudRepository<AccessLog, UUID> {
    AccessLog findByRequestId(UUID requestId);
}
