package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    private CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Originalni endpoint za JSON podatke (za postojeću funkcionalnost)
    @PostMapping(value = "/comments", consumes = "application/json")
    public ResponseEntity<Void> createCommentJson(@RequestBody Comment comment, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        comment.setUserId(user.getId());
        commentRepository.create(comment);

        return ResponseEntity.noContent().build();
    }

    // Novi endpoint za form podatke (za CSRF napad)
    @PostMapping(value = "/comments", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Void> createCommentForm(@RequestParam("bookId") int bookId, 
                                                 @RequestParam("comment") String commentText,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setComment(commentText);
        comment.setUserId(user.getId());
        commentRepository.create(comment);

        return ResponseEntity.noContent().build();
    }
}
