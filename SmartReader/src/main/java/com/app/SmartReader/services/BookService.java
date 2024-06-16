package com.app.SmartReader.services;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.BookRepository;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.exceptions.CrudOperationException;
import com.app.SmartReader.utils.mappers.EntityToDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

        books.forEach(result -> results.add(EntityToDtoMapper.mapBookToDto(result)));
        return results;
    }

    public BookDto getBookById(Integer id){
        Book result=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        return EntityToDtoMapper.mapBookToDto(result);
    }

    public BookDto updateBook(Integer id, BookDto bookDto){

        Book book=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        User user = userRepository.findById(bookDto.getCreatedBy()).orElseThrow(() -> new CrudOperationException("User does not exist"));
        book.setId(bookDto.getId());
        book.setImage(bookDto.getImage());
        book.setGenre(bookDto.getGenre());
        book.setEditure(bookDto.getEditure());
        book.setAuthor(bookDto.getAuthor());
        book.setLanguage(bookDto.getLanguage());
        book.setState(bookDto.getState());
        book.setTitle(book.getTitle());
        book.setCreatedBy(book.getCreatedBy());
        book.setIsPublic(bookDto.getIsPublic());
        book.setNoOfPages(bookDto.getNoOfPages());
        return bookDto;
    }

    public BookDto addBook(BookDto bookDto){
        User user = userRepository.findById(bookDto.getCreatedBy()).orElseThrow(() -> new CrudOperationException("User does not exist"));
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
        // Remove the book from all users' libraries
        Set<User> users = result.getUsers();
        for (User user : users) {
            user.getBooks().remove(result);
            userRepository.save(user);
        }
        bookRepository.delete(result);
        return EntityToDtoMapper.mapBookToDto(result);
    }
    /** USER SPECIFIC CRUD BELOW **/
    public List<BookDto> getUserBooks(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Book> books = user.getBooks();
            List<BookDto> results = new ArrayList<>();
            books.forEach(result -> results.add(EntityToDtoMapper.mapBookToDto(result)));
            return results;
        }
        else throw new CrudOperationException("User does not exist");
    }

    public BookDto addExistingBookToUserLibrary(Integer id, String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        Set<Book> userBooks = user.getBooks();
        userBooks.add(book);
        user.setBooks(userBooks);
        userRepository.save(user);
        return EntityToDtoMapper.mapBookToDto(book);
    }


    public BookDto createNewBookByUser(BookDto bookDto, String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Book book = Book.builder()
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
            Set<Book> userBooks = user.getBooks();
            userBooks.add(book);
            user.setBooks(userBooks);
            userRepository.save(user);
            return EntityToDtoMapper.mapBookToDto(book);
        }
        else throw new CrudOperationException("User does not exist");
    }
    public BookDto updateBookByUser (Integer id, BookDto bookDto, String username){
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        if (book.getCreatedBy().getId().equals(user.getId())) {
            book.setImage(bookDto.getImage());
            book.setGenre(bookDto.getGenre());
            book.setEditure(bookDto.getEditure());
            book.setAuthor(bookDto.getAuthor());
            book.setLanguage(bookDto.getLanguage());
            book.setState(bookDto.getState());
            book.setTitle(book.getTitle());
            book.setCreatedBy(book.getCreatedBy());
            book.setIsPublic(bookDto.getIsPublic());
            book.setNoOfPages(bookDto.getNoOfPages());
            bookRepository.save(book);
            return EntityToDtoMapper.mapBookToDto(book);
        }
        else throw  new CrudOperationException("User can't edit an unowned book");
    }
    public BookDto removeBookFromUserLibrary (Integer id, String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean bookExistsInLibrary = user.getBooks().stream()
                    .anyMatch(book -> book.getId().equals(id));
            if (bookExistsInLibrary){
                Book book = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
                Set<Book> userBooks = user.getBooks();
                //if book exists
                userBooks.remove(book);
                user.setBooks(userBooks);
                userRepository.save(user);
                return EntityToDtoMapper.mapBookToDto(book);
            }
            else throw new CrudOperationException("User doesn't have that book in library");

        }
        else throw new CrudOperationException("User does not exist");
    }
    public BookDto removeBookByUser (Integer id, String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book=bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        if (book.getCreatedBy().getId().equals(user.getId())) {
            // Remove the book from all users' libraries
            Set<User> users = book.getUsers();
            for (User user1 : users) {
                user1.getBooks().remove(book);
                userRepository.save(user1);
            }
            bookRepository.deleteById(id);
            return EntityToDtoMapper.mapBookToDto(book);
        }
        else throw new CrudOperationException("User can't delete an unowned book");
    }
}
