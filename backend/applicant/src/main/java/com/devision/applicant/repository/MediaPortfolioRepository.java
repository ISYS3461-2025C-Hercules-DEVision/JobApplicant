package com.devision.applicant.repository;

import com.devision.applicant.model.MediaPortfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.devision.applicant.enums.Visibility;

import java.util.List;
import java.util.Optional;

public interface MediaPortfolioRepository extends MongoRepository<MediaPortfolio, String> {

    List<MediaPortfolio> findByApplicantId(String applicantId);

    List<MediaPortfolio> findByApplicantIdAndVisibility(String applicantId, Visibility visibility);

    Optional<MediaPortfolio> findByPublicId(String publicId);

    void deleteByApplicantId(String applicantId);
}
