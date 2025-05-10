package org.example.oopproject1.cli;

import org.example.oopproject1.model.Application;
import org.example.oopproject1.model.Job;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.model.User;
import org.example.oopproject1.service.ApplicationService;
import org.example.oopproject1.service.JobService;
import org.example.oopproject1.service.RecruiterService;
import org.example.oopproject1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@Profile("console")
public class ConsoleMenuRunner implements CommandLineRunner {

    private final JobService jobService;
    private final RecruiterService recruiterService;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final ApplicationContext applicationContext;
    private final AuthenticationManager authenticationManager;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    @Autowired
    public ConsoleMenuRunner(JobService jobService,
                             RecruiterService recruiterService,
                             UserService userService,
                             ApplicationService applicationService,
                             ApplicationContext applicationContext,
                             AuthenticationManager authenticationManager) {
        this.jobService = jobService;
        this.recruiterService = recruiterService;
        this.userService = userService;
        this.applicationService = applicationService;
        this.applicationContext = applicationContext;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting Job Portal Console Interface...");
        showMainMenu();
    }

    private void showMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== Job Portal System =====");
            System.out.println("1. Login");
            System.out.println("2. Register New User");
            System.out.println("3. Browse Jobs (Guest)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    registerUser();
                    break;
                case "3":
                    browseJobsAsGuest();
                    break;
                case "4":
                    running = false;
                    System.out.println("Thank you for using Job Portal System. Goodbye!");
                    exit();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void login() {
        System.out.println("\n===== Login =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            currentUser = (User) authentication.getPrincipal();

            System.out.println("Login successful. Welcome, " + username + "!");

            if (hasRole(currentUser, "ADMIN")) {
                showAdminMenu();
            } else if (hasRole(currentUser, "RECRUITER")) {
                showRecruiterMenu();
            } else {
                showClientMenu();
            }
        } catch (Exception e) {
            System.out.println("Login failed: Invalid credentials");
        }
    }

    private boolean hasRole(User user, String role) {
        return user.getRoles().contains(role);
    }

    private void registerUser() {
        System.out.println("\n===== User Registration =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            userService.registerUser(username, email, password, "USER");
            System.out.println("Registration successful! You can now login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void updateProfile() {
        System.out.println("\n===== Update Profile =====");
        System.out.println("Current Profile Information:");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());

        System.out.println("\nProfile updates are not available in the CLI interface.");
        System.out.println("To update your profile, please use the web interface or contact an administrator.");

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void browseJobsAsGuest() {
        boolean browsing = true;
        while (browsing) {
            System.out.println("\n===== Job Browser (Guest) =====");
            System.out.println("1. View All Jobs");
            System.out.println("2. Search Jobs by Keyword");
            System.out.println("3. Filter Jobs by Location");
            System.out.println("4. Filter Jobs by Company");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllJobs();
                    break;
                case "2":
                    searchJobsByKeyword();
                    break;
                case "3":
                    filterJobsByLocation();
                    break;
                case "4":
                    filterJobsByCompany();
                    break;
                case "5":
                    browsing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showClientMenu() {
        boolean inClientMenu = true;
        while (inClientMenu) {
            System.out.println("\n===== User Menu =====");
            System.out.println("1. View All Jobs");
            System.out.println("2. Search Jobs");
            System.out.println("3. Apply for a Job");
            System.out.println("4. View My Applications");
            System.out.println("5. Update Profile");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllJobs();
                    break;
                case "2":
                    searchJobsByKeyword();
                    break;
                case "3":
                    applyForJob();
                    break;
                case "4":
                    viewMyApplications();
                    break;
                case "5":
                    updateProfile();
                    break;
                case "6":
                    inClientMenu = false;
                    currentUser = null;
                    SecurityContextHolder.clearContext();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showRecruiterMenu() {
        boolean inRecruiterMenu = true;
        while (inRecruiterMenu) {
            System.out.println("\n===== Recruiter Menu =====");
            System.out.println("1. View My Jobs");
            System.out.println("2. Post New Job");
            System.out.println("3. Manage Applications");
            System.out.println("4. Update Profile");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewMyJobs();
                    break;
                case "2":
                    postNewJob();
                    break;
                case "3":
                    manageApplications();
                    break;
                case "4":
                    updateProfile();
                    break;
                case "5":
                    inRecruiterMenu = false;
                    currentUser = null;
                    SecurityContextHolder.clearContext();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showAdminMenu() {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Manage Jobs");
            System.out.println("2. Manage Recruiters");
            System.out.println("3. Manage Users");
            System.out.println("4. View Statistics");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manageJobs();
                    break;
                case "2":
                    manageRecruiters();
                    break;
                case "3":
                    manageUsers();
                    break;
                case "4":
                    viewStatistics();
                    break;
                case "5":
                    inAdminMenu = false;
                    currentUser = null;
                    SecurityContextHolder.clearContext();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Implementation of menu options
    private void displayAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }

        System.out.println("\n===== All Jobs =====");
        for (Job job : jobs) {
            System.out.printf("ID: %s\nTitle: %s\nCompany: %s\nLocation: %s\nSalary: $%.2f - $%.2f\n\n",
                    job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
                    job.getMinSalary(), job.getMaxSalary());
        }
    }

    private void searchJobsByKeyword() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<Job> jobs = jobService.searchJobs(keyword);
        if (jobs.isEmpty()) {
            System.out.println("No jobs found matching: " + keyword);
            return;
        }

        System.out.println("\n===== Search Results for '" + keyword + "' =====");
        for (Job job : jobs) {
            System.out.printf("ID: %s\nTitle: %s\nCompany: %s\nLocation: %s\n\n",
                    job.getId(), job.getTitle(), job.getCompany(), job.getLocation());
        }
    }

    private void filterJobsByLocation() {
        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        List<Job> jobs = jobService.findActiveJobsByLocation(location);
        if (jobs.isEmpty()) {
            System.out.println("No jobs found in location: " + location);
            return;
        }

        System.out.println("\n===== Jobs in " + location + " =====");
        for (Job job : jobs) {
            System.out.printf("ID: %s\nTitle: %s\nCompany: %s\n\n",
                    job.getId(), job.getTitle(), job.getCompany());
        }
    }

    private void filterJobsByCompany() {
        System.out.print("Enter company name: ");
        String company = scanner.nextLine();

        List<Job> jobs = jobService.findJobsByCompany(company);
        if (jobs.isEmpty()) {
            System.out.println("No jobs found for company: " + company);
            return;
        }

        System.out.println("\n===== Jobs at " + company + " =====");
        for (Job job : jobs) {
            System.out.printf("ID: %s\nTitle: %s\nLocation: %s\n\n",
                    job.getId(), job.getTitle(), job.getLocation());
        }
    }

    private void applyForJob() {
        System.out.print("Enter Job ID to apply for: ");
        String jobId = scanner.nextLine();

        try {
            Job job = jobService.getJobById(jobId);
            System.out.println("\nJob Details:");
            System.out.printf("Title: %s\nCompany: %s\nLocation: %s\n\n",
                    job.getTitle(), job.getCompany(), job.getLocation());

            System.out.println("Do you want to apply for this job? (y/n)");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                // Create a new application
                Application application = new Application();
                application.setJobId(jobId);

                // Set candidate information based on current user
                application.setCandidateName(currentUser.getUsername());
                application.setEmail(currentUser.getEmail());

                // Get phone number
                System.out.print("Enter your phone number: ");
                String phone = scanner.nextLine();
                application.setPhone(phone);

                // Prompt for a cover letter
                System.out.println("Enter a short cover letter or message (press Enter when done):");
                String coverLetter = scanner.nextLine();
                application.setCoverLetterText(coverLetter);

                // Set status to SUBMITTED
                application.setStatus(Application.ApplicationStatus.SUBMITTED);

                // The applicationDate will be set by the ApplicationService

                // Save the application
                applicationService.createApplication(application);
                System.out.println("Application submitted successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewMyApplications() {
        try {
            // Get the current user's email
            String userEmail = currentUser.getEmail();

            // Get applications by email
            List<Application> applications = applicationService.getApplicationsByEmail(userEmail);

            if (applications.isEmpty()) {
                System.out.println("You haven't submitted any applications yet.");
                return;
            }

            System.out.println("\n===== My Applications =====");
            for (Application app : applications) {
                Job job = jobService.getJobById(app.getJobId());
                System.out.printf("Application ID: %s\nJob: %s at %s\nStatus: %s\nApplied Date: %s\n\n",
                        app.getId(), job.getTitle(), job.getCompany(), app.getStatus(), app.getApplicationDate());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewMyJobs() {
        // Find recruiter by email
        try {
            Recruiter recruiter = recruiterService.getRecruiterByEmail(currentUser.getEmail());
            if (recruiter != null) {
                List<Job> jobs = jobService.findJobsByRecruiter(recruiter.getId());

                if (jobs.isEmpty()) {
                    System.out.println("You haven't posted any jobs yet.");
                    return;
                }

                System.out.println("\n===== My Posted Jobs =====");
                for (Job job : jobs) {
                    System.out.printf("ID: %s\nTitle: %s\nCompany: %s\nLocation: %s\n\n",
                            job.getId(), job.getTitle(), job.getCompany(), job.getLocation());
                }
            } else {
                System.out.println("No recruiter profile found for your account.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void postNewJob() {
        System.out.println("\n===== Post New Job =====");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Company: ");
        String company = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Minimum Salary: ");
        double minSalary = Double.parseDouble(scanner.nextLine());
        System.out.print("Maximum Salary: ");
        double maxSalary = Double.parseDouble(scanner.nextLine());
        System.out.print("Location: ");
        String location = scanner.nextLine();
        System.out.print("Employment Type: ");
        String employmentType = scanner.nextLine();
        System.out.print("Required Skills (comma-separated): ");
        String skillsInput = scanner.nextLine();
        List<String> requiredSkills = Arrays.asList(skillsInput.split(","));
        System.out.print("Deadline (YYYY-MM-DD): ");
        String deadlineStr = scanner.nextLine();

        try {
            // Find recruiter by email
            Recruiter recruiter = recruiterService.getRecruiterByEmail(currentUser.getEmail());

            if (recruiter == null) {
                System.out.println("No recruiter profile found for your account.");
                return;
            }

            if (!recruiter.getCompany().equals(company)) {
                System.out.println("Error: You can only post jobs for your company: " + recruiter.getCompany());
                return;
            }

            Job job = new Job();
            job.setTitle(title);
            job.setCompany(company);
            job.setDescription(description);
            job.setMinSalary(minSalary);
            job.setMaxSalary(maxSalary);
            job.setLocation(location);
            job.setEmploymentType(employmentType);
            job.setRequiredSkills(requiredSkills);
            job.setPostDate(LocalDate.now());
            job.setRecruiterId(recruiter.getId());

            if (!deadlineStr.isEmpty()) {
                job.setDeadlineDate(LocalDate.parse(deadlineStr));
            } else {
                job.setDeadlineDate(LocalDate.now().plusMonths(1));
            }

            job.setActive(true);
            job.setRecruiterId(recruiter.getId());

            Job createdJob = jobService.createJob(job);
            System.out.println("Job posted successfully with ID: " + createdJob.getId());
        } catch (Exception e) {
            System.out.println("Error posting job: " + e.getMessage());
        }
    }

    private void manageApplications() {
        try {
            // Find recruiter by email
            Recruiter recruiter = recruiterService.getRecruiterByEmail(currentUser.getEmail());
            if (recruiter == null) {
                System.out.println("No recruiter profile found for your account.");
                return;
            }

            // Get jobs posted by this recruiter
            List<Job> recruiterJobs = jobService.findJobsByRecruiter(recruiter.getId());
            if (recruiterJobs.isEmpty()) {
                System.out.println("You haven't posted any jobs yet.");
                return;
            }

            // Get job IDs
            List<String> jobIds = new java.util.ArrayList<>();
            for (Job job : recruiterJobs) {
                jobIds.add(job.getId());
            }

            // For each job, get and process applications
            boolean hasApplications = false;

            System.out.println("\n===== Applications for Your Jobs =====");

            for (Job job : recruiterJobs) {
                List<Application> jobApplications = applicationService.getApplicationsByJobId(job.getId());

                if (!jobApplications.isEmpty()) {
                    hasApplications = true;
                    System.out.printf("\n--- Applications for: %s ---\n", job.getTitle());

                    for (Application app : jobApplications) {
                        System.out.printf("ID: %s | Candidate: %s | Email: %s | Status: %s | Date: %s\n",
                                app.getId(), app.getCandidateName(), app.getEmail(),
                                app.getStatus(), app.getApplicationDate());

                        System.out.println("1. View Details  2. Update Status  3. Next Application");
                        System.out.print("Enter choice (or any other key to skip): ");
                        String choice = scanner.nextLine();

                        if (choice.equals("1")) {
                            viewApplicationDetails(app);
                        } else if (choice.equals("2")) {
                            updateApplicationStatus(app);
                        }
                    }
                }
            }

            if (!hasApplications) {
                System.out.println("No applications received for your jobs yet.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewApplicationDetails(Application app) {
        System.out.println("\n----- Application Details -----");
        System.out.println("Candidate: " + app.getCandidateName());
        System.out.println("Email: " + app.getEmail());
        System.out.println("Phone: " + app.getPhone());
        System.out.println("Status: " + app.getStatus());
        System.out.println("Applied on: " + app.getApplicationDate());

        if (app.getCoverLetterText() != null && !app.getCoverLetterText().isEmpty()) {
            System.out.println("\nCover Letter:");
            System.out.println(app.getCoverLetterText());
        }

        if (app.getResumeUrl() != null && !app.getResumeUrl().isEmpty()) {
            System.out.println("\nResume URL: " + app.getResumeUrl());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateApplicationStatus(Application app) {
        System.out.println("\nCurrent Status: " + app.getStatus());
        System.out.println("Select new status:");
        System.out.println("1. SUBMITTED");
        System.out.println("2. REVIEWING");
        System.out.println("3. INTERVIEWED");
        System.out.println("4. ACCEPTED");
        System.out.println("5. REJECTED");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine();
        Application.ApplicationStatus newStatus;

        switch (choice) {
            case "1": newStatus = Application.ApplicationStatus.SUBMITTED; break;
            case "2": newStatus = Application.ApplicationStatus.REVIEWING; break;
            case "3": newStatus = Application.ApplicationStatus.INTERVIEWED; break;
            case "4": newStatus = Application.ApplicationStatus.ACCEPTED; break;
            case "5": newStatus = Application.ApplicationStatus.REJECTED; break;
            default:
                System.out.println("Invalid choice. Status not updated.");
                return;
        }

        app.setStatus(newStatus);
        applicationService.updateApplication(app.getId(), app);
        System.out.println("Application status updated to " + newStatus);
    }

    private void manageJobs() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n===== Manage Jobs =====");
            System.out.println("1. View All Jobs");
            System.out.println("2. Add New Job");
            System.out.println("3. Update Job");
            System.out.println("4. Delete Job");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllJobs();
                    break;
                case "2":
                    addNewJob();
                    break;
                case "3":
                    updateJob();
                    break;
                case "4":
                    deleteJob();
                    break;
                case "5":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addNewJob() {
        System.out.println("\n===== Add New Job =====");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Company: ");
        String company = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Minimum Salary: ");
        double minSalary = Double.parseDouble(scanner.nextLine());
        System.out.print("Maximum Salary: ");
        double maxSalary = Double.parseDouble(scanner.nextLine());
        System.out.print("Location: ");
        String location = scanner.nextLine();
        System.out.print("Employment Type: ");
        String employmentType = scanner.nextLine();
        System.out.print("Required Skills (comma-separated): ");
        String skillsInput = scanner.nextLine();
        List<String> requiredSkills = Arrays.asList(skillsInput.split(","));
        System.out.print("Deadline (YYYY-MM-DD): ");
        String deadlineStr = scanner.nextLine();

        try {
            Job job = new Job();
            job.setTitle(title);
            job.setCompany(company);
            job.setDescription(description);
            job.setMinSalary(minSalary);
            job.setMaxSalary(maxSalary);
            job.setLocation(location);
            job.setEmploymentType(employmentType);
            job.setRequiredSkills(requiredSkills);
            job.setPostDate(LocalDate.now());

            if (!deadlineStr.isEmpty()) {
                job.setDeadlineDate(LocalDate.parse(deadlineStr));
            } else {
                job.setDeadlineDate(LocalDate.now().plusMonths(1));
            }

            job.setActive(true);

            Job createdJob = jobService.createJob(job);
            System.out.println("Job created successfully with ID: " + createdJob.getId());
        } catch (Exception e) {
            System.out.println("Error creating job: " + e.getMessage());
        }
    }

    private void updateJob() {
        System.out.print("Enter Job ID to update: ");
        String id = scanner.nextLine();

        try {
            Job job = jobService.getJobById(id);

            System.out.println("\nCurrent Job Details:");
            System.out.println("Title: " + job.getTitle());
            System.out.println("Company: " + job.getCompany());
            System.out.println("Description: " + job.getDescription());
            System.out.println("Salary Range: $" + job.getMinSalary() + " - $" + job.getMaxSalary());
            System.out.println("Location: " + job.getLocation());
            System.out.println("Employment Type: " + job.getEmploymentType());
            System.out.println("Active: " + (job.isActive() ? "Yes" : "No"));

            System.out.println("\nEnter new details (leave blank to keep current value):");

            System.out.print("Title: ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) job.setTitle(title);

            System.out.print("Company: ");
            String company = scanner.nextLine();
            if (!company.isEmpty()) job.setCompany(company);

            System.out.print("Description: ");
            String description = scanner.nextLine();
            if (!description.isEmpty()) job.setDescription(description);

            System.out.print("Minimum Salary: ");
            String minSalaryStr = scanner.nextLine();
            if (!minSalaryStr.isEmpty()) job.setMinSalary(Double.parseDouble(minSalaryStr));

            System.out.print("Maximum Salary: ");
            String maxSalaryStr = scanner.nextLine();
            if (!maxSalaryStr.isEmpty()) job.setMaxSalary(Double.parseDouble(maxSalaryStr));

            System.out.print("Location: ");
            String location = scanner.nextLine();
            if (!location.isEmpty()) job.setLocation(location);

            System.out.print("Employment Type: ");
            String employmentType = scanner.nextLine();
            if (!employmentType.isEmpty()) job.setEmploymentType(employmentType);

            System.out.print("Active (true/false): ");
            String activeStr = scanner.nextLine();
            if (!activeStr.isEmpty()) job.setActive(Boolean.parseBoolean(activeStr));

            Job updatedJob = jobService.updateJob(id, job);
            System.out.println("Job updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating job: " + e.getMessage());
        }
    }

    private void deleteJob() {
        System.out.print("Enter Job ID to delete: ");
        String id = scanner.nextLine();

        try {
            jobService.deleteJob(id);
            System.out.println("Job deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting job: " + e.getMessage());
        }
    }

    private void manageRecruiters() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n===== Manage Recruiters =====");
            System.out.println("1. View All Recruiters");
            System.out.println("2. Add New Recruiter");
            System.out.println("3. Update Recruiter");
            System.out.println("4. Delete Recruiter");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllRecruiters();
                    break;
                case "2":
                    addNewRecruiter();
                    break;
                case "3":
                    updateRecruiter();
                    break;
                case "4":
                    deleteRecruiter();
                    break;
                case "5":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllRecruiters() {
        List<Recruiter> recruiters = recruiterService.getAllRecruiters();
        if (recruiters.isEmpty()) {
            System.out.println("No recruiters found.");
            return;
        }

        System.out.println("\n===== All Recruiters =====");
        for (Recruiter recruiter : recruiters) {
            System.out.printf("ID: %s\nName: %s\nCompany: %s\nEmail: %s\n\n",
                    recruiter.getId(), recruiter.getName(), recruiter.getCompany(), recruiter.getEmail());
        }
    }

    private void manageUsers() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n===== Manage Users =====");
            System.out.println("1. View All Users");
            System.out.println("2. Enable/Disable User");
            System.out.println("3. Back to Admin Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllUsers();
                    break;
                case "2":
                    toggleUserStatus();
                    break;
                case "3":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n===== All Users =====");
        for (User user : users) {
            System.out.printf("ID: %s\nUsername: %s\nEmail: %s\nRoles: %s\nEnabled: %s\n\n",
                    user.getId(), user.getUsername(), user.getEmail(), user.getRoles(),
                    user.isEnabled() ? "Yes" : "No");
        }
    }

    private void toggleUserStatus() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();

        try {
            // Using the getUserById method from your UserService
            User user = userService.getUserById(id);
            boolean currentStatus = user.isEnabled();

            System.out.printf("User %s is currently %s. Toggle to %s? (y/n): ",
                    user.getUsername(), currentStatus ? "enabled" : "disabled",
                    !currentStatus ? "enabled" : "disabled");

            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                userService.updateUserStatus(id, !currentStatus);
                System.out.println("User status updated successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewStatistics() {
        Map<String, Object> stats = userService.getDashboardStats();

        System.out.println("\n===== System Statistics =====");
        System.out.println("Total Users: " + stats.get("totalUsers"));
        System.out.println("Total Jobs: " + stats.get("totalJobs"));
        System.out.println("Total Applications: " + stats.get("totalApplications"));
        System.out.println("Total Recruiters: " + stats.get("totalRecruiters"));
    }

    private void exit() {
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }

    private void addNewRecruiter() {
        System.out.println("\n===== Add New Recruiter =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Company: ");
        String company = scanner.nextLine();
        System.out.print("Position: ");
        String position = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        try {
            // Register user with RECRUITER role
            User user = userService.registerUser(username, email, password, "RECRUITER");

            // Create recruiter profile
            Recruiter recruiter = new Recruiter();
            recruiter.setName(username);
            recruiter.setEmail(email);
            recruiter.setCompany(company);
            recruiter.setPosition(position);
            recruiter.setPhone(phone);

            // Note: There's no direct link to the User entity in your model

            recruiterService.createRecruiter(recruiter);
            System.out.println("Recruiter added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding recruiter: " + e.getMessage());
        }
    }

    private void updateRecruiter() {
        System.out.print("Enter Recruiter ID: ");
        String id = scanner.nextLine();

        try {
            Recruiter recruiter = recruiterService.getRecruiterById(id);

            System.out.println("\nCurrent Recruiter Details:");
            System.out.println("Name: " + recruiter.getName());
            System.out.println("Email: " + recruiter.getEmail());
            System.out.println("Company: " + recruiter.getCompany());
            System.out.println("Position: " + recruiter.getPosition());
            System.out.println("Phone: " + recruiter.getPhone());

            System.out.println("\nEnter new details (leave blank to keep current value):");

            System.out.print("Name: ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) recruiter.setName(name);

            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) recruiter.setEmail(email);

            System.out.print("Company: ");
            String company = scanner.nextLine();
            if (!company.isEmpty()) recruiter.setCompany(company);

            System.out.print("Position: ");
            String position = scanner.nextLine();
            if (!position.isEmpty()) recruiter.setPosition(position);

            System.out.print("Phone: ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) recruiter.setPhone(phone);

            recruiterService.updateRecruiter(id, recruiter);
            System.out.println("Recruiter updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating recruiter: " + e.getMessage());
        }
    }

    private void deleteRecruiter() {
        System.out.print("Enter Recruiter ID: ");
        String id = scanner.nextLine();

        try {
            recruiterService.deleteRecruiter(id);
            System.out.println("Recruiter deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting recruiter: " + e.getMessage());
        }
    }
}