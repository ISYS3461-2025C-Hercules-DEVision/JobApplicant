package com.devision.applicant.controller;

import com.devision.applicant.dto.*;
import com.devision.applicant.enums.Visibility;
import com.devision.applicant.model.MediaPortfolio;
import com.devision.applicant.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applicants")
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

    //WORKED
    @GetMapping("/{id}")
    public ApplicantDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ApplicantDTO> getAll() {
        return service.getAll();
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

    @DeleteMapping("/{id}/field/{fieldName}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicantDTO deleteProfileByField(@PathVariable String id, @PathVariable String fieldName){
        return service.deleteProfileByField(id, fieldName);
    }

    //Profile Image
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicantDTO uploadAvatar(@PathVariable String id, @RequestParam("file")MultipartFile file){
        UploadAvatarRequest request = new UploadAvatarRequest(file);
        return service.uploadProfileImage(id, request);
    }

    //Media Portfolio
    @PostMapping(value = "/{id}/portfolio", consumes = "multipart/form-data")
    public MediaPortfolio uploadPortfolio(@PathVariable String id,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "title", required = false) String title,
                                                          @RequestParam(value = "description", required = false) String description,
                                                          @RequestParam(value = "visibility", defaultValue = "PRIVATE") Visibility visibility){
        var request = new UploadMediaPortfolioRequest(file, title, description, visibility);

        return service.uploadMediaPortfolio(id, request);

    }

    //WORKED
    @GetMapping("/{id}/portfolio")
    public List<MediaPortfolio> getPortfolio(@PathVariable String id,
                                             @RequestParam(value = "visibility", required = false) Visibility visibility){

        return service.getMediaPortfolio(id, visibility);
    }

    @DeleteMapping("/{id}/portfolio/{mediaId}")
    public void deleteMediaPortfolio(@PathVariable String id, @PathVariable String mediaId){
        service.deleteMediaPortfolio(id, mediaId);
    }


}
