package com.app.SmartReader.controllers;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.models.User;
import com.app.SmartReader.services.BookService;
import com.app.SmartReader.utils.enums.BookState;
import com.app.SmartReader.utils.enums.BookStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService=bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> addBook(@RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.addBook(bookDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Integer id, @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDto> removeBook(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.removeBook(id));
    }

    public UserDto getLoggedInUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto){
            return (UserDto) authentication.getPrincipal();
        }
        return null;
    }
    @GetMapping("/myBooks")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<List<BookDto>> getUserBooks() {
        return ResponseEntity.ok(bookService.getUserBooks(getLoggedInUserDetails().getUsername()));
    }
    @GetMapping("/getBookByIdByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> getBookByIdByUser(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookByIdByUser(id, getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/addNewBookToUserLibrary/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> addNewBookToUserLibrary(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.addExistingBookToUserLibrary(id,getLoggedInUserDetails().getUsername()));
    }
    @PostMapping("/createNewBook")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> createNewBookByUser(@RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.createNewBookByUser(bookDto,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/updateBookByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> updateBookByUser(@PathVariable Integer id, @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBookByUser(id,bookDto,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/updatePublicBookByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> updatePublicBookByUser(@PathVariable Integer id, @RequestBody BookState bookState) {
        return ResponseEntity.ok(bookService.updatePublicBookByUser(id,bookState,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/removeBookFromUserLibrary/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> removeFromUserLibrary(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.removeBookFromUserLibrary(id,getLoggedInUserDetails().getUsername()));
    }
    @DeleteMapping("/removeBookByUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> removeBookByUser(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.removeBookByUser(id,getLoggedInUserDetails().getUsername()));
    }
    @PutMapping("/changeStatus/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<BookDto> changeBookStatus(@PathVariable Integer id, @RequestBody BookStatus status) {
        return ResponseEntity.ok(bookService.changeBookStatus(id,getLoggedInUserDetails().getUsername(),status));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public ResponseEntity<List<BookDto>> getPendingBooks() {
        return ResponseEntity.ok(bookService.getAllPendingBooks(getLoggedInUserDetails().getUsername()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('MODERATOR')")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestBody String keyword) {
        return ResponseEntity.ok(bookService.searchBooksByTitleOrAuthor(keyword.trim(), getLoggedInUserDetails().getUsername()));
    }
}
