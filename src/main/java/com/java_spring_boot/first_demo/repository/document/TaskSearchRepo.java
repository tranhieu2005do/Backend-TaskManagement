package com.java_spring_boot.first_demo.repository.document;

import com.java_spring_boot.first_demo.document.TaskDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSearchRepo extends ElasticsearchRepository<TaskDocument, Long> {

    List<TaskDocument> findByTitleContainingOrDescriptionContaining(
            String title,
            String description);
}
