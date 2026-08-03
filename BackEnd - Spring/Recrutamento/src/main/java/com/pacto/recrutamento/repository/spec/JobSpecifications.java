package com.pacto.recrutamento.repository.spec;

import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.enuns.JobStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobSpecifications {

    public static Specification<Job> textContains(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) return cb.conjunction();
            String like = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("requirements")), like)
            );
        };
    }

    public static Specification<Job> hasStatus(JobStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

}