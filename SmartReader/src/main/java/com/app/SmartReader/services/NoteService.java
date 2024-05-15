package com.app.SmartReader.services;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.NoteDto;
import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.models.Note;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.BookRepository;
import com.app.SmartReader.repositories.NoteRepository;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.exceptions.CrudOperationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class NoteService {

    @Autowired
    private final NoteRepository noteRepository;
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<NoteDto> getAllNotes() {
        Iterable<Note> notes = noteRepository.findAll();
        List<NoteDto> results = new ArrayList<>();

        notes.forEach(result -> results.add(mapNoteToDto(result)));
        return results;
    }

    public NoteDto getNoteById(Integer id) {
        Note result = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        return mapNoteToDto(result);
    }

    public NoteDto addNote(NoteDto noteDto) {
        User user = userRepository.findById(noteDto.getCreatedBy().getId()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(noteDto.getBook().getId()).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        Note note = Note.builder()
                .createdBy(user)
                .book(book)
                .content(noteDto.getContent())
                .page(noteDto.getPage())
                .comment(noteDto.getComment())
                .build();

        noteRepository.save(note);
        noteDto.setId(note.getId());
        return noteDto;
    }

    public NoteDto updateNote(Integer id, NoteDto noteDto) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        User user = userRepository.findById(noteDto.getCreatedBy().getId()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(noteDto.getBook().getId()).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        note.setContent(noteDto.getContent());
        note.setPage(noteDto.getPage());
        note.setComment(noteDto.getComment());
        note.setCreatedBy(user);
        note.setBook(book);

        noteRepository.save(note);
        return noteDto;
    }

    public NoteDto removeNote(Integer id) {
        Note result = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        noteRepository.deleteById(id);
        return mapNoteToDto(result);
    }

    private NoteDto mapNoteToDto(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .content(note.getContent())
                .page(note.getPage())
                .comment(note.getComment())
                .createdBy(UserDto.builder()
                        .id(note.getCreatedBy().getId())
                        .role(note.getCreatedBy().getRole())
                        .email(note.getCreatedBy().getEmail())
                        .picture(note.getCreatedBy().getPicture())
                        .username(note.getCreatedBy().getUsername())
                        .friends(note.getCreatedBy().getFriends())
                        .build())
                .book(BookDto.builder()
                        .id(note.getBook().getId())
                        .author(note.getBook().getAuthor())
                        .createdBy(UserDto.builder()
                                .id(note.getBook().getCreatedBy().getId())
                                .role(note.getBook().getCreatedBy().getRole())
                                .email(note.getBook().getCreatedBy().getEmail())
                                .picture(note.getBook().getCreatedBy().getPicture())
                                .username(note.getBook().getCreatedBy().getUsername())
                                .friends(note.getCreatedBy().getFriends())
                                .build())
                        .editure(note.getBook().getEditure())
                        .genre(note.getBook().getGenre())
                        .image(note.getBook().getImage())
                        .isPublic(note.getBook().getIsPublic())
                        .language(note.getBook().getLanguage())
                        .noOfPages(note.getBook().getNoOfPages())
                        .state(note.getBook().getState())
                        .title(note.getBook().getTitle())
                        .build())
                .build();
    }
}