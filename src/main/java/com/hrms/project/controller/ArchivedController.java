package com.hrms.project.controller;

import com.hrms.project.entity.Archive;
import com.hrms.project.service.ArchivedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api")
public class ArchivedController {


    @Autowired
    private ArchivedServiceImpl archivedService;

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/terminated/employees")
    public ResponseEntity<List<Archive>>  getTerminatedEmployees(@PathVariable Integer pageSize,
                                                                 @PathVariable Integer pageNumber,
                                                                 @PathVariable String sortBy,
                                                                 @PathVariable String sortOrder) {

        return new ResponseEntity<>(archivedService.getEmployee(pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);

    }
}
