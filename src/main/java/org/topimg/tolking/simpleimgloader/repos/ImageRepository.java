package org.topimg.tolking.simpleimgloader.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.topimg.tolking.simpleimgloader.entities.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(value =
            "select * from Image " +
                    "where to_tsvector(description) @@ plainto_tsquery(:text) " +
                    "   OR description like concat('%', :text, '%')"
            , nativeQuery = true)
    List<Image> searchFullText(@Param("text") String description);

    @Transactional
    void deleteImageByNameEqualsIgnoreCase(String name);
}
