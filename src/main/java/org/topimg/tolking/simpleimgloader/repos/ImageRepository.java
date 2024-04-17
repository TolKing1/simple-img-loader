package org.topimg.tolking.simpleimgloader.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.topimg.tolking.simpleimgloader.entities.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> searchImagesByDescriptionContainingIgnoreCase(String description);

    @Query(value = "INSERT INTO image(description, name) values(:desc,:name) returning id", nativeQuery = true)
    long insertImage(@Param("desc") String desc, @Param("name") String name);
}
