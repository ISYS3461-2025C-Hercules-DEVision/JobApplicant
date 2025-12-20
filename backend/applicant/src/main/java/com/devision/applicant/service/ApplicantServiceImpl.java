package com.devision.applicant.service;

import com.cloudinary.Cloudinary;
import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.repository.ApplicantRepository;
import com.devision.applicant.repository.MediaPortfolioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository repository;
    private final MediaPortfolioRepository mediaPortfolioRepository;
    private final ImageService imageService;

    public ApplicantServiceImpl(ApplicantRepository repository, MediaPortfolioRepository mediaPortfolioRepository, ImageService mediaService) {
        this.repository = repository;
        this.mediaPortfolioRepository = mediaPortfolioRepository;
        this.imageService = mediaService;
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

    @Override
    public ApplicantDTO uploadProfileImage(String id, UploadAvatarRequest request){
        Applicant a = repository.findById(id)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        try{
            String avatarUrl = imageService.uploadProfileImage(request.file(), id);
            a.setProfileImageUrl(avatarUrl);
            Applicant saved = repository.save(a);
            return ApplicantMapper.toDto(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload avatar");
        }
    }

    @Override
    public MediaPortfolio uploadMediaPortfolio(String applicantId, UploadMediaPortfolioRequest request){
         repository.findById(applicantId)
                 .filter(x -> x.getDeletedAt() == null)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        try{
            return imageService.uploadMediaPortfolio(
                    request.file(),
                    applicantId,
                    request.title(),
                    request.description(),
                    request.visibility() != null ? request.visibility() : Visibility.PRIVATE
            );

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload portfolio");
        }
    }

    @Override
    public List<MediaPortfolio> getMediaPortfolio(String applicantId, Visibility visibility) {
        repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

            if (visibility == null) {
                return mediaPortfolioRepository.findByApplicantId(applicantId);
            }
            return mediaPortfolioRepository.findByApplicantIdAndVisibility(applicantId, visibility);
    }

    @Override
    public void deleteMediaPortfolio(String applicantId, String mediaId){
        repository.findById(applicantId)
                .filter(x -> x.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        MediaPortfolio mediaPortfolio = mediaPortfolioRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));

        if(!mediaPortfolio.getApplicantId().equals(applicantId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own portfolio");
        }

        try{
            imageService.deleteMedia(mediaId, applicantId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete media from storage");
        }
    }


}
