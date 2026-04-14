package com.example.resumeparser.dao;

import com.example.resumeparser.exception.DaoException;
import com.example.resumeparser.model.ParsedResumeData;
import com.example.resumeparser.model.RoleFitness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcResumeDao implements ResumeDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcResumeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ParsedResumeData createParsedResumeData(ParsedResumeData resume) {
        try {
            // Step 1: Insert Applicant
            String applicantSql = "INSERT INTO applicant (firstname, lastname, email) VALUES (?, ?, ?) RETURNING id;";
            int applicantId = jdbcTemplate.queryForObject(applicantSql, Integer.class,
                    resume.getFirstname(), resume.getLastname(), resume.getEmail());

            // Step 2: Insert Roles and Link to Applicant
            for (RoleFitness roleFitness : resume.getRoleFitnessList()) {
                int roleId = getOrInsertRole(roleFitness.getRoleName());
                linkApplicantToRole(applicantId, roleId, roleFitness.getFitness());
            }

            // Retrieve and return the saved applicant
            return getResumeById(applicantId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("unique constraint") || e.getMessage().contains("email")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "An applicant with this email already exists.");
            }
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public List<ParsedResumeData> getResumes() {
        List<ParsedResumeData> resumes = new ArrayList<>();
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a 
            ;
            """;

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                ParsedResumeData resume = mapRowToResume(results);
                resumes.add(resume);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resumes;
    }

    @Override
    public ParsedResumeData getResumeById(int id) {
        ParsedResumeData resume = null;
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a 
            WHERE id = ?
            ;
            """;

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()) {
                resume = mapRowToResume(result);
                resume.setRoleFitnessList(getRolesForApplicant(id));  // Fetch roles
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resume;
    }

    @Override
    public List<ParsedResumeData> getResumesByFirstname(String firstname) {
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a 
            WHERE firstname ILIKE ?
            ;
            """;
        List<ParsedResumeData> resumes = new ArrayList<>();

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, firstname);
            while (results.next()) {
                ParsedResumeData resume = mapRowToResume(results);
                resumes.add(resume);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resumes;
    }

    @Override
    public List<ParsedResumeData> getResumesByLastname(String lastname) {
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a 
            WHERE lastname ILIKE ?
            ;
            """;
        List<ParsedResumeData> resumes = new ArrayList<>();

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, lastname);
            while (results.next()) {
                ParsedResumeData resume = mapRowToResume(results);
                resumes.add(resume);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resumes;
    }

    @Override
    public ParsedResumeData getResumesByEmail(String email) {
        ParsedResumeData resume = null;
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a 
            WHERE email ILIKE ?
            ;
            """;

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, email);
            if (result.next()) {
                resume = mapRowToResume(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resume;
    }

    @Override
    public List<RoleFitness> getRolesForApplicant(int applicantId) {
        List<RoleFitness> roles = new ArrayList<>();
        String sql = """
            SELECT r.role_name, ar.fitness
            FROM roles r
            JOIN applicant_role ar ON r.id = ar.role_id
            WHERE ar.applicant_id = ?;
        """;

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, applicantId);
        while (results.next()) {
            roles.add(new RoleFitness(
                    results.getString("role_name"),
                    results.getInt("fitness")
            ));
        }
        return roles;
    }

    @Override
    public List<ParsedResumeData> getApplicantsForRole(String roleName) {
        List<ParsedResumeData> resumes = new ArrayList<>();
        String sql = """
            SELECT a.id, a.firstname, a.lastname, a.email
            FROM applicant a
            JOIN applicant_role ar ON a.id = ar.applicant_id
            JOIN roles r ON ar.role_id = r.id
            WHERE r.role_name ILIKE ?;
        """;

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, roleName);
            while (results.next()) {
                ParsedResumeData resume = mapRowToResume(results);
                resumes.add(resume);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return resumes;
    }

    @Override
    public ParsedResumeData updateParsedResumeData(ParsedResumeData resume) {
        try {
            // Step 1: Update Applicant Information
            String sql = """
                UPDATE applicant 
                SET firstname = ?, lastname = ?, email = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?;
                """;
            int rowsAffected = jdbcTemplate.update(sql, resume.getFirstname(), resume.getLastname(),
                    resume.getEmail(), resume.getId());

            if (rowsAffected == 0) {
                throw new DaoException("No applicant found with the given ID.");
            }

            // Step 2: Update Roles (delete old and insert new)
            String deleteSql = "DELETE FROM applicant_role WHERE applicant_id = ?;";
            jdbcTemplate.update(deleteSql, resume.getId());

            for (RoleFitness roleFitness : resume.getRoleFitnessList()) {
                int roleId = getOrInsertRole(roleFitness.getRoleName());
                linkApplicantToRole(resume.getId(), roleId, roleFitness.getFitness());
            }

            return getResumeById(resume.getId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int deleteParsedResumeDataById(int id) {
        try {
            // Step 1: Delete Roles First (Foreign Key Constraint)
            String deleteRolesSql = "DELETE FROM applicant_role WHERE applicant_id = ?;";
            jdbcTemplate.update(deleteRolesSql, id);

            // Step 2: Delete Applicant
            String deleteApplicantSql = "DELETE FROM applicant WHERE id = ?;";
            return jdbcTemplate.update(deleteApplicantSql, id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private int getOrInsertRole(String roleName) {
        // Try to find the role first
        String selectSql = "SELECT id FROM roles WHERE role_name = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(selectSql, roleName);

        if (result.next()) {
            return result.getInt("id");
        } else {
            // If role doesn't exist, insert it
            String insertSql = "INSERT INTO roles (role_name) VALUES (?) RETURNING id;";
            return jdbcTemplate.queryForObject(insertSql, Integer.class, roleName);
        }
    }

    private void linkApplicantToRole(int applicantId, int roleId, int fitness) {
        String sql = "INSERT INTO applicant_role (applicant_id, role_id, fitness) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, applicantId, roleId, fitness);
    }

    private ParsedResumeData mapRowToResume(SqlRowSet result) {
        ParsedResumeData prd = new ParsedResumeData();
        prd.setId(result.getInt("id"));
        prd.setFirstname(result.getString("firstname"));
        prd.setLastname(result.getString("lastname"));
        prd.setEmail(result.getString("email"));
        prd.setRoleFitnessList(getRolesForApplicant(result.getInt("id")));  // Fetch roles
        return prd;
    }
}
