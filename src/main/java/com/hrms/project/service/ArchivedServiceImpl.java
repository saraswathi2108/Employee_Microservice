package com.hrms.project.service;

import com.hrms.project.entity.Archive;
import com.hrms.project.entity.Employee;
import com.hrms.project.repository.ArchiveRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchivedServiceImpl {
    @Autowired
    private ArchiveRepository archiveRepository;
    @Autowired
    private ModelMapper modelMapper;


    public List<Archive> getEmployee(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        List<Archive> employees=archiveRepository.findAll(pageable).getContent();

        return employees;



    }
}
