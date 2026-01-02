package com.wa.repository;

import com.wa.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.matriculations ORDER BY p.fullname")
    List<Person> findAllWithRelations();

    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.matriculations WHERE p.id = :id")
    Optional<Person> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN p.address " +
            "LEFT JOIN p.matriculations " +
            "WHERE (:search IS NULL OR :search = '' OR LOWER(p.fullname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY p.fullname")
    Page<Person> findAllWithRelationsPaginated(@Param("search") String search, Pageable pageable);
}
