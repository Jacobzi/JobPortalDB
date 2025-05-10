package org.example.oopproject1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applications")
public class Application {
    @Id
    private String id;

    @NotBlank(message = "Job ID is required")
    private String jobId;

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String resumeUrl;
    private String coverLetterText;
    private LocalDate applicationDate;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    public enum ApplicationStatus {
        SUBMITTED, REVIEWING, INTERVIEWED, REJECTED, ACCEPTED
    }
}