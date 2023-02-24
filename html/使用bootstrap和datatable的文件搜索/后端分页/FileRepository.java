package com.example.filesearch.repository;

import com.example.filesearch.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Page<FileEntity> findByFileNameIn(List<String> fileNames, Pageable pageable);
}
