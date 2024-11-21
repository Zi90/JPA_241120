package com.ezen.boot_JPA.repository;

import com.ezen.boot_JPA.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/* JpaRepository<entity name, id class> */
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    List<Comment> findByBno(long bno);
    Page<Comment> findByBno(long bno, Pageable pageable);
}
