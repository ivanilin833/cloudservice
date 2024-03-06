package com.netology.diplombackend.repository;

import com.netology.diplombackend.domain.model.StorageFile;
import com.netology.diplombackend.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<StorageFile, Long> {

    void deleteByUserAndFilename(User user, String filename);

    StorageFile findByUserAndFilename(User user, String filename);

    @Modifying
    @Query("update StorageFile f set f.filename = :newName where f.filename = :filename and f.user = :user")
    void editFileNameByUser(@Param("user") User user, @Param("filename") String filename, @Param("newName") String newName);

    List<StorageFile> findAllByUser(User user);
}
