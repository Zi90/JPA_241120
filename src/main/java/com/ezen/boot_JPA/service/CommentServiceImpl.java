package com.ezen.boot_JPA.service;

import com.ezen.boot_JPA.dto.CommentDTO;
import com.ezen.boot_JPA.entity.Comment;
import com.ezen.boot_JPA.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;

    @Override
    public long post(CommentDTO commentDTO) {
        return commentRepository.save(convertDtoToEntity(commentDTO)).getBno();
    }

//    @Override
//    public List<CommentDTO> getList(long bno) {
//        // select * from comment where bno = ${bno}
//        List<Comment> list = commentRepository.findByBno(bno);
//        List<CommentDTO> dtoList = list.stream().map(c -> convertEntityToDto(c)).toList();
//        return dtoList;
//    }

    @Override
    public Page<CommentDTO> getList(long bno, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("cno").descending());
        Page<Comment> list = commentRepository.findByBno(bno, pageable);
        Page<CommentDTO> dtoPageList = list.map(c -> convertEntityToDto(c));
        return dtoPageList;
    }

    @Override
    public long update(CommentDTO commentDTO) {
        Optional<Comment> optionalComment = commentRepository.findById(commentDTO.getCno());
        if(optionalComment.isPresent()){
            Comment comment = optionalComment.get();
            comment.setContent(commentDTO.getContent());
            return commentRepository.save(comment).getCno();
        }
        return 0;
    }

    @Override
    public long remove(long cno) {
        Optional<Comment> comment = commentRepository.findById(cno);
        if(comment.isPresent()) {
            commentRepository.deleteById(cno);
            return 1;
        }else {
            return 0;
        }
    }
}
