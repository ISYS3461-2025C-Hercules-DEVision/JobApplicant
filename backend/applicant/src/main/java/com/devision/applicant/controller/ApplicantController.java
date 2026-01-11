package com.devision.applicant.controller;

import com.devision.applicant.api.ApplicantMapper;
import com.devision.applicant.config.KafkaConstant;
import com.devision.applicant.connection.ApplicantToJmEvent;
import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applicants")
public class ApplicantController {
    private final ApplicantService service;
    private final KafkaGenericProducer<ApplicantToJmEvent> genericProducer;
    public ApplicantController(ApplicantService service, KafkaGenericProducer<ApplicantToJmEvent> genericProducer) {
        this.service = service;
        this.genericProducer = genericProducer;
    }

    //WORKED
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicantDTO create(@Valid @RequestBody ApplicantCreateRequest request) {
        return service.create(request);
    }

    //WORKED
    @GetMapping("/{id}")
    public ApplicantDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ApplicantForAdmin> getAll() {
        List<ApplicantDTO> a = service.getAll();
        return ApplicantMapper.toApplicantForAdmin(a);
    }

    //WORKED
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

    //WORKED
    //Profile Image
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicantDTO uploadAvatar(@PathVariable String id, @RequestParam("file")MultipartFile file){
        UploadAvatarRequest request = new UploadAvatarRequest(file);
        return service.uploadProfileImage(id, request);
    }

    //WORKED
    //Media Portfolio
    @PostMapping(value = "/{resumeId}/portfolio", consumes = "multipart/form-data")
    public MediaPortfolio uploadPortfolio(@PathVariable String resumeId,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "title", required = false) String title,
                                                          @RequestParam(value = "description", required = false) String description,
                                                          @RequestParam(value = "visibility", defaultValue = "PRIVATE") Visibility visibility){
        var request = new UploadMediaPortfolioRequest(file, title, description, visibility);

        return service.uploadMediaPortfolio(resumeId, request);

    }


    @GetMapping("/{resumeId}/portfolio")
    public List<MediaPortfolio> getPortfolio(@PathVariable String resumeId,
                                             @RequestParam(value = "visibility", required = false) Visibility visibility){

        return service.getMediaPortfolio(resumeId, visibility);
    }

    @DeleteMapping("/{resumeId}/portfolio/{mediaId}")
    public void deleteMediaPortfolio(@PathVariable String resumeId, @PathVariable String mediaId){
        service.deleteMediaPortfolio(resumeId, mediaId);
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApplicantDTO> deactivateApplicant(@PathVariable("id") String id) {
        ApplicantDTO result = service.deactivateApplicantAccount(id);
        return ResponseEntity.ok(result);
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApplicantDTO> activateApplicant(@PathVariable("id") String id) {
        ApplicantDTO result = service.activateApplicantAccount(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicantId}/resumes")
    public ResumeDTO updateResume(@PathVariable String applicantId, @RequestBody ResumeUpdateRequest request){

        return service.updateResume(applicantId, request);
    }

    @GetMapping("/{applicantId}/resumes")
    public ResumeDTO getResume(@PathVariable String applicantId){
        return service.getResume(applicantId);
    }

    @DeleteMapping("/{applicantId}/resumes")
    public void deleteResume(@PathVariable String applicantId){
        service.deleteResume(applicantId);
    }

    @GetMapping("/resumes")
    public List<ResumeDTO> getAllResumes(){
        return service.getAllResumes();
    }
}
