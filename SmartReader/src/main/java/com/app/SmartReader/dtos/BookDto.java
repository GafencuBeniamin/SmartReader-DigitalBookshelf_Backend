package com.app.SmartReader.dtos;

import com.app.SmartReader.utils.enums.BookState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookDto {
    private Integer id;
    private BookState state;
    private Set<String> author;
    private String title;
    private Integer noOfPages;
    private String language;
    private String image;
    private String genre;
    private Boolean isPublic;
    private UserDto createdBy;
    private String editure;
}
