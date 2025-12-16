package com.devision.applicant.service;

import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.ApplicantUpdateRequest;
import com.devision.applicant.entity.Applicant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository repository;

    public ApplicantServiceImpl(ApplicantRepository repository) {
        this.repository = repository;
    }

    @Override
    public ApplicantDTO create(ApplicantCreateRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        Applicant saved = repository.save(ApplicantMapper.toEntity(req));
        return ApplicantMapper.toDto(saved);
    }

    @Override
    public ApplicantDTO getById(String id) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        return ApplicantMapper.toDto(a);
    }

    @Override
    public List<ApplicantDTO> getAll() {
        return repository.findByDeletedAtIsNull()
                .stream()
                .map(ApplicantMapper::toDto)
                .toList();
    }

    @Override
    public ApplicantDTO update(String id, ApplicantUpdateRequest req) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        ApplicantMapper.updateEntity(a, req);
        return ApplicantMapper.toDto(repository.save(a));
    }

    @Override
    public void delete(String id) {
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        a.setDeletedAt(LocalDateTime.now());
        repository.save(a);
    }
}
