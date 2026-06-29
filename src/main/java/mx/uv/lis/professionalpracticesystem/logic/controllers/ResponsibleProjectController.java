package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ResponsibleProjectDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ResponsibleProjectDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_POSITION_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

public class ResponsibleProjectController {
    private final ResponsibleProjectDAO responsibleDAO = new ResponsibleProjectDAO();

    public int registerResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException {
        validateResponsibleData(responsible);
        
        if (responsible.getIdLinkedOrganization() <= MIN_VALID_ID) {
            throw new ValidationException("Error: El responsable debe estar asociado a una organizacion vinculada valida.");
        }

        return responsibleDAO.registerResponsible(responsible);
    }

    public List<ResponsibleProjectDTO> getResponsiblesByOrganization(int idOrganization) throws DatabaseSystemException {
        if (idOrganization <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID de la organizacion no es valido.");
        }
        return responsibleDAO.getResponsiblesByOrganization(idOrganization);
    }

    public ResponsibleProjectDTO getResponsibleById(int idResponsible) throws EntityNotFoundException, DatabaseSystemException {
        if (idResponsible <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID del responsable proporcionado no es valido.");
        }
        return responsibleDAO.getResponsibleById(idResponsible);
    }

    public int updateResponsible(ResponsibleProjectDTO responsible) throws DataIntegrityException, DatabaseSystemException {
        validateResponsibleData(responsible);
        
        if (responsible.getIdResponsible() <= MIN_VALID_ID) {
            throw new ValidationException("Error: No se puede actualizar un responsable sin un ID valido.");
        }

        return responsibleDAO.updateResponsible(responsible);
    }

    private void validateResponsibleData(ResponsibleProjectDTO responsible) {
        if (!Validator.isNotEmpty(responsible.getFirstName())) {
            throw new ValidationException("Error: El nombre es obligatorio.");
        }

        if (responsible.getFirstName().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.");
        }

        if (!Validator.isNotEmpty(responsible.getLastName())) {
            throw new ValidationException("Error: El primer apellido es obligatorio.");
        }

        if (!Validator.isNotEmpty(responsible.getPosition())) {
            throw new ValidationException("Error: El cargo del responsable es obligatorio.");
        }

        if (responsible.getPosition().length() > MAX_POSITION_LENGTH) {
            throw new ValidationException("Error: El cargo no puede exceder los " + MAX_POSITION_LENGTH + " caracteres.");
        }
    }
}