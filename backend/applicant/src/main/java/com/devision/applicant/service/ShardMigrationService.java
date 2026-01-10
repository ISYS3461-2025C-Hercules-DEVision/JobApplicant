package com.devision.applicant.service;

import com.devision.applicant.model.Applicant;
import com.devision.applicant.model.MediaPortfolio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShardMigrationService {

    private final ShardRouter shardRouter;

    @Transactional
    public void migrateApplicant(Applicant applicant, String oldCountry, String newCountry){
        if(oldCountry != null && oldCountry.equalsIgnoreCase(newCountry)){
            log.info("Same shard — country unchanged for applicant {}", applicant.getApplicantId());
            return;
        }

        MongoTemplate oldTemplate = shardRouter.getTemplateForCountry(oldCountry != null ? oldCountry : "VN");
        MongoTemplate newTemplate = shardRouter.getTemplateForCountry(newCountry);

        if(oldTemplate == newTemplate){
            log.info("Same shard- no migration needed for applicant {}:", applicant.getApplicantId());
            return;
        }

        try{
            log.info("Migrating applicant {} from shard {} to shard {}", applicant.getApplicantId(), oldCountry, newCountry);
            //Save applicant to new shard
            newTemplate.save(applicant);

            //Migrate related collections
            migrateMediaPortfolios(applicant.getApplicantId(), oldTemplate, newTemplate);

            //Delete from old shard
            oldTemplate.remove(Query.query(Criteria.where("applicantId").is(applicant.getApplicantId())), Applicant.class);
            log.info("Migrated applicant {} from shard {} to shard {}", applicant.getApplicantId(), oldCountry, newCountry);
        } catch (Exception e){
            log.error("Failed to migrate applicant {} from shard {} to shard {}", applicant.getApplicantId(), oldCountry, newCountry);
            throw new RuntimeException("Applicant migration failed: ", e);
        }

    }

    private void migrateMediaPortfolios(String applicantId, MongoTemplate oldTemplate, MongoTemplate newTemplate){
        Query query = Query.query(Criteria.where("applicantId").is(applicantId));
        List<MediaPortfolio> mediaPortfolios = oldTemplate.find(query, MediaPortfolio.class, "media_portfolios");

        if(!mediaPortfolios.isEmpty()){
            log.info("Migrating {} media portfolios for applicant {}", mediaPortfolios.size(), applicantId);
            newTemplate.insert(mediaPortfolios, "media_portfolios");
            oldTemplate.remove(query, MediaPortfolio.class, "media_portfolios");
        }
    }
}
