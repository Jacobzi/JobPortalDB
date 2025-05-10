package org.example.oopproject1.cli;

import org.example.oopproject1.model.Job;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.service.JobService;
import org.example.oopproject1.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ShellComponent
public class JobPortalCommands {

    private final JobService jobService;
    private final RecruiterService recruiterService;

    @Autowired
    public JobPortalCommands(JobService jobService, RecruiterService recruiterService) {
        this.jobService = jobService;
        this.recruiterService = recruiterService;
    }

    @ShellMethod(value = "List all jobs", key = "list-jobs")
    public String listJobs() {
        List<Job> jobs = jobService.getAllJobs();
        if (jobs.isEmpty()) {
            return "No jobs found.";
        }

        StringBuilder result = new StringBuilder("Jobs:\n");
        for (Job job : jobs) {
            result.append(String.format("ID: %s, Title: %s, Company: %s, Location: %s\n",
                    job.getId(), job.getTitle(), job.getCompany(), job.getLocation()));
        }
        return result.toString();
    }

    @ShellMethod(value = "Get job details", key = "job-details")
    public String getJobDetails(@ShellOption(value = {"-i", "--id"}) String id) {
        try {
            Job job = jobService.getJobById(id);
            StringBuilder result = new StringBuilder("Job Details:\n");
            result.append(String.format("ID: %s\n", job.getId()));
            result.append(String.format("Title: %s\n", job.getTitle()));
            result.append(String.format("Company: %s\n", job.getCompany()));
            result.append(String.format("Description: %s\n", job.getDescription()));
            result.append(String.format("Salary Range: $%.2f - $%.2f\n", job.getMinSalary(), job.getMaxSalary()));
            result.append(String.format("Location: %s\n", job.getLocation()));
            result.append(String.format("Employment Type: %s\n", job.getEmploymentType()));
            result.append(String.format("Required Skills: %s\n", job.getRequiredSkills()));
            result.append(String.format("Post Date: %s\n", job.getPostDate()));
            result.append(String.format("Deadline: %s\n", job.getDeadlineDate()));
            result.append(String.format("Active: %s\n", job.isActive() ? "Yes" : "No"));
            result.append(String.format("Recruiter ID: %s", job.getRecruiterId()));
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod(value = "List all recruiters", key = "list-recruiters")
    public String listRecruiters() {
        List<Recruiter> recruiters = recruiterService.getAllRecruiters();
        if (recruiters.isEmpty()) {
            return "No recruiters found.";
        }

        StringBuilder result = new StringBuilder("Recruiters:\n");
        for (Recruiter recruiter : recruiters) {
            result.append(String.format("ID: %s, Name: %s, Company: %s\n",
                    recruiter.getId(), recruiter.getName(), recruiter.getCompany()));
        }
        return result.toString();
    }

    @ShellMethod(value = "Create sample job", key = "create-sample-job")
    public String createSampleJob(
            @ShellOption(value = {"-t", "--title"}, defaultValue = "Software Engineer") String title,
            @ShellOption(value = {"-c", "--company"}, defaultValue = "Tech Corp") String company,
            @ShellOption(value = {"-d", "--description"}, defaultValue = "Develop applications") String description,
            @ShellOption(value = {"-min", "--min-salary"}, defaultValue = "50000") Double minSalary,
            @ShellOption(value = {"-max", "--max-salary"}, defaultValue = "80000") Double maxSalary,
            @ShellOption(value = {"-l", "--location"}, defaultValue = "New York") String location,
            @ShellOption(value = {"-e", "--employment-type"}, defaultValue = "Full-time") String employmentType,
            @ShellOption(value = {"-r", "--recruiter-id"}, defaultValue = "") String recruiterId) {

        try {
            Job job = new Job();
            job.setTitle(title);
            job.setCompany(company);
            job.setDescription(description);
            job.setMinSalary(minSalary);
            job.setMaxSalary(maxSalary);
            job.setLocation(location);
            job.setEmploymentType(employmentType);
            job.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB"));
            job.setPostDate(LocalDate.now());
            job.setDeadlineDate(LocalDate.now().plusMonths(1));
            job.setActive(true);
            if (!recruiterId.isEmpty()) {
                job.setRecruiterId(recruiterId);
            }

            Job createdJob = jobService.createJob(job);
            return "Sample job created with ID: " + createdJob.getId();
        } catch (Exception e) {
            return "Error creating job: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Update job status", key = "toggle-job-status")
    public String toggleJobStatus(
            @ShellOption(value = {"-i", "--id"}) String id,
            @ShellOption(value = {"-a", "--active"}, defaultValue = "true") boolean active) {

        try {
            Job job = jobService.getJobById(id);
            job.setActive(active);
            jobService.updateJob(id, job);
            return String.format("Job with ID %s is now %s", id, active ? "active" : "inactive");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Search jobs by keyword", key = "search-jobs")
    public String searchJobs(@ShellOption(value = {"-k", "--keyword"}) String keyword) {
        try {
            List<Job> jobs = jobService.searchJobs(keyword);
            if (jobs.isEmpty()) {
                return "No jobs found matching: " + keyword;
            }

            StringBuilder result = new StringBuilder("Jobs matching '" + keyword + "':\n");
            for (Job job : jobs) {
                result.append(String.format("ID: %s, Title: %s, Company: %s, Location: %s\n",
                        job.getId(), job.getTitle(), job.getCompany(), job.getLocation()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error searching jobs: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Filter jobs by location", key = "filter-by-location")
    public String filterByLocation(@ShellOption(value = {"-l", "--location"}) String location) {
        try {
            List<Job> jobs = jobService.findActiveJobsByLocation(location);
            if (jobs.isEmpty()) {
                return "No active jobs found in location: " + location;
            }

            StringBuilder result = new StringBuilder("Active jobs in " + location + ":\n");
            for (Job job : jobs) {
                result.append(String.format("ID: %s, Title: %s, Company: %s\n",
                        job.getId(), job.getTitle(), job.getCompany()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error filtering jobs: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Filter jobs by company", key = "filter-by-company")
    public String filterByCompany(@ShellOption(value = {"-c", "--company"}) String company) {
        try {
            List<Job> jobs = jobService.findJobsByCompany(company);
            if (jobs.isEmpty()) {
                return "No jobs found for company: " + company;
            }

            StringBuilder result = new StringBuilder("Jobs at " + company + ":\n");
            for (Job job : jobs) {
                result.append(String.format("ID: %s, Title: %s, Location: %s\n",
                        job.getId(), job.getTitle(), job.getLocation()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error filtering jobs: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Filter jobs by title", key = "filter-by-title")
    public String filterByTitle(@ShellOption(value = {"-t", "--title"}) String title) {
        try {
            List<Job> jobs = jobService.findJobsByTitle(title);
            if (jobs.isEmpty()) {
                return "No jobs found with title containing: " + title;
            }

            StringBuilder result = new StringBuilder("Jobs with title containing '" + title + "':\n");
            for (Job job : jobs) {
                result.append(String.format("ID: %s, Title: %s, Company: %s, Location: %s\n",
                        job.getId(), job.getTitle(), job.getCompany(), job.getLocation()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error filtering jobs: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Filter jobs by recruiter", key = "filter-by-recruiter")
    public String filterByRecruiter(@ShellOption(value = {"-r", "--recruiter-id"}) String recruiterId) {
        try {
            List<Job> jobs = jobService.findJobsByRecruiter(recruiterId);
            if (jobs.isEmpty()) {
                return "No jobs found for recruiter with ID: " + recruiterId;
            }

            StringBuilder result = new StringBuilder("Jobs posted by recruiter " + recruiterId + ":\n");
            for (Job job : jobs) {
                result.append(String.format("ID: %s, Title: %s, Company: %s, Location: %s\n",
                        job.getId(), job.getTitle(), job.getCompany(), job.getLocation()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error filtering jobs: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Delete a job", key = "delete-job")
    public String deleteJob(@ShellOption(value = {"-i", "--id"}) String id) {
        try {
            jobService.deleteJob(id);
            return "Job with ID " + id + " deleted successfully";
        } catch (Exception e) {
            return "Error deleting job: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Show help information", key = "job-portal-help")
    public String help() {
        StringBuilder help = new StringBuilder("Job Portal CLI Commands:\n\n");

        help.append("list-jobs                      : List all jobs\n");
        help.append("job-details -i [ID]            : Get detailed information about a specific job\n");
        help.append("list-recruiters                : List all recruiters\n");
        help.append("create-sample-job [options]    : Create a sample job with optional parameters\n");
        help.append("  Options:\n");
        help.append("    -t, --title [TITLE]        : Job title (default: Software Engineer)\n");
        help.append("    -c, --company [COMPANY]    : Company name (default: Tech Corp)\n");
        help.append("    -d, --description [DESC]   : Job description (default: Develop applications)\n");
        help.append("    -min, --min-salary [MIN]   : Minimum salary (default: 50000)\n");
        help.append("    -max, --max-salary [MAX]   : Maximum salary (default: 80000)\n");
        help.append("    -l, --location [LOCATION]  : Job location (default: New York)\n");
        help.append("    -e, --employment-type [TYPE]: Employment type (default: Full-time)\n");
        help.append("    -r, --recruiter-id [ID]    : Recruiter ID (default: none)\n");
        help.append("toggle-job-status -i [ID] [-a true/false] : Activate or deactivate a job\n");
        help.append("search-jobs -k [KEYWORD]       : Search jobs by keyword\n");
        help.append("filter-by-location -l [LOCATION] : Filter active jobs by location\n");
        help.append("filter-by-company -c [COMPANY] : Filter jobs by company name\n");
        help.append("filter-by-title -t [TITLE]     : Filter jobs by title\n");
        help.append("filter-by-recruiter -r [ID]    : Filter jobs by recruiter ID\n");
        help.append("delete-job -i [ID]             : Delete a job\n");
        help.append("job-portal-help                : Show this help information\n");

        return help.toString();
    }
}