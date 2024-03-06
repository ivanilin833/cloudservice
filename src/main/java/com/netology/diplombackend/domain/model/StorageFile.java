package com.netology.diplombackend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
@Builder
public class StorageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Long size;

    @Lob
    @Column(name = "file_content", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileContent;

    @ManyToOne
    private User user;
}
