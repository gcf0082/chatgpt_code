package com.example.filesearch.service;

import com.example.filesearch.dto.FileDto;
import com.example.filesearch.entity.FileEntity;
import com.example.filesearch.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
this.fileRepository = fileRepository;
}

  public List<FileDto> searchFiles(List<String> fileNames, PageRequest pageRequest) {
    Page<FileEntity> fileEntities = fileRepository.findByFileNameIn(fileNames, pageRequest);

    List<FileDto> fileDtos = new ArrayList<>();
    for (FileEntity fileEntity : fileEntities.getContent()) {
        fileDtos.add(new FileDto(fileEntity.getFileName(), fileEntity.getFilePath()));
    }

    return fileDtos;
}
}
