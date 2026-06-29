package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ProjectDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

public class ProjectController {
    private final ProjectDAO projectDAO = new ProjectDAO();

    public int registerProject(ProjectDTO project) 
            throws DataIntegrityException, DatabaseSystemException {
        validateProjectData(project);
        
        if (project.getIdLinkedOrganization() <= MIN_VALID_ID) {
            throw new ValidationException("Error: El proyecto debe estar "
                    + "vinculado a una organizacion valida.");
        }

        return projectDAO.saveProject(project);
    }

    public List<ProjectDTO> getAvailableProjects() 
            throws DatabaseSystemException {
        return projectDAO.getAllAvailableProjects();
    }

    public ProjectDTO getProjectById(int idProject) 
            throws EntityNotFoundException, DatabaseSystemException {
        if (idProject <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID del proyecto "
                    + "proporcionado no es valido.");
        }
        return projectDAO.getProjectById(idProject);
    }

    public int updateProject(ProjectDTO project) 
            throws DataIntegrityException, DatabaseSystemException {
        validateProjectData(project);
        
        if (project.getIdProject() <= MIN_VALID_ID) {
            throw new ValidationException("Error: No se puede actualizar un "
                    + "proyecto sin un identificador valido.");
        }

        return projectDAO.updateProject(project);
    }

    private void validateProjectData(ProjectDTO project) {
        if (!Validator.isNotEmpty(project.getProjectName())) {
            throw new ValidationException("Error: El nombre del proyecto "
                    + "es obligatorio.");
        }

        if (project.getProjectName().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Error: El nombre no puede exceder "
                    + "los " + MAX_NAME_LENGTH + " caracteres.");
        }

        if (!Validator.isNotEmpty(project.getDescription())) {
            throw new ValidationException("Error: La descripcion del proyecto "
                    + "es obligatoria.");
        }

        if (!Validator.isNotEmpty(project.getGeneralObjective())) {
            throw new ValidationException("Error: El objetivo general "
                    + "es obligatorio.");
        }

        if (!Validator.isNotEmpty(project.getDuration())) {
            throw new ValidationException("Error: Debe especificar la "
                    + "duracion del proyecto.");
        }

        if (!Validator.isNotEmpty(project.getResponsibilities())) {
            throw new ValidationException("Error: Las responsabilidades "
                    + "son obligatorias.");
        }
    }
    
        public boolean canDeactivateProject(int idProject) throws DatabaseSystemException {
        if (idProject <= MIN_VALID_ID) {
            throw new ValidationException("ID de proyecto no válido.");
        }
        int assignedStudents = projectDAO.getAssignedStudentsCount(idProject);
        return assignedStudents == 0;
    }

    public int deactivateProject(int idProject) throws DatabaseSystemException {
        return projectDAO.deactivateProject(idProject);
    }
    
    public boolean isProjectDeactivatable(int idProject) throws DatabaseSystemException {
        int assignedCount = projectDAO.getAssignedStudentsCount(idProject);
        return assignedCount == 0;
    }

}