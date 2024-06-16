package com.app.SmartReader.services;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.NoteDto;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.models.Note;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.BookRepository;
import com.app.SmartReader.repositories.NoteRepository;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.exceptions.CrudOperationException;
import com.app.SmartReader.utils.mappers.EntityToDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        notes.forEach(result -> results.add(EntityToDtoMapper.mapNoteToDto(result)));
        return results;
    }

    public NoteDto getNoteById(Integer id) {
        Note result = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        return EntityToDtoMapper.mapNoteToDto(result);
    }

    public NoteDto addNote(NoteDto noteDto) {
        User user = userRepository.findById(noteDto.getCreatedBy()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(noteDto.getBook()).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        Note note = Note.builder()
                .createdBy(user)
                .book(book)
                .title(noteDto.getTitle())
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
        User user = userRepository.findById(noteDto.getCreatedBy()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(noteDto.getBook()).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        note.setContent(noteDto.getContent());
        note.setPage(noteDto.getPage());
        note.setTitle(noteDto.getTitle());
        note.setComment(noteDto.getComment());
        note.setCreatedBy(user);
        note.setBook(book);

        noteRepository.save(note);
        return noteDto;
    }

    public NoteDto removeNote(Integer id) {
        Note result = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        noteRepository.deleteById(id);
        return EntityToDtoMapper.mapNoteToDto(result);
    }

    /** USER SPECIFIC CRUD BELOW **/

    public List<NoteDto> getUserNotesFromBook(Integer bookId, String username){
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Note> notes = user.getNotes();
            List<NoteDto> results = new ArrayList<>();
            notes.forEach(note -> {
                if (note.getBook().getId().equals(bookId)) {
                    results.add(EntityToDtoMapper.mapNoteToDto(note));
                }
            });
            return results;
        }
        else throw new CrudOperationException("User does not exist");
    }

    public NoteDto addNoteByUser(NoteDto noteDto,String username){
        Book book = bookRepository.findById(noteDto.getBook()).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Note note = Note.builder()
                    .createdBy(user)
                    .book(book)
                    .title(noteDto.getTitle())
                    .content(noteDto.getContent())
                    .page(noteDto.getPage())
                    .comment(noteDto.getComment())
                    .build();
            noteRepository.save(note);
            noteDto.setId(note.getId());

            Set<Note> userNotes = user.getNotes();
            userNotes.add(note);
            user.setNotes(userNotes);
            userRepository.save(user);

            Set<Note> bookNotes = book.getNotes();
            bookNotes.add(note);
            book.setNotes(bookNotes);
            bookRepository.save(book);

            return EntityToDtoMapper.mapNoteToDto(note);
        }
        else throw new CrudOperationException("User does not exist");
    }
    public NoteDto updateNoteByUser (Integer id, NoteDto noteDto, String username){
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Note note= noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        Book book = bookRepository.findById(noteDto.getBook()).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        if (note.getCreatedBy().getId().equals(user.getId())) {
            note.setContent(noteDto.getContent());
            note.setPage(noteDto.getPage());
            note.setTitle(noteDto.getTitle());
            note.setComment(noteDto.getComment());
            note.setCreatedBy(user);
            note.setBook(book);
            return EntityToDtoMapper.mapNoteToDto(note);
        }
        else throw  new CrudOperationException("User can't edit an unowned note");
    }

    public NoteDto removeNoteByUser (Integer id, String username){
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Note result = noteRepository.findById(id).orElseThrow(() -> new CrudOperationException("Note does not exist"));
        if (result.getCreatedBy().getId().equals(user.getId())){
            noteRepository.deleteById(id);
            return EntityToDtoMapper.mapNoteToDto(result);
        }
        else throw new CrudOperationException("User can't delete an unowned note");

    }


}