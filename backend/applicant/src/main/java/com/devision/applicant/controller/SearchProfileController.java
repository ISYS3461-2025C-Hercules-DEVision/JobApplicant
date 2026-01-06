package com.devision.applicant.controller;

import com.devision.applicant.dto.SearchProfileCreateRequest;
import com.devision.applicant.dto.SearchProfileDTO;
import com.devision.applicant.dto.SearchProfileUpdateRequest;
import com.devision.applicant.service.SearchProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search-profiles")
@RequiredArgsConstructor
public class SearchProfileController {

    private final SearchProfileService service;

    @GetMapping("/applicant/{applicantId}")
    public List<SearchProfileDTO> getByApplicant(@PathVariable String applicantId){
        return service.getByApplicantId(applicantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SearchProfileDTO create(@Valid @RequestBody SearchProfileCreateRequest request){
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SearchProfileDTO update(@PathVariable String id, @Valid @RequestBody SearchProfileUpdateRequest request){
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id){
        service.delete(id);
    }

}
