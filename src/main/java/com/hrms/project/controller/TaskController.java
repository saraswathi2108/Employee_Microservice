package com.hrms.project.controller;

import com.hrms.project.dto.AllTaskDTO;
import com.hrms.project.dto.TaskDTO;
import com.hrms.project.dto.TaskUpdateDTO;
import com.hrms.project.entity.Task;
import com.hrms.project.service.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")


//@CrossOrigin(origins = "*")
public class TaskController {


    @Autowired
    private TaskServiceImpl taskService;


    @PostMapping("/{tlId}/{employeeId}/{projectId}/task")
    public ResponseEntity<String> createAssignment( @PathVariable String employeeId,
                                                    @PathVariable String tlId,
                                                    @PathVariable String projectId,
                                                    @RequestPart(value = "attachedFileLinks",required = false)
                                                        MultipartFile[] attachedFileLinks,
                                                    @RequestPart TaskDTO taskDTO) throws IOException {

        return new ResponseEntity<>(taskService.createAssignment(employeeId,tlId,attachedFileLinks,projectId,taskDTO), HttpStatus.CREATED);

    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/tasks/{employeeId}")
    public ResponseEntity<List<AllTaskDTO>> getALlTasks(@PathVariable Integer pageNumber,
                                                        @PathVariable Integer pageSize,
                                                        @PathVariable String sortBy,
                                                        @PathVariable String sortOrder,
                                                        @PathVariable String employeeId)
    {
        return new ResponseEntity<>(taskService.getAllTasks(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);

    }

    @GetMapping("/task/{projectId}/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String projectId,@PathVariable String taskId){

        return new ResponseEntity<>(taskService.getTask(projectId,taskId),HttpStatus.OK);

    }


    @PutMapping("{tlId}/{employeeId}/{projectId}/task")
    public ResponseEntity<String> updateTask(@RequestPart TaskDTO taskDTO,
                                             @PathVariable String tlId,
                                             @RequestPart(value = "attachedFileLinks",required = false) MultipartFile[] attachedFileLinks,
                                             @PathVariable String employeeId,
                                             @PathVariable String projectId) throws IOException {
        return new ResponseEntity<>(taskService.updateTask(taskDTO,tlId,employeeId,attachedFileLinks,projectId),HttpStatus.CREATED);
    }


    @DeleteMapping("{projectId}/{taskId}/delete/task")
    public ResponseEntity<String> deleteTask(@PathVariable String projectId,@PathVariable String taskId){
        return new ResponseEntity<>(taskService.deleteTask(projectId,taskId),HttpStatus.OK);
    }



    @PostMapping("{reviewedById}/history/{projectId}/{taskId}")
    public ResponseEntity<String> createTaskHistory(@PathVariable String projectId,
                                                    @PathVariable String reviewedById,
                                                    @PathVariable String taskId,
                                                    @RequestPart MultipartFile[] relatedFileLinks,
                                                    @RequestPart TaskUpdateDTO taskUpdateDTO) throws IOException {

        return new ResponseEntity<>(taskService.createStatus(reviewedById,taskId,projectId,relatedFileLinks, taskUpdateDTO), HttpStatus.CREATED);
    }

    @PutMapping("{reviewedById}/status/{projectId}/{taskId}")
    public ResponseEntity<String> updateTaskHistory(@PathVariable String taskId,
                                                    @PathVariable String reviewedById,
                                                    @PathVariable String projectId,
                                                 //  @RequestPart MultipartFile[] relatedFileLinks,
                                                    @RequestBody TaskUpdateDTO taskUpdateDTO) throws IOException {

        return new ResponseEntity<>(taskService.updateStatus(reviewedById,taskId,projectId, taskUpdateDTO),
                HttpStatus.OK);
    }

    @GetMapping("/{projectId}/{taskId}/updatetasks")
    public ResponseEntity<List<TaskUpdateDTO>> getUpdateTasks(@PathVariable String projectId,
                                                              @PathVariable String taskId){
        return new ResponseEntity<>(taskService.getUpdateTasks(projectId,taskId),HttpStatus.OK);

    }
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/{tlId}")
    public ResponseEntity<List<TaskDTO>> getTasks(@PathVariable Integer pageNumber,
                                                  @PathVariable Integer pageSize,
                                                  @PathVariable String sortBy,
                                                  @PathVariable String sortOrder,
                                                  @PathVariable String tlId){

        return new ResponseEntity<>(taskService.getTasks(pageNumber,pageSize,sortBy,sortOrder,tlId),HttpStatus.OK);

    }
    @DeleteMapping("/{projectId}/{taskId}/{historyId}/delete")
    public ResponseEntity<String> deleteTaskHistory(@PathVariable String projectId,
                                                    @PathVariable String taskId,
                                                    @PathVariable Long historyId){
        return new ResponseEntity<>(taskService.deleteTaskHsitory(projectId,taskId,historyId),HttpStatus.OK);
    }



}