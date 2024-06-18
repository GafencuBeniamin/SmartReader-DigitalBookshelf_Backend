package com.app.SmartReader.utils.mappers;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.NoteDto;
import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.models.Note;
import com.app.SmartReader.models.User;

import java.util.Set;
import java.util.stream.Collectors;

public class EntityToDtoMapper {

    public static UserDto mapUserToDto(User user) {
        Set<NoteDto> noteDtos = user.getNotes().stream()
                .map(EntityToDtoMapper::mapNoteToDto)
                .collect(Collectors.toSet());

        Set<BookDto> bookDtos = user.getBooks().stream()
                .map(EntityToDtoMapper::mapBookToDto)
                .collect(Collectors.toSet());

        Set<UserDto> friendDtos = user.getFriends().stream()
                .map(EntityToDtoMapper::mapUserToDto)
                .collect(Collectors.toSet());


        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .picture(user.getPicture())
                .friends(friendDtos)
                .notes(noteDtos)
                .books(bookDtos)
                .build();
    }

    public static BookDto mapBookToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .genre(book.getGenre())
                .author(book.getAuthor())
                .createdBy(book.getCreatedBy().getId())
                .isPublic(book.getIsPublic())
                .language(book.getLanguage())
                .noOfPages(book.getNoOfPages())
                .image(book.getImage())
                .editure(book.getEditure())
                .bookStates(book.getBookStates())
                .build();
    }

    public static NoteDto mapNoteToDto(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .book(note.getBook().getId())
                .title(note.getTitle())
                .createdBy(note.getCreatedBy().getId())
                .content(note.getContent())
                .page(note.getPage())
                .comment(note.getComment())
                .build();
    }
}
