package org.example.oopproject1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs")
@Schema(description = "Job information")
public class Job {
    @Id
    @Schema(description = "Unique job identifier")
    private String id;

    @NotBlank(message = "Title is required")
    @Schema(description = "Job title", example = "Software Engineer")
    private String title;

    @NotBlank(message = "Company is required")
    @Schema(description = "Company name", example = "Tech Corp")
    private String company;

    @NotBlank(message = "Description is required")
    @Schema(description = "Detailed job description")
    private String description;

    @Schema(description = "List of required skills", example = "[\"Java\", \"Spring Boot\", \"MongoDB\"]")
    private List<String> requiredSkills;

    @NotNull(message = "Salary range is required")
    @Schema(description = "Minimum salary", example = "50000")
    private Double minSalary;

    @Schema(description = "Maximum salary", example = "80000")
    private Double maxSalary;

    @NotBlank(message = "Location is required")
    @Schema(description = "Job location", example = "Remote")
    private String location;

    @NotBlank(message = "Employment type is required")
    @Schema(description = "Type of employment", example = "Full-time")
    private String employmentType;

    @Schema(description = "Date when the job was posted")
    private LocalDate postDate;

    @Schema(description = "Application deadline date")
    private LocalDate deadlineDate;

    @Schema(description = "Whether the job posting is active")
    private boolean isActive;

    @Schema(description = "ID of the recruiter who posted the job")
    private String recruiterId;
}