package com.expense_sharing_backend.repository;

import com.expense_sharing_backend.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Eagerly fetch members when needed to avoid LazyInitializationException
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.members WHERE g.id = :id")
    Optional<Group> findByIdWithMembers(@Param("id") Long id);
}

