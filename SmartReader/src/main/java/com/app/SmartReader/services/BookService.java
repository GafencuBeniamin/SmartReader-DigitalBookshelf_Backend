package com.app.SmartReader.services;

import com.app.SmartReader.dtos.BookDto;
import com.app.SmartReader.models.User;
import com.app.SmartReader.repositories.BookRepository;
import com.app.SmartReader.models.Book;
import com.app.SmartReader.repositories.UserRepository;
import com.app.SmartReader.utils.enums.BookStatus;
import com.app.SmartReader.utils.enums.UserRole;
import com.app.SmartReader.utils.exceptions.CrudOperationException;
import com.app.SmartReader.utils.mappers.EntityToDtoMapper;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.SmartReader.utils.enums.BookState;

import javax.management.relation.Role;
import java.util.*;
import java.util.stream.Collectors;

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
        book.setBookStates(bookDto.getBookStates());
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
                .bookStates(bookDto.getBookStates())
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

    public BookDto getBookByIdByUser(Integer id, String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book=  bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        Map<Integer, BookState> foundBookStates= book.getBookStates();
        Map<Integer, BookState> bookStateOfUser= new HashMap<>();
        bookStateOfUser.put(user.getId(), foundBookStates.get(user.getId()));
        book.setBookStates(bookStateOfUser);
        return EntityToDtoMapper.mapBookToDto(book);
    }
    public List<BookDto> getUserBooks(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Book> books = user.getBooks();
            List<BookDto> results = new ArrayList<>();
            books.forEach(book -> {
                Map<Integer, BookState> foundBookStates= book.getBookStates();
                Map<Integer, BookState> bookStateOfUser= new HashMap<>();
                bookStateOfUser.put(user.getId(), foundBookStates.get(user.getId()));
                book.setBookStates(bookStateOfUser);
                results.add(EntityToDtoMapper.mapBookToDto(book));
            });
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

        // Update the book's bookStates map to include the new state for this user
        Map<Integer, BookState> bookStates = book.getBookStates();
        bookStates.put(user.getId(), BookState.TO_BE_READ);
        book.setBookStates(bookStates);

        bookRepository.save(book);
        userRepository.save(user);
        return EntityToDtoMapper.mapBookToDto(book);
    }


    public BookDto createNewBookByUser(BookDto bookDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CrudOperationException("User does not exist"));

        // Retrieve the initial book states from the dto and modify the key of the first entry
        Map<Integer, BookState> bookStates = bookDto.getBookStates();
        if (bookStates == null || bookStates.isEmpty()) {
            throw new CrudOperationException("Initial book states are not provided in the DTO");
        }

        // Extract the first entry (if any) and update its key to the user's ID
        Map.Entry<Integer, BookState> firstEntry = bookStates.entrySet().iterator().next();
        bookStates.remove(firstEntry.getKey());
        bookStates.put(user.getId(), firstEntry.getValue());

        Book book = Book.builder()
                .author(bookDto.getAuthor())
                .createdBy(user)
                .editure(bookDto.getEditure())
                .genre(bookDto.getGenre())
                .image(bookDto.getImage())
                .title(bookDto.getTitle())
                .isPublic(BookStatus.PRIVATE)
                .language(bookDto.getLanguage())
                .noOfPages(bookDto.getNoOfPages())
                .bookStates(bookStates)
                .build();
        bookRepository.save(book);
        bookDto.setId(book.getId());
        Set<Book> userBooks = user.getBooks();
        userBooks.add(book);
        user.setBooks(userBooks);
        userRepository.save(user);
        return EntityToDtoMapper.mapBookToDto(book);
    }
    public BookDto updatePublicBookByUser (Integer id, BookState bookState, String username){
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        if (book.getIsPublic()==BookStatus.PUBLIC) {
            Map<Integer, BookState> foundBookStates = book.getBookStates();
            foundBookStates.put(user.getId(), bookState);

            book.setBookStates(foundBookStates);
            bookRepository.save(book);
        }
        else throw new CrudOperationException("Book is not public");
        return EntityToDtoMapper.mapBookToDto(book);
    }
    public BookDto updateBookByUser (Integer id, BookDto bookDto, String username){
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));

        // Retrieve the initial book states from the dto and modify the key of the first entry
        Map<Integer, BookState> bookStates = bookDto.getBookStates();
        if (bookStates == null || bookStates.isEmpty()) {
            throw new CrudOperationException("Initial book states are not provided in the DTO");
        }

        // Extract the first entry (if any) and update its key to the user's ID
        Map.Entry<Integer, BookState> firstEntry = bookStates.entrySet().iterator().next();

        Map<Integer, BookState> foundBookStates= book.getBookStates();
        foundBookStates.put(user.getId(), firstEntry.getValue());

        if (book.getCreatedBy().getId().equals(user.getId()) || user.getRole()== UserRole.ADMIN || user.getRole()== UserRole.MODERATOR) {
            book.setImage(bookDto.getImage());
            book.setGenre(bookDto.getGenre());
            book.setEditure(bookDto.getEditure());
            book.setAuthor(bookDto.getAuthor());
            book.setLanguage(bookDto.getLanguage());
            book.setBookStates(foundBookStates);
            book.setTitle(bookDto.getTitle());
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
    public BookDto changeBookStatus(Integer id, String username, BookStatus status) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        Book book = bookRepository.findById(id).orElseThrow(() -> new CrudOperationException("Book does not exist"));
        if (user.getRole() == UserRole.USER && status==BookStatus.PUBLIC){
            throw new CrudOperationException("User can't make a book Public");
        }
        else{
            book.setIsPublic(status);
            bookRepository.save(book);
        }
        return EntityToDtoMapper.mapBookToDto(book);
    }
    public List<BookDto> getAllPendingBooks( String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CrudOperationException("User does not exist"));
        List<Book> books = bookRepository.findAll();

        // Filter books with isPublic == BookStatus.PENDING
        List<Book> pendingBooks = books.stream()
                .filter(book -> book.getIsPublic() == BookStatus.PENDING)
                .toList();

        // Map pending books to BookDto
        List<BookDto> results = pendingBooks.stream()
                .map(EntityToDtoMapper::mapBookToDto)
                .collect(Collectors.toList());
        return results;
    }
    public List<BookDto> searchBooksByTitleOrAuthor(String keyword, String username) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CrudOperationException("Keyword cannot be empty");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CrudOperationException("User does not exist"));

        List<Book> books = bookRepository.findAll();

        List<Book> filteredBooks = books.stream()
                .filter(book -> book.getIsPublic() == BookStatus.PUBLIC &&
                        (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                authorContainsKeyword(book.getAuthor(), keyword)))
                .toList();

        return filteredBooks.stream()
                .map(EntityToDtoMapper::mapBookToDto)
                .collect(Collectors.toList());
    }

    private boolean authorContainsKeyword(Set<String> authors, String keyword) {
        return authors.stream()
                .anyMatch(author -> author.toLowerCase().contains(keyword.toLowerCase()));
    }
}
