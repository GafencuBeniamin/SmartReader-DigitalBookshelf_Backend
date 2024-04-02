package com.app.SmartReader.models;

import com.app.SmartReader.utils.enums.BookState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private BookState state;
    private Set<String> author;
    private String title;
    private Integer noOfPages;
    private String image;
    private String language;
    @JsonIgnore
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Note> notes;
    private String genre;
    private Boolean isPublic;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
    private String editure;
}
