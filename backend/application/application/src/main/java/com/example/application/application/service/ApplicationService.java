package com.example.application.application.service;

import com.example.application.application.dto.ApplicationCreateRequest;
import com.example.application.application.dto.ApplicationDTO;
import com.example.application.application.enums.ApplicationStatus;
import com.example.application.application.mapper.ApplicationMapper;
import com.example.application.application.model.Application;
import com.example.application.application.model.FileReference;
import com.example.application.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {
    private final ApplicationRepository repository;



    public List<ApplicationDTO> getApplicationsByApplicantId(String applicantId){
        return repository.findByApplicantId(applicantId)
                .stream()
                .filter(app -> app.getDeletedAt() == null) //exclude soft-delete
                .map(ApplicationMapper::toDto)
                .toList();
    }

    public ApplicationDTO getById(String applicationId){
        Application application = repository.findById(applicationId)
                .filter(app -> app.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        return ApplicationMapper.toDto(application);
    }

    public ApplicationDTO createApplication(ApplicationCreateRequest req){
        Application application = Application.builder()
                .applicationId(UUID.randomUUID().toString())
                .applicantId(req.applicantId())
                .jobPostId(req.jobPostId())
                .companyId(req.companyId())
                .status(ApplicationStatus.PENDING)
                .submissionDate(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isArchived(false)
                .build();

        //Set documents if provided
        if(req.documents() != null && !req.documents().isEmpty()){
            for(FileReference reference : req.documents()){
                FileReference doc = FileReference.builder()
                        .fileId(reference.getFileId() != null ? reference.getFileId() : UUID.randomUUID().toString())
                        .applicationId(application.getApplicationId())
                        .fileUrl(reference.getFileUrl())
                        .publicId(reference.getPublicId())
                        .fileType(reference.getFileType())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                application.getDocuments().add(doc);
            }
        }

        Application saved = repository.save(application);
        return ApplicationMapper.toDto(saved);
    }

    public ApplicationDTO updateStatus(String applicationId, ApplicationStatus newStatus){
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cant find application"));

        application.setStatus(newStatus);
        application.setUpdatedAt(Instant.now());

        Application saved = repository.save(application);
        return ApplicationMapper.toDto(saved);
    }

    //Soft delete (archive)
    public void archive(String applicationId){
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cant find application"));
        application.setIsArchived(true);
        application.setDeletedAt(Instant.now());
        repository.save(application);
    }

}
