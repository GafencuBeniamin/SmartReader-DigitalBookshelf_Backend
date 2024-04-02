package com.app.SmartReader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NoteDto {
    private Integer id;
    private BookDto book;
    private UserDto createdBy;
    private String content;
    private Integer page;
    private String comment;
}
