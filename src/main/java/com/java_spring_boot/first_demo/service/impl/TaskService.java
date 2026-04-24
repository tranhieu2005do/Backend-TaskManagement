package com.java_spring_boot.first_demo.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.document.TaskDocument;
import com.java_spring_boot.first_demo.document.mapper.TaskDocumentMapper;
import com.java_spring_boot.first_demo.dto.request.AddTaskOwnerRequest;
import com.java_spring_boot.first_demo.dto.request.CreatedNotificationRequest;
import com.java_spring_boot.first_demo.dto.request.CreatedTaskRequest;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.entity.*;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.exception.OptimisticLockException;
import com.java_spring_boot.first_demo.repository.*;
import com.java_spring_boot.first_demo.repository.document.TaskSearchRepo;
import com.java_spring_boot.first_demo.service.async_service.NotificationAsyncService;
import com.java_spring_boot.first_demo.service.interf.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NotificationAsyncService notificationAsyncService;
    private final TaskSearchRepo  taskSearchRepo;
    private final ElasticsearchOperations elasticsearchOperations;
    private final TaskDocumentMapper taskDocumentMapper;
    private final DocumentService documentService;


    @Audit(action = "CREATE_TASK", entity = "TASK")
    @PreAuthorize("@teamSecurity.isAdmin(#request.teamId)")
    @Transactional(rollbackFor = AccessDeniedException.class)
    @Override
    public CreatedTaskResponse createTask(Long creatorId, CreatedTaskRequest request) {
        log.info("Creating task with request {}", request);
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundException("Team not found"));
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("Creator not found"));
        Task newTask = Task.builder()
                .team(team)
                .dueDate(request.getDueDate())
                .updatedAt(LocalDateTime.now())
                .title(request.getTitle())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .status(Task.Status.todo)
                .createdBy(creator)
                .build();
        taskRepository.save(newTask);
        // index sang elastic
        // Đảm bảo sau khi commit bên db thì mới ghi vào elastic để không bị write dirty(ghi dữ liệu chưa được commit)
        log.info("Map to taskDocument in elasticsearch");
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        taskSearchRepo.save(taskDocumentMapper.mapToTaskDocument(newTask));
                        documentService.createDocument(newTask.getId());
                    }
                }
        );

        if(request.getEmailTaskOwner() != null && !request.getEmailTaskOwner().isEmpty()) {
            for(String email : request.getEmailTaskOwner()){
                addTaskOwner(AddTaskOwnerRequest.builder()
                        .email(email)
                        .taskId(newTask.getId())
                        .build());
            }
        }
        return CreatedTaskResponse.builder()
                .status(Task.Status.todo)
                .id(newTask.getId())
                .dueDate(newTask.getCreatedAt())
                .creator(creator.getFullName())
                .title(newTask.getTitle())
                .description(newTask.getDescription())
                .build();
    }

    @Audit(action = "ADD_TASK_OWNER", entity = "TaskAssignment")
    @PreAuthorize("@teamSecurity.isAdminByTaskId(#request.taskId)")
    @Transactional(rollbackFor = AccessDeniedException.class)
    @Override
    public AddTaskOwnerResponse addTaskOwner(AddTaskOwnerRequest request) {
        log.info("Adding user with email {} to task with id {}",
                request.getEmail(), request.getTaskId());
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email "+ request.getEmail() +" not found"));
//        if(user == null){
//            log.error("User with email {} not found", request.getEmail());
//            throw new NotFoundException("User not found");
//        }
        TaskAssignment newTaskAssignment = TaskAssignment.builder()
                .task(task)
                .user(user)
                .assignedAt(LocalDateTime.now())
                .build();

        taskAssignmentRepository.save(newTaskAssignment);
        log.info("Added task assignment successfully with task id {} to user with email {}",
                newTaskAssignment.getId(), request.getEmail());
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        notificationAsyncService.createNotificationAsync(
                                CreatedNotificationRequest.builder()
                                        .content("You have just been assigned in a task. Let's see it.")
                                        .receiverId(user.getId())
                                        .type(Notification.Type.newtask)
                                        .build()
                        );
                    }
                }
        );

        return AddTaskOwnerResponse.builder()
                .newOwner(user.getFullName())
                .title(task.getTitle())
                .description(task.getDescription())
                .build();
    }

