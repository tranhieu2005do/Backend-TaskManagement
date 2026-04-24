package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.CreatedCommentRequest;
import com.java_spring_boot.first_demo.dto.response.CommentResponse;
import com.java_spring_boot.first_demo.dto.response.CreatedCommentResponse;

import java.util.List;

public interface ICommentService {

    CreatedCommentResponse createComment(CreatedCommentRequest createdCommentRequest);

    List<CommentResponse> getCommentsByTaskId(Long taskId);

    void deleteComment(Long commentId);
}
