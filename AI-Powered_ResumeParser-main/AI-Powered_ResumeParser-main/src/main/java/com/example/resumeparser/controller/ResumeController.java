package com.example.resumeparser.controller;

import com.example.resumeparser.dao.ResumeDao;
import com.example.resumeparser.model.ParsedResumeData;
import com.example.resumeparser.model.ResumeRequest;
import com.example.resumeparser.model.RoleFitness;
import com.example.resumeparser.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class ResumeController {

    private final OpenAiService openAiService;
    private final ResumeDao resumeDao;

    @Autowired
    public ResumeController(OpenAiService openAiService, ResumeDao resumeDao) {
        this.openAiService = openAiService;
        this.resumeDao = resumeDao;
    }

    @PostMapping("/applicant")
    public ParsedResumeData parseResume(@RequestBody ResumeRequest request) {
        String resumeText = request.getResumeText();

        ParsedResumeData parsedData = openAiService.parseResumeWithOpenAi(resumeText);

        ParsedResumeData savedData = resumeDao.createParsedResumeData(parsedData);

        return savedData;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicants")
    public List<ParsedResumeData> getAllResumes() {
        return resumeDao.getResumes();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/roles/{applicantId}")
    public List<RoleFitness> getRolesForApplicant(@PathVariable int applicantId) {
        return resumeDao.getRolesForApplicant(applicantId);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicants/role/{roleName}")
    public List<ParsedResumeData> getApplicantForRole(@PathVariable String roleName) {
        return resumeDao.getApplicantsForRole(roleName);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicant/{id}")
    public ParsedResumeData getResumeById(@PathVariable int id) {
        ParsedResumeData resume = resumeDao.getResumeById(id);
        if (resume == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found");
        }
        return resume;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicant/searchByFirstname")
    public List<ParsedResumeData> searchResumesByFirstname(@RequestParam String firstname) {
        List<ParsedResumeData> resumes = resumeDao.getResumesByFirstname(firstname);
        if (resumes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applicant found with the given firstname");
        }
        return resumes;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicant/searchByLastname")
    public List<ParsedResumeData> searchResumesByLastname(@RequestParam String lastname) {
        List<ParsedResumeData> resumes = resumeDao.getResumesByLastname(lastname);
        if (resumes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applicant found with the given lastname");
        }
        return resumes;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/applicant/searchByEmail")
    public ParsedResumeData searchResumesByEmail(@RequestParam String email) {
        ParsedResumeData resume = resumeDao.getResumesByEmail(email);
        if (resume == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applicant found with the given email address");
        }
        return resume;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/applicant/id/{id}")
    public ParsedResumeData updateResume(@PathVariable int id, @RequestBody ParsedResumeData updatedResume) {
        updatedResume.setId(id);
        ParsedResumeData result = resumeDao.updateParsedResumeData(updatedResume);

        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found or update failed");
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/applicant/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable int id) {
        int rowsAffected = resumeDao.deleteParsedResumeDataById(id);

        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found or already deleted");
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/health")
    public String healthCheck() { // Monitoring the API status.
        return "Resume Parser API is running!";
    }

}
