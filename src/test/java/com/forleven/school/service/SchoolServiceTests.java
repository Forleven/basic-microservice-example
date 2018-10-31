package com.forleven.school.service;

import java.util.Collections;
import java.util.Optional;

import io.vavr.control.Either;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.forleven.common.event.CreationEvent;
import com.forleven.common.event.UpdatingEvent;
import com.forleven.common.exception.HttpException;
import com.forleven.common.exception.NotFoundException;
import com.forleven.school.form.SchoolForm;
import com.forleven.school.model.School;
import com.forleven.school.repository.SchoolRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchoolServiceTests {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private SchoolRepository schoolRepository;

    @Autowired
    @InjectMocks
    private SchoolService schoolService;

    @Test
    public void testGetSchools() {

        Page<School> expectedSchool = new PageImpl<>(Collections.singletonList(School.builder().build()));

        when(schoolRepository.findAll(any(Pageable.class)))
                .thenReturn(expectedSchool);

        Optional<Page<School>> schoolNullable = schoolService.getSchools(Pageable.unpaged());

        assertTrue(schoolNullable.isPresent());

        assertEquals(
                expectedSchool,
                schoolNullable.get()
        );

    }

    @Test
    public void testGetSchoolsNotFound() {

        when(schoolRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        Optional<Page<School>> schoolNullable = schoolService.getSchools(Pageable.unpaged());

        assertFalse(schoolNullable.isPresent());
    }

    @Test
    public void testGetSchool() {
        School expectedSchool = School.builder().build();

        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.of(expectedSchool));

        Optional<School> schoolNullable = schoolService.getSchool(1L);

        assertTrue(schoolNullable.isPresent());

        assertEquals(
                expectedSchool,
                schoolNullable.get()
        );
    }

    @Test
    public void testGetSchoolNotFound() {

        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.empty());

        Optional<School> schoolNullable = schoolService.getSchool(1L);

        assertFalse(schoolNullable.isPresent());

        assertEquals(
                Optional.empty(),
                schoolNullable
        );
    }

    @Test
    public void testSaveSchool() {

        when(schoolRepository.save(any(School.class)))
                .thenReturn(School.builder().build());

        SchoolForm schoolForm = SchoolForm.builder()
                .name("test school name")
                .build();

        Either<HttpException, School> school = schoolService.saveSchool(schoolForm);

        assertTrue(school.isRight());

        verify(schoolRepository, times(1)).save(any(School.class));
        verify(publisher, times(1)).publishEvent(ArgumentMatchers.<CreationEvent<School>>any());
    }

    @Test
    public void testUpdateSchool() {
        School expectedSchool = School.builder().build();

        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.of(expectedSchool));

        when(schoolRepository.save(any(School.class)))
                .then(invocation -> invocation.getArgument(0));

        School school = School.builder()
                .id(1L)
                .name("school name")
                .build();

        Either<HttpException, School> schoolOrError = schoolService.updateSchool(school);

        assertTrue(schoolOrError.isRight());

        assertEquals(
                "school name",
                schoolOrError.get().getName()
        );

        verify(schoolRepository, times(1)).save(any(School.class));
        verify(publisher, times(1)).publishEvent(ArgumentMatchers.<UpdatingEvent<School>>any());
    }

    @Test
    public void testUpdateSchoolAndSchoolNotFound() {
        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.empty());

        School school = School.builder()
                .id(1L)
                .name("school name")
                .build();

        Either<HttpException, School> schoolOrError = schoolService.updateSchool(school);

        assertTrue(schoolOrError.isLeft());

        assertEquals(
                new NotFoundException("school.not_found"),
                schoolOrError.getLeft()
        );

        verify(schoolRepository, times(0)).save(any(School.class));
        verify(publisher, times(0)).publishEvent(ArgumentMatchers.<UpdatingEvent<School>>any());
    }

    @Test
    public void testDeleteSchool() {
        School expectedSchool = School.builder().build();

        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.of(expectedSchool));

        Optional<HttpException> hasErrors = schoolService.deleteSchool(1L);

        assertFalse(hasErrors.isPresent());

        verify(schoolRepository, times(1)).save(eq(expectedSchool));
    }

    @Test
    public void testDeleteSchoolAndSchoolNotFound() {
        School expectedSchool = School.builder().build();

        when(schoolRepository.findOne(ArgumentMatchers.<Specification<School>>any()))
                .thenReturn(Optional.empty());

        Optional<HttpException> hasErrors = schoolService.deleteSchool(1L);

        assertTrue(hasErrors.isPresent());

        assertEquals(
                new NotFoundException("school.not_found"),
                hasErrors.get()
        );

        verify(schoolRepository, times(0)).save(eq(expectedSchool));
    }

}