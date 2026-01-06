package com.devision.applicant.service;

import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.api.SearchProfileMapper;
import com.devision.applicant.dto.SearchProfileCreateRequest;
import com.devision.applicant.dto.SearchProfileDTO;
import com.devision.applicant.dto.SearchProfileUpdateRequest;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.SearchProfile;
import com.devision.applicant.repository.SearchProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchProfileServiceImpl implements SearchProfileService{
    private final SearchProfileRepository repository;

    @Override
    public List<SearchProfileDTO> getByApplicantId(String applicantId){
        return SearchProfileMapper.toDtoList(repository.findByApplicantId(applicantId));
    }

    @Override
    public SearchProfileDTO create(SearchProfileCreateRequest request){

        SearchProfile saved = repository.save(SearchProfileMapper.toEntity(request));
        return SearchProfileMapper.toDTO(saved);
    }

    @Override
    public SearchProfileDTO update(String id, SearchProfileUpdateRequest request){
        SearchProfile s = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        SearchProfileMapper.updateEntity(s, request);
        s.setUpdatedAt(Instant.now());

        SearchProfile updated = repository.save(s);
        return SearchProfileMapper.toDTO(updated);
    }

    @Override
    public void delete(String id){
        repository.deleteById(id);
    }
}
