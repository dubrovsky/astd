package com.isc.astd.repository;

import com.isc.astd.domain.FileReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileReviewRepository  extends JpaRepository<FileReview, Long> {
}
