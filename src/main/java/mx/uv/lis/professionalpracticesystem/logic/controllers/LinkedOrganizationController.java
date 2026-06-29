package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.LinkedOrganizationDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.LinkedOrganizationDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PHONE_NUMBER_LENGTH;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

public class LinkedOrganizationController {
    private final LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO();

    public int registerLinkedOrganization(LinkedOrganizationDTO organization) throws DataIntegrityException, DatabaseSystemException {
        validateOrganizationData(organization);
        return organizationDAO.saveLinkedOrganization(organization);
    }

    public List<LinkedOrganizationDTO> getAllOrganizations() throws DatabaseSystemException {
        return organizationDAO.getAllLinkedOrganizations();
    }

    public LinkedOrganizationDTO getOrganizationById(int idOrganization) throws EntityNotFoundException, DatabaseSystemException {
        if (idOrganization <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID de la organizacion debe ser un valor positivo.");
        }
        return organizationDAO.getLinkedOrganizationById(idOrganization);
    }

    public int updateLinkedOrganization(LinkedOrganizationDTO organization) throws DataIntegrityException, DatabaseSystemException {
        validateOrganizationData(organization);
        
        if (organization.getIdLinkedOrganization() <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID de la organizacion no es valido para actualizar.");
        }
        
        return organizationDAO.updateLinkedOrganization(organization);
    }

    private void validateOrganizationData(LinkedOrganizationDTO organization) {
        if (!Validator.isNotEmpty(organization.getLinkedOrganizationName())) {
            throw new ValidationException("Error: El nombre de la empresa es obligatorio.");
        }

        if (organization.getLinkedOrganizationName().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Error: El nombre no puede exceder los " + MAX_NAME_LENGTH + " caracteres.");
        }

        if (!Validator.isValidEmail(organization.getEmail())) {
            throw new ValidationException("Error: El formato del correo electronico es invalido.");
        }

        if (!Validator.isValidPhoneNumber(organization.getPhoneNumber())) {
            throw new ValidationException("Error: El numero telefonico debe tener " + PHONE_NUMBER_LENGTH + " digitos.");
        }

        if (!Validator.isNotEmpty(organization.getCity())) {
            throw new ValidationException("Error: El campo de ciudad no puede estar vacio.");
        }
    }
}