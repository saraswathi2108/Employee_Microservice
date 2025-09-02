package com.hrms.project.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    @NotBlank(message = "Project Id is required")
    @Pattern(regexp = "^PRO\\d{4}$", message = "Project ID must start with 'PRO' followed by 4 digits")
    private String projectId;

    @NotBlank(message = "Project title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 50, message = "Client name must be between 2 and 50 characters")
    private String client;

    @Size(max = 255, message = "Description can be up to 255 characters")
    private String description;

    @NotBlank(message = "Project priority is required")
    @Pattern(regexp = "^(High|Medium|Low)$", message = "Project priority must be High, Medium, or Low")
    private String projectPriority;

    @NotBlank(message = "Project status is required")
    @Pattern(regexp = "^(Not Started|In Progress|Completed|On Hold)$", message = "Invalid project status")
    private String projectStatus;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

}
