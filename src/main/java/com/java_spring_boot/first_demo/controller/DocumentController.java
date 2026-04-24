package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.request.UpdateDocumentRequest;
import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.dto.response.DocumentResponse;
import com.java_spring_boot.first_demo.service.impl.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/document")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentByTaskId(
            @RequestParam Long taskId) {
        return ResponseEntity.ok(ApiResponse.<DocumentResponse>builder()
                .data(documentService.getDocumentByTaskId(taskId))
                .statusCode(HttpStatus.OK.value())
                .message("Get document successfully")
                .build());
    }

    @PatchMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> updateDocument(
            @PathVariable Long documentId,
            @RequestBody UpdateDocumentRequest request){
        documentService.updateDocument(request.getContent(), documentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Update document successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
