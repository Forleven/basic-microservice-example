package com.forleven.school.specification;

import org.springframework.data.jpa.domain.Specification;

import com.forleven.school.model.School;
import com.forleven.school.model.School_;

public class SchoolSpecification {

    private SchoolSpecification() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<School> withSchoolId(Long schoolId) {
        return (root, query, builder) ->
                builder.equal(root.get(School_.id), schoolId);
    }
}
