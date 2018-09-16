package com.forleven.school.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import io.vavr.control.Either;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.forleven.common.event.CreationEvent;
import com.forleven.common.event.UpdatingEvent;
import com.forleven.common.exception.HttpException;
import com.forleven.common.exception.NotFoundException;
import com.forleven.common.fn.Lambda;
import com.forleven.common.specification.GeneralSpecification;
import com.forleven.school.form.SchoolForm;
import com.forleven.school.model.School;
import com.forleven.school.repository.SchoolRepository;
import com.forleven.school.specification.SchoolSpecification;

import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
public class SchoolService {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private SchoolRepository schoolRepository;

    public Optional<Page<School>> getSchools(Pageable pageable) {
        return Lambda.toOptionalSpec(schoolRepository.findAll(pageable));
    }

    public Optional<Page<School>> getSchools(Specification<School> spec,
                                             Pageable pageable) {

        return Lambda.toOptionalSpec(schoolRepository.findAll(spec, pageable));
    }

    public Optional<School> getSchool(Long schoolId) {

        Specification<School> spec = where(SchoolSpecification.withSchoolId(schoolId))
                .and(GeneralSpecification.hasStatusActive());

        return getSchool(spec);
    }

    public Optional<School> getSchool(Specification<School> spec) {
        return schoolRepository.findOne(spec);
    }

    public Either<HttpException, School> saveSchool(SchoolForm schoolForm) {

        // business logic, eventual return Either.left(a http exception e.g new ConflictException)

        School schoolToSave = School.builder()
                .name(schoolForm.getName())
                .build();

        School schoolSaved = schoolRepository.save(schoolToSave);

        publisher.publishEvent(new CreationEvent<>(schoolSaved));

        return Either.right(schoolSaved);
    }

    public Either<HttpException, School> updateSchool(School school) {
        Optional<School> schoolNullable = getSchool(school.getId());

        if (!schoolNullable.isPresent()) {
            // here use a i18n code (in messages folder)
            return Either.left(new NotFoundException("school.not_found"));
        }

        School schoolToUpdate = schoolNullable.get()
                .withName(school.getName());

        School schoolUpdated = schoolRepository.save(schoolToUpdate);

        publisher.publishEvent(new UpdatingEvent<>(schoolUpdated));

        return Either.right(schoolUpdated);
    }

    public Optional<HttpException> deleteSchool(Long schoolId) {
        Optional<School> schoolNullable = getSchool(schoolId);

        if (!schoolNullable.isPresent()) {
            return Optional.of(new NotFoundException("school.not_found"));
        }

        schoolNullable.ifPresent(schoolToDelete -> {
            schoolToDelete.setStatus(false);
            schoolRepository.save(schoolToDelete);
        });

        return Optional.empty();
    }
}
