package com.example.resumeparser.dao;

import com.example.resumeparser.exception.DaoException;
import com.example.resumeparser.model.ParsedResumeData;
import com.example.resumeparser.model.RoleFitness;

import java.util.List;

public interface ResumeDao {
    List<ParsedResumeData> getResumes() throws DaoException;  // Fetch all resumes
    ParsedResumeData getResumeById(int id) throws DaoException;  // Fetch a specific resume
    List<ParsedResumeData> getResumesByFirstname(String firstname) throws DaoException; // Allow searching by first name.
    List<ParsedResumeData> getResumesByLastname(String lastname) throws DaoException; // Allow searching by last name.
    ParsedResumeData getResumesByEmail(String email) throws DaoException; // Allow searching by email.
    List<RoleFitness> getRolesForApplicant(int applicantId) throws DaoException;
    List<ParsedResumeData> getApplicantsForRole(String roleName) throws DaoException;
    ParsedResumeData createParsedResumeData(ParsedResumeData parsedResumeData) throws DaoException;  // Insert applicant and roles
    ParsedResumeData updateParsedResumeData(ParsedResumeData resumeData) throws DaoException;  // Update applicant and roles
    int deleteParsedResumeDataById(int id) throws DaoException;  // Delete an applicant and their roles
}
