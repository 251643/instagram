package com.example.intermediate.repository;

import com.example.intermediate.model.DeletedUrlPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedUrlPathRepository extends JpaRepository<DeletedUrlPath,Long> {
}