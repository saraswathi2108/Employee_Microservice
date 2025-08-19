package com.hrms.project.service;

import com.hrms.project.dto.SkillsDTO;
import com.hrms.project.entity.Achievements;

import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.AchievementsDTO;
import com.hrms.project.repository.AchievementsRepository;
import com.hrms.project.repository.EmployeeRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class AchievementsServiceImpl {

    @Autowired
    private AchievementsRepository achievementsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private S3Service s3Service;



    @Autowired
    private ModelMapper modelMapper;

    public  AchievementsDTO addAchievements(String employeeId,MultipartFile achievementFile, AchievementsDTO achievementsDTO) throws IOException {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        if (achievementsRepository.existsByEmployeeEmployeeIdAndCertificationName(employeeId, achievementsDTO.getCertificationName())) {
            throw new IllegalArgumentException("This certification already exists for the employee.");
        }

        Achievements achievements= modelMapper.map(achievementsDTO,Achievements.class);

        achievements.setEmployee(employee);
        if (achievementFile != null && !achievementFile.isEmpty()) {

            String fileKey = s3Service.uploadDegreeFile(employeeId, "achievements", achievements.getCertificationName(), achievementFile);
            achievements.setAchievementFile(fileKey);
        }


        Achievements saved = achievementsRepository.save(achievements);

        return modelMapper.map(saved, AchievementsDTO.class);
    }

    public List<AchievementsDTO> getCertifications(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        List<Achievements> achievements=employee.getAchievements();
        return achievements.stream().map(achieve->
                modelMapper.map(achieve,AchievementsDTO.class)).toList();
    }

    public AchievementsDTO getAchievement(String achievementId) {

        Achievements achievements=achievementsRepository.findById(achievementId)
                .orElseThrow(()->new APIException("Achievement not found with id " + achievementId));
        return modelMapper.map(achievements,AchievementsDTO.class);

    }

    public AchievementsDTO updateAchievements(String employeeId, String certificateId,
                                              MultipartFile achievementFile, AchievementsDTO achievementDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        if (employee.getAchievements() == null || employee.getAchievements().isEmpty()) {
            throw new RuntimeException("No achievements found for employee " + employeeId);
        }

        Achievements achievementToUpdate = employee.getAchievements().stream()
                .filter(achieve -> achieve.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Achievement with certificate ID " + certificateId + " not found."));

        // Update details from DTO
        achievementToUpdate.setCertificationName(achievementDTO.getCertificationName());
        achievementToUpdate.setIssuingAuthorityName(achievementDTO.getIssuingAuthorityName());
        achievementToUpdate.setCertificationURL(achievementDTO.getCertificationURL());
        achievementToUpdate.setIssueMonth(achievementDTO.getIssueMonth());
        achievementToUpdate.setIssueYear(achievementDTO.getIssueYear());
        achievementToUpdate.setExpirationMonth(achievementDTO.getExpirationMonth());
        achievementToUpdate.setExpirationYear(achievementDTO.getExpirationYear());
        achievementToUpdate.setLicenseNumber(achievementDTO.getLicenseNumber());

        if (achievementFile != null && !achievementFile.isEmpty()) {

            if (achievementToUpdate.getAchievementFile() != null && !achievementToUpdate.getAchievementFile().isEmpty()) {
                s3Service.deleteFile(achievementToUpdate.getAchievementFile());
            }

            String fileKey = s3Service.uploadDegreeFile(employeeId, "achievements", achievementToUpdate.getCertificationName(), achievementFile);
            achievementToUpdate.setAchievementFile(fileKey);
        }

        achievementsRepository.save(achievementToUpdate);

        return modelMapper.map(achievementToUpdate, AchievementsDTO.class);
    }





    public AchievementsDTO deleteAchievements(String employeeId, String certificateId) {


        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        List<Achievements> achievements=employee.getAchievements();

        Achievements achievementToDelete = achievements.stream()
                .filter(achieve -> achieve.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Achievement with certificate ID " + certificateId + " not found."));
        if (achievementToDelete.getAchievementFile() != null && !achievementToDelete.getAchievementFile().isEmpty()) {
            s3Service.deleteFile(achievementToDelete.getAchievementFile());
        }
        achievementsRepository.delete(achievementToDelete);

        return modelMapper.map(achievementToDelete, AchievementsDTO.class);

    }

    public SkillsDTO addSkills(String employeeId, SkillsDTO resumeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        if (employee.getSkills() != null && !employee.getSkills().isEmpty()) {
            throw new APIException("Skills already exist for this employee cannot add again.");
        }
        modelMapper.map(resumeDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, SkillsDTO.class);

    }

    public SkillsDTO updateSkills(String employeeId, SkillsDTO resumeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        modelMapper.map(resumeDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, SkillsDTO.class);

    }

    public SkillsDTO getSkills(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        SkillsDTO resumeDTO = modelMapper.map(employee, SkillsDTO.class);
        resumeDTO.setSkills(employee.getSkills());

        return resumeDTO;
    }

    public SkillsDTO deleteSkills(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        if (employee.getSkills() == null || employee.getSkills().isEmpty()) {
            throw new APIException("No skills found to delete for this employee.");
        }

        employee.setSkills(null);
        employeeRepository.save(employee);

        SkillsDTO resumeDTO = modelMapper.map(employee, SkillsDTO.class);
        resumeDTO.setSkills(employee.getSkills());

        return resumeDTO;
    }


}
