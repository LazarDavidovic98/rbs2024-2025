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
import org.springframework.web.util.HtmlUtils;

@Controller
public class CommentController {
    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    private CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Originalni endpoint za JSON podatke (za postojeću funkcionalnost)
    @PostMapping(value = "/comments", consumes = "application/json")
    public ResponseEntity<Void> createComment(@RequestBody Comment comment, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        comment.setUserId(user.getId());
        
        // Validacija unosa 
        if (comment.getComment() == null || comment.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Sanitizacija inputa za zastitu od xss-a
        String sanitizedComment = HtmlUtils.htmlEscape(comment.getComment().trim());
        comment.setComment(sanitizedComment);
        
        if (sanitizedComment.length() > 1000) {
            return ResponseEntity.badRequest().build();
        }
        
        commentRepository.create(comment);

        return ResponseEntity.noContent().build();
    }

}
