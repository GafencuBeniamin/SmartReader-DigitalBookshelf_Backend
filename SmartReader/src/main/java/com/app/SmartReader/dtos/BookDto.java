package com.app.SmartReader.dtos;

import com.app.SmartReader.utils.enums.BookState;
import com.app.SmartReader.utils.enums.BookStatus;
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
    private BookStatus isPublic;
    private Integer createdBy;
    private String editure;
}
