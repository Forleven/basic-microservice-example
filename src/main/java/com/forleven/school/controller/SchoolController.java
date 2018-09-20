package com.forleven.school.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import io.swagger.annotations.*;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.forleven.common.exception.NotFoundException;
import com.forleven.common.fn.Lambda;
import com.forleven.common.validation.FormErrors;
import com.forleven.common.web.ResourceErrors;
import com.forleven.common.web.Resources;
import com.forleven.common.web.ResponseError;
import com.forleven.school.form.SchoolForm;
import com.forleven.school.model.School;
import com.forleven.school.service.SchoolService;

import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping("/school")
@Api(value = "school", tags = "School", description = "Operations pertaining to School")
public class SchoolController {

    @Autowired
    private FormErrors formErrors;

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    @ApiOperation(value = "List School")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Obtain Schools"),
            @ApiResponse(code = 404, message = "Schools Not Founded", response = ResponseError.class),
    })
    public ResponseEntity<Resources<School>> getSchools(
            @ApiIgnore Pageable pageable) {

        log.info("GET request to return all schools");

        return schoolService.getSchools(pageable)
                .map(Lambda::toResponse)
                .orElseThrow(() -> new NotFoundException("school.not_founded"));
    }

    @HystrixCommand(fallbackMethod = "fallbackGetSchool")
    @GetMapping("/{schoolId}")
    @ApiOperation(value = "View a School", response = School.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Obtain School"),
            @ApiResponse(code = 404, message = "School Not Founded", response = ResponseError.class),
    })
    public ResponseEntity<School> getSchool(
            @ApiParam(value = "ID of School to obtain School", required = true, example = "1")
            @PathVariable Long schoolId) {

        log.info("GET request to return a School");

        return schoolService.getSchool(schoolId)
                .map(Lambda::toResponse)
                .orElseThrow(() -> new NotFoundException("school.not_founded"));
    }

    @PostMapping
    @ApiOperation(value = "Save a new School")
    @ApiResponses({
            @ApiResponse(code = 202, message = "School accept to verification", response = Object.class),
            @ApiResponse(code = 409, message = "Conflict occur in save School method", response = ResponseError.class),
            @ApiResponse(code = 400, message = "Errors on School object", response = ResourceErrors.class)
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity saveSchool(
            @ApiParam(name = "body", value = "School object that needs to be added to the sample", required = true)
            @Valid @RequestBody SchoolForm schoolForm,
            BindingResult bindingResult) {

        log.info("POST request to save a new School");

        if (bindingResult.hasErrors()) {
            log.error("Error in binding results");
            return formErrors.validationsToResponse(bindingResult);
        }

        return schoolService.saveSchool(schoolForm).fold(
                Lambda::errorToResponse,
                success -> ResponseEntity.accepted().build()
        );
    }

    @PutMapping("/{schoolId}")
    @ApiOperation(value = "Update a School")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Update of School accepted to new verification", response = Object.class),
            @ApiResponse(code = 404, message = "School Not Founded", response = ResponseError.class),
            @ApiResponse(code = 400, message = "Error on School object", response = ResourceErrors.class)
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity updateSchool(
            @ApiParam(value = "ID School to update", required = true, example = "1")
            @PathVariable Long schoolId,
            @ApiParam(name = "body", value = "School object that needs to be update to the sample", required = true)
            @RequestBody @Valid SchoolForm schoolForm,
            BindingResult bindingResult) {

        log.info("PUT request to update a School");

        if (bindingResult.hasErrors()) {
            log.error("Error in binding results");
            return formErrors.validationsToResponse(bindingResult);
        }

        School school = School.builder()
                .id(schoolId)
                .name(schoolForm.getName())
                .build();

        return schoolService.updateSchool(school).fold(
                Lambda::errorToResponse,
                success -> ResponseEntity.accepted().build()
        );
    }

    @DeleteMapping("/{schoolId}")
    @ApiOperation(value = "Delete a School")
    @ApiResponses({
            @ApiResponse(code = 202, message = "School deleted", response = Object.class),
            @ApiResponse(code = 404, message = "School not founded", response = ResourceErrors.class)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteSchool(
            @ApiParam(value = "ID of School to delete a School", required = true, example = "1")
            @PathVariable Long schoolId) {

        log.info("DELETE request to delete a School");

        return schoolService.deleteSchool(schoolId)
                .map(Lambda::errorToResponse)
                .orElseGet(Lambda.TO_ACCEPTED);
    }

    // FALLBACK METHODS

    public ResponseEntity fallbackGetSchool(Long schoolId) {

        // publish in spring events to migrate this school in legacy to this service
        // or
        // call a service to get school any place and save in this service

        // after do process retry
        return getSchool(schoolId);
    }
}
