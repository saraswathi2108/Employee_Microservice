package com.hrms.project.service;

import com.hrms.project.configuration.TaskId;
import com.hrms.project.configuration.TaskUpdateId;
import com.hrms.project.dto.AllTaskDTO;
import com.hrms.project.dto.TaskDTO;
import com.hrms.project.dto.TaskUpdateDTO;
import com.hrms.project.entity.*;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.handlers.ProjectNotFoundException;
import com.hrms.project.handlers.TaskNotFoundException;
import com.hrms.project.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskUpdateRepository taskUpdateRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private S3Service s3Service;


    public String createAssignment(String employeeId, String tlId, MultipartFile[] attachedFileLinks, String projectId, TaskDTO taskDTO) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));


        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        Task task = modelMapper.map(taskDTO, Task.class);

        TaskId taskId = new TaskId(taskDTO.getId(), projectId);
        task.setCreatedBy(tlId);
        task.setId(taskId);
        task.setEmployee(employee);
        task.setProject(project);
        if (attachedFileLinks != null && attachedFileLinks.length > 0 && !attachedFileLinks[0].isEmpty()) {
            List<String> s3Keys = s3Service.uploadMultipleFiles(employeeId, "attachedFileLinks", attachedFileLinks);
            task.setAttachedFileLinks(s3Keys);
        }
        taskRepository.save(task);

        return "Assignment Created Successfully";
    }

    public List<AllTaskDTO> getAllTasks(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId) {


        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getTasks() == null || employee.getTasks().isEmpty()) {
            throw new TaskNotFoundException("This employee has no tasks assigned");
        }

       Page<Task> tasks = taskRepository.findByEmployee_EmployeeId(employeeId,pageable);

        return tasks.stream()
                .map(task -> {
                    AllTaskDTO dto = new AllTaskDTO();
                    dto.setId(task.getId().getTaskId());
                    dto.setStatus(task.getStatus());
                    dto.setTitle(task.getTitle());
                    dto.setPriority(task.getPriority());
                    dto.setStartDate(task.getCreatedDate());
                    dto.setDueDate(task.getDueDate());
                    dto.setProjectId(task.getProject().getProjectId());
                    dto.setAssignedTo(task.getAssignedTo());
                    dto.setCreatedBy(task.getCreatedBy());
                    return dto;
                })
                .toList();
    }

    public String updateTask(TaskDTO taskDTO, String tlId, String employeeId, MultipartFile[] attachedFileLinks, String projectId) throws IOException {
        TaskId compositeTaskId = new TaskId(taskDTO.getId(), projectId);

        Task task = taskRepository.findById(compositeTaskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));


        if (task.getAttachedFileLinks() != null && !task.getAttachedFileLinks().isEmpty()) {
            for (String key : task.getAttachedFileLinks()) {
                s3Service.deleteFile(key);
            }

        }
        if (attachedFileLinks != null && attachedFileLinks.length > 0 && !attachedFileLinks[0].isEmpty()) {
            List<String> s3Keys = s3Service.uploadMultipleFiles(task.getEmployee().getEmployeeId(), "attachedFileLinks", attachedFileLinks);
            taskDTO.setAttachedFileLinks(s3Keys);
        }


        taskDTO.setProjectId(projectId);
        modelMapper.map(taskDTO, task);

        task.setCreatedBy(tlId);
        task.setId(compositeTaskId);

        task.setEmployee(employee);
        task.setProject(project);


        //task.setAttachedFileLinks(S3Key);

        taskRepository.save(task);

        return "Assignment Updated Successfully";
    }

    public String createStatus(String reviewedById, String taskId, String projectId, MultipartFile[] relatedFileLinks, TaskUpdateDTO taskUpdateDTO) throws IOException {

        TaskId taskKey = new TaskId(taskId, projectId);
        Task task = taskRepository.findById(taskKey)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (taskUpdateDTO.getId() == null) {
            throw new IllegalArgumentException("Task update ID must be provided");
        }

        TaskUpdateId compositeUpdateId = new TaskUpdateId(taskKey, taskUpdateDTO.getId());

        TaskUpdate taskUpdate = new TaskUpdate();
        taskUpdate.setTask(task);
        taskUpdate.setId(compositeUpdateId);

        taskUpdate.setUpdatedDate(LocalDateTime.now());
        taskUpdate.setChanges(taskUpdateDTO.getChanges());
        taskUpdate.setNote(taskUpdateDTO.getNote());
        taskUpdate.setRelatedLinks(taskUpdateDTO.getRelatedLinks());
        taskUpdate.setReviewedBy(reviewedById);
        taskUpdate.setRemark(taskUpdateDTO.getRemark());
        if (relatedFileLinks != null && relatedFileLinks.length > 0 && !relatedFileLinks[0].isEmpty()) {

        List<String> s3Keys = s3Service.uploadMultipleFiles(task.getEmployee().getEmployeeId(), "relatedFileLinks", relatedFileLinks);
        taskUpdate.setRelatedFileLinks(s3Keys);
        }

        taskUpdateRepository.save(taskUpdate);
        return "Task update saved successfully";
    }


    public String updateStatus(String reviewedById, String taskId,
                               //MultipartFile[] relatedFileLinks,
                               String projectId, TaskUpdateDTO taskUpdateDTO) throws IOException {
        TaskId taskKey = new TaskId(taskId, projectId);
        TaskUpdateId compositeUpdateId = new TaskUpdateId(taskKey, taskUpdateDTO.getId());


        Task task = taskRepository.findById(taskKey)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        TaskUpdate taskUpdate = taskUpdateRepository.findById(compositeUpdateId)
                .orElseThrow(() -> new TaskNotFoundException("Update not found"));

        taskUpdate.setUpdatedDate(taskUpdateDTO.getUpdatedDate());
        taskUpdate.setNote(taskUpdateDTO.getNote());
        taskUpdate.setChanges(taskUpdateDTO.getChanges());
        taskUpdate.setRelatedLinks(taskUpdateDTO.getRelatedLinks());
        taskUpdate.setRelatedFileLinks(taskUpdateDTO.getRelatedFileLinks());
        taskUpdate.setReviewedBy(reviewedById);
        taskUpdate.setRemark(taskUpdateDTO.getRemark());

//        if (taskUpdate.getRelatedFileLinks() != null && !taskUpdate.getRelatedFileLinks().isEmpty()) {
//            for (String key : taskUpdate.getRelatedFileLinks()) {
//                s3Service.deleteFile(key);
//            }
//        }
//            if (relatedFileLinks != null && relatedFileLinks.length > 0 && !relatedFileLinks[0].isEmpty()) {
//                List<String> s3Keys = s3Service.uploadMultipleFiles(task.getEmployee().getEmployeeId(), "relatedFileLinks", relatedFileLinks);
//                taskUpdate.setRelatedFileLinks(s3Keys);
//            }
//
//

        taskUpdate.setTask(task);

        taskUpdateRepository.save(taskUpdate);
        return "Updated Successfully";

    }

    public TaskDTO getTask(String projectId, String taskId) {

        TaskId taskKey = new TaskId(taskId, projectId);

        Task task = taskRepository.findById(taskKey)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId().getTaskId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedDate(task.getCreatedDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setDueDate(task.getDueDate());
        dto.setRating(task.getRating());
        dto.setRemark(task.getRemark());
        dto.setCompletionNote(task.getCompletionNote());
        dto.setRelatedLinks(task.getRelatedLinks());
        List<String> presignedUrls = task.getAttachedFileLinks().stream()
                .map(s3Service::generatePresignedUrl)
                .toList();

        dto.setAttachedFileLinks(presignedUrls);

        return dto;
    }

    public List<TaskUpdateDTO> getUpdateTasks(String projectId, String taskId) {
        return taskUpdateRepository.findAll()
                .stream()
                .filter(taskUpdate ->
                        taskUpdate.getTask().getId().getTaskId().equals(taskId) &&
                                taskUpdate.getTask().getId().getProjectId().equals(projectId)
                )
                .map(update -> {
                    TaskUpdateDTO dto = new TaskUpdateDTO();
                    dto.setId(update.getId().getUpdateNumber());
                    dto.setChanges(update.getChanges());
                    dto.setNote(update.getNote());
                    dto.setRelatedLinks(update.getRelatedLinks());
                    //dto.setRelatedFileLinks(update.getRelatedFileLinks());
                    dto.setUpdatedDate(update.getUpdatedDate());
                    dto.setReviewedBy(update.getReviewedBy());

                    dto.setRemark(update.getRemark());
                    List<String> presignedUrls = update.getRelatedFileLinks().stream()
                            .map(s3Service::generatePresignedUrl)
                            .toList();

                    dto.setRelatedFileLinks(presignedUrls);
                    return dto;
                })
                .toList();
    }

    public String deleteTask(String projectId, String taskId) {

        TaskId taskKey = new TaskId(taskId, projectId);
        Task task = taskRepository.findById(taskKey)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (task.getAttachedFileLinks() != null && !task.getAttachedFileLinks().isEmpty()) {
            for (String key : task.getAttachedFileLinks()) {
                s3Service.deleteFile(key);
            }
        }
            taskRepository.deleteById(taskKey);

            return "Task deleted successfully";
        }


    public List<TaskDTO> getTasks(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String tlId) {



        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Task> tasks = taskRepository.findByCreatedBy(tlId,pageable);



        return tasks.stream()
                .map(task -> {
                    TaskDTO dto = new TaskDTO();
                    dto.setId(task.getId().getTaskId());
                    dto.setTitle(task.getTitle());
                    dto.setDescription(task.getDescription());
                    dto.setCreatedBy(task.getCreatedBy());
                    dto.setAssignedTo(task.getAssignedTo());
                    dto.setStatus(task.getStatus());
                    dto.setPriority(task.getPriority());
                    dto.setCreatedDate(task.getCreatedDate());
                    dto.setCompletedDate(task.getCompletedDate());
                    dto.setDueDate(task.getDueDate());
                    dto.setRating(task.getRating());
                    dto.setRemark(task.getRemark());
                    dto.setCompletionNote(task.getCompletionNote());
                    dto.setRelatedLinks(task.getRelatedLinks());
                    dto.setAttachedFileLinks(task.getAttachedFileLinks());
                    dto.setProjectId(task.getId().getProjectId());
                    return dto;
                })
                .toList();
    }

    public String deleteTaskHsitory(String projectId, String taskId,Long id) {

        TaskId taskKey = new TaskId(taskId, projectId);
        TaskUpdateId taskUpdateId = new TaskUpdateId(taskKey,id);
        TaskUpdate task = taskUpdateRepository.findById(taskUpdateId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (task.getRelatedFileLinks() != null && !task.getRelatedFileLinks().isEmpty()) {
            for (String key : task.getRelatedFileLinks()) {
                s3Service.deleteFile(key);
            }
        }
        taskUpdateRepository.deleteById(taskUpdateId);
        return "Task deleted successfully";
    }
}