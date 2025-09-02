package com.hrms.project.controller;

import com.hrms.project.dto.PaginatedResponseDTO;
import com.hrms.project.dto.TaskDTO;
import com.hrms.project.dto.TaskUpdateDTO;
import com.hrms.project.security.CheckEmployeeAccess;
import com.hrms.project.service.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")

public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @PostMapping("/{tlId}/{employeeId}/{projectId}/task")
    @PreAuthorize("hasAnyRole( 'TEAM_LEAD')")
    public ResponseEntity<String> createAssignment( @PathVariable String employeeId,
                                                    @PathVariable String tlId,
                                                    @PathVariable String projectId,
                                                    @RequestPart(value = "attachedFileLinks",required = false)
                                                    MultipartFile[] attachedFileLinks,
                                                    @RequestPart TaskDTO taskDTO) throws IOException {
        return new ResponseEntity<>(taskService.createAssignment(employeeId,tlId,attachedFileLinks,projectId,taskDTO), HttpStatus.CREATED);

    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/tasks/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'MANAGER','TEAM_LEAD')")
    public ResponseEntity<PaginatedResponseDTO> getALlTasks(@PathVariable Integer pageNumber,
                                                            @PathVariable Integer pageSize,
                                                            @PathVariable String sortBy,
                                                            @PathVariable String sortOrder,
                                                            @PathVariable String employeeId) {
        return new ResponseEntity<>(taskService.getAllTasks(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);
    }

    @GetMapping("/task/{projectId}/{taskId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String projectId,@PathVariable String taskId){
        return new ResponseEntity<>(taskService.getTask(projectId,taskId),HttpStatus.OK);
    }


    @PutMapping("{tlId}/{employeeId}/{projectId}/task")
    @PreAuthorize("hasAnyRole( 'TEAM_LEAD')")
    @CheckEmployeeAccess(param = "id", roles = {"ADMIN","MANAGER","TEAM_LEAD"})
    public ResponseEntity<String> updateTask(@RequestPart TaskDTO taskDTO,
                                             @PathVariable String tlId,
                                             @RequestPart(value = "attachedFileLinks",required = false) MultipartFile[] attachedFileLinks,
                                             @PathVariable String employeeId,
                                             @PathVariable String projectId) throws IOException {
        return new ResponseEntity<>(taskService.updateTask(taskDTO,tlId,employeeId,attachedFileLinks,projectId),HttpStatus.CREATED);
    }

    @DeleteMapping("{projectId}/{taskId}/delete/task")
    @PreAuthorize("hasAnyRole('TEAM_LEAD')")
    public ResponseEntity<String> deleteTask(@PathVariable String projectId,@PathVariable String taskId){
        return new ResponseEntity<>(taskService.deleteTask(projectId,taskId),HttpStatus.OK);
    }

    @PostMapping("history/{projectId}/{taskId}")
    @PreAuthorize("hasAnyRole('TEAM_LEAD')")
    public ResponseEntity<String> createTaskHistory(@PathVariable String projectId,
                                                    @PathVariable String taskId,
                                                    @RequestPart MultipartFile[] relatedFileLinks,
                                                    @RequestPart TaskUpdateDTO taskUpdateDTO) throws IOException {
        return new ResponseEntity<>(taskService.createStatus(taskId,projectId,relatedFileLinks, taskUpdateDTO), HttpStatus.CREATED);
    }

    @PutMapping("{reviewedById}/status/{projectId}/{taskId}")
    @PreAuthorize("hasAnyRole('TEAM_LEAD')")
    @CheckEmployeeAccess(param = "id", roles = {"ADMIN", "HR","MANAGER","TEAM_LEAD"})
    public ResponseEntity<String> updateTaskHistory(@PathVariable String taskId,
                                                    @PathVariable String reviewedById,
                                                    @PathVariable String projectId,
                                                    @RequestBody TaskUpdateDTO taskUpdateDTO) throws IOException {
        return new ResponseEntity<>(taskService.updateStatus(reviewedById,taskId,projectId, taskUpdateDTO),
                HttpStatus.OK);
    }

    @GetMapping("/{projectId}/{taskId}/updatetasks")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
    public ResponseEntity<List<TaskUpdateDTO>> getUpdateTasks(@PathVariable String projectId,
                                                              @PathVariable String taskId){
        return new ResponseEntity<>(taskService.getUpdateTasks(projectId,taskId),HttpStatus.OK);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/{tlId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
    public ResponseEntity<PaginatedResponseDTO> getTasks(@PathVariable Integer pageNumber,
                                                  @PathVariable Integer pageSize,
                                                  @PathVariable String sortBy,
                                                  @PathVariable String sortOrder,
                                                  @PathVariable String tlId){
        return new ResponseEntity<>(taskService.getTasks(pageNumber,pageSize,sortBy,sortOrder,tlId),HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/{taskId}/{historyId}/delete")
    @PreAuthorize("hasAnyRole('TEAM_LEAD')")
    public ResponseEntity<String> deleteTaskHistory(@PathVariable String projectId,
                                                    @PathVariable String taskId,
                                                    @PathVariable Long historyId){
        return new ResponseEntity<>(taskService.deleteTaskHsitory(projectId,taskId,historyId),HttpStatus.OK);
    }
}