package com.app.SmartReader.dtos;

import com.app.SmartReader.models.Book;
import com.app.SmartReader.models.Note;
import com.app.SmartReader.models.User;
import com.app.SmartReader.utils.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto{
    private Integer id;
    private String username;
    private String email;   
    private UserRole role;
    private String picture;
    private Set<UserDto> friends;
    private Set<NoteDto> notes;
    private Set<BookDto> books;
    private String token;
}
