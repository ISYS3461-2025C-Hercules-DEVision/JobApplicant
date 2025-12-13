package com.devision.applicant.repository;

import com.devision.applicant.model.ApplicantSkill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicantSkillRepository extends MongoRepository<ApplicantSkill, String> {

    List<ApplicantSkill> findByApplicantId(String applicantId);

    void deleteByApplicantId(String applicantId);

    boolean existsByApplicantIdAndSkillId(String applicantId, String skillId);
}
