package com.app.SmartReader.services;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.dtos.UserDto;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.BookRepository;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.exceptions.CrudOperationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class BookService {
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<BookDto> getAllBooks(){
        Iterable<Book> books =bookRepository.findAll();
        List<BookDto> results = new ArrayList<>();

        books.forEach(result -> results.add(mapBookToDto(result)));
        return results;
    }

    public BookDto getBookById(Integer id){
        Book result=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        return mapBookToDto(result);
    }

    public BookDto updateBook(Integer id, BookDto bookDto){

        Book book=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        User user = userRepository.findById(bookDto.getCreatedBy().getId()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        book.setId(bookDto.getId());
        book.setImage(bookDto.getImage());
        book.setGenre(bookDto.getGenre());
        book.setEditure(bookDto.getEditure());
        book.setAuthor(bookDto.getAuthor());
        book.setLanguage(bookDto.getLanguage());
        book.setState(bookDto.getState());
        book.setTitle(book.getTitle());
        book.setCreatedBy(user);
        book.setIsPublic(bookDto.getIsPublic());
        book.setNoOfPages(bookDto.getNoOfPages());
        return bookDto;
    }

    public BookDto addBook(BookDto bookDto){
        User user = userRepository.findById(bookDto.getCreatedBy().getId()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = Book.builder()
                .id(bookDto.getId())
                .author(bookDto.getAuthor())
                .createdBy(user)
                .editure(bookDto.getEditure())
                .genre(bookDto.getGenre())
                .image(bookDto.getImage())
                .title(bookDto.getTitle())
                .isPublic(bookDto.getIsPublic())
                .language(bookDto.getLanguage())
                .noOfPages(bookDto.getNoOfPages())
                .state(bookDto.getState())
                .build();
        bookRepository.save(book);
        bookDto.setId(book.getId());
        return bookDto;
    }

    public BookDto removeBook(Integer id){
        Book result = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        bookRepository.deleteById(id);
        return mapBookToDto(result);
    }

    private BookDto mapBookToDto(Book result) {
        return BookDto.builder()
                .id(result.getId())
                .state(result.getState())
                .title(result.getTitle())
                .genre(result.getGenre())
                .author(result.getAuthor())
                .createdBy(UserDto.builder()
                        .id(result.getCreatedBy().getId())
                        .role(result.getCreatedBy().getRole())
                        .email(result.getCreatedBy().getEmail())
                        .profile(result.getCreatedBy().getProfile())
                        .username(result.getCreatedBy().getUsername())
                        .password(result.getCreatedBy().getPassword())
                        .build())
                .isPublic(result.getIsPublic())
                .language(result.getLanguage())
                .noOfPages(result.getNoOfPages())
                .image(result.getImage())
                .editure(result.getEditure())
                .build();
    }

}
