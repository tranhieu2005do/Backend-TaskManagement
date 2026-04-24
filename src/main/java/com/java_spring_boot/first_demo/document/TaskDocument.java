package com.java_spring_boot.first_demo.document;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
@Document(indexName = "tasks")
public class TaskDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    // từ User
    @Field(type = FieldType.Text)
    private String createdByName;

    @Field(type = FieldType.Keyword)
    private Long createdById;

    @Field(type = FieldType.Long)
    private List<Long> participantIds;

    // từ Team
    @Field(type = FieldType.Text)
    private String teamName;

    @Field(type = FieldType.Keyword)
    private Long teamId;

    @Field(type = FieldType.Keyword)
    private String status;

    @JsonProperty("due_date")
    @Field(type = FieldType.Date)
    private LocalDate dueDate;

//    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
//    private LocalDateTime createdAt;
}
