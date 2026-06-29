package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.EducativeExperienceDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.EducativeExperienceDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.NRC_PATTERN;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

public class EducativeExperienceController {
    private final EducativeExperienceDAO experienceDAO = new EducativeExperienceDAO();

    public int registerExperienceEducative(EducativeExperienceDTO experience) throws DataIntegrityException, DatabaseSystemException {
        validateExperienceName(experience.getEducativeExperienceName());
        return experienceDAO.registerEducativeExperience(experience);
    }

    public List<EducativeExperienceDTO> getAllExperiences() throws DatabaseSystemException {
        return experienceDAO.getAllEducativeExperiencesWithProfessors();
    }

    public EducativeExperienceDTO getExperienceEducativeByNrc(String nrc) throws EntityNotFoundException, DatabaseSystemException {
        if (nrc == null || !nrc.matches(NRC_PATTERN)) {
            throw new ValidationException("Error: El NRC debe ser un valor numerico.");
        }
        return experienceDAO.getEducativeExperienceWithProfessorByNrc(nrc);
    }

    private void validateExperienceName(String name) {
        if (!Validator.isNotEmpty(name)) {
            throw new ValidationException("Error: El nombre de la experiencia educativa es obligatorio.");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Error: El nombre de la experiencia no puede exceder los " + MAX_NAME_LENGTH + " caracteres.");
        }
    }
}