package com.devision.applicant.controller;

import com.devision.applicant.dto.ApplicantCreateRequest;
import com.devision.applicant.dto.ApplicantDTO;
import com.devision.applicant.dto.ApplicantUpdateRequest;
import com.devision.applicant.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applicants")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicantController {
    private final ApplicantService service;

    public ApplicantController(ApplicantService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicantDTO create(@Valid @RequestBody ApplicantCreateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApplicantDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ApplicantDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ApplicantDTO update(@PathVariable String id,
                               @Valid @RequestBody ApplicantUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
