package com.app.SmartReader.controllers;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.NoteDto;
import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.services.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable Integer id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @PostMapping
    public ResponseEntity<NoteDto> addNote(@RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(noteService.addNote(noteDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(@PathVariable Integer id, @RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(noteService.updateNote(id, noteDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NoteDto> removeNote(@PathVariable Integer id) {
        return ResponseEntity.ok(noteService.removeNote(id));
    }

    public UserDto getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto){
            return (UserDto) authentication.getPrincipal();
        }
        return null;
    }
    @GetMapping("/myNotesFromBook/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<List<NoteDto>> getUserBooks(@PathVariable Integer bookId) {
        return ResponseEntity.ok(noteService.getUserNotesFromBook(bookId, getLoggedInUserDetails().getUsername()));
    }

    @PostMapping("/createNewNote")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<NoteDto> addNoteByUser(@RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(noteService.addNoteByUser(noteDto,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/updateNoteByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<NoteDto> updateNoteByUser(@PathVariable Integer id, @RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(noteService.updateNoteByUser(id,noteDto,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/removeNoteByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<NoteDto> removeNoteByUser(@PathVariable Integer id) {
        return ResponseEntity.ok(noteService.removeNoteByUser(id,getLoggedInUserDetails().getUsername()));
    }
}