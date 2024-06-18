package com.app.SmartReader.repositories;

import com.app.SmartReader.models.Book;
import com.app.SmartReader.utils.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
//    @Query("SELECT b FROM Book b WHERE b.isPublic = :status AND " +
//            "(LOWER(b.title) LIKE LOWER(:searchString) OR " +
//            "LOWER(b.author) LIKE LOWER(:searchString))")
//    List<Book> searchBooksByTitleOrAuthorAndStatus(@Param("searchString") String searchString,
//                                                   @Param("status") BookStatus status);
}