//    public void doUpdateTask(Long taskId, LocalDate newDueDate, Task.Status newStatus){
//        int maxEntries = 3;
//        for(int i = 0; i < maxEntries; i++){
//            try {
//                updateTask(taskId, newDueDate, newStatus);
//                return;
//            } catch (OptimisticLockException e){
//                if(i == maxEntries - 1){
//                    log.error("Update task with id {} failed because of optimistic locking", taskId, e);
//                    throw new OptimisticLockException("Update task with id " + taskId + " failed");
//                }
//            }
//        }
//    }

    @Audit(action = "UPDATE_TASK", entity="Task")
    @PreAuthorize("@teamSecurity.isAdminByTaskId(#taskId)")
    @Transactional(rollbackFor = AccessDeniedException.class)
    @Override
    public void updateTask(Long taskId, LocalDate newDueDate, Task.Status newStatus, Long version) {
            try {
                log.info("Updating task with id {}, version {}", taskId, version);
                Task task =  taskRepository.findById(taskId)
                        .orElseThrow(() -> new NotFoundException("Task not found"));
                if(task.getVersion() != version){
                    throw new OptimisticLockException("Optimistic locking not supported");
                }
                if(newDueDate != null){
                    task.setDueDate(newDueDate);
                }
                if(newStatus != null){
                    task.setStatus(newStatus);
                    log.info("Updated task status successfully with task id {}", taskId);
                }
                task.setUpdatedAt(LocalDateTime.now());
                taskRepository.saveAndFlush(task);
                log.info("Updated the task");
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronizationAdapter() {
                            @Override
                            public void afterCommit() {
                                taskSearchRepo.save(taskDocumentMapper.mapToTaskDocument(task));
                            }
                        }
                );
                log.info("Updated data in elasticsearch");
            } catch (OptimisticLockingFailureException e){
                log.error("Optimistic lock exception", e);
                throw new OptimisticLockException("Optimistic lock exception");
            }
    }

    @Audit(action = "GET_UNFINISHED_TASK", entity = "Task")
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Transactional(rollbackFor = AccessDeniedException.class)
    @Override
    public PageResponse<UnfinishedTask> getAllUnfinishedTasksOfUser(Long userId) {
        log.info("Getting all unfinished tasks of user with id {}", userId);
        Pageable pageable =  PageRequest.of(0, 15, Sort.by("assignedAt").descending());
        return PageResponse.fromPage(
                taskRepository.getUnfinishedTasksOfUser(userId, pageable, Task.Status.done)
                        .map(UnfinishedTask::fromEntity)
        );
    }

    @Audit(action = "FILTER_TASKS", entity = "Task")
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Transactional(rollbackFor = AccessDeniedException.class)
    @Override
    public PageResponse<TaskResponse> filterTasksOfUser(
            Long userId,
            Long teamId,
            Task.Status status,
            LocalDate first,
            LocalDate second,
            String sortBy) {
        log.info("Filtering task of user with id {}, teamId {}, status {}, first is {}, second is {}",
                userId, teamId, status, first, second);
        Pageable pageable = PageRequest.of(0, 15, Sort.by("assignedAt").descending());
        return PageResponse.fromPage(
                taskRepository.filterTasksOfUser(userId, pageable, teamId, first, second, status)
                        .map(TaskResponse::fromEntity)
        );
    }

    @Audit(action = "SEARCH_TASK", entity = "Task")
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    public List<TaskDocument> searchAdvanced(String keyword, String status, Long userId) {
        log.info("Elasticsearch searching for tasks with keyword {} and status {}", keyword, status);
        Query query = buildQuery(keyword, status, userId);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withSort(s -> s
                        .field(f -> f
                                .field("dueDate")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                        )
                )
                .build();

        return elasticsearchOperations
                .search(nativeQuery, TaskDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    // 1 query trên 1 document = 1 query join các bảng với nhau . do đã flatten dữ liệu join
    public Query buildQuery(String keyword, String status, Long userId) {
        log.info("Build query in elasticsearch");
        return Query.of(q -> q.bool(b -> {

            b.filter(f -> f.terms(t -> t
                    .field("participantIds")
                    .terms(v -> v.value(List.of(FieldValue.of(userId)))
            )));

            if (keyword != null && !keyword.isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(keyword)
                        .fields("title", "description")
                        .type(TextQueryType.BoolPrefix) // để đảm bảo gõ logi thì vẫn tra cứu cả login (prefix...)
                        // Nếu muốn ngram tokenizer thì phải tạo index thủ công, đồng nghĩa kích thước index sẽ lớn hơn
                ));
            }

            if (status != null && !status.isBlank()) {
                b.filter(f -> f.term(t -> t
                        .field("status")
                        .value(status)
                ));
            }

            return b;
        }));
    }

}
