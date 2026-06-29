package mx.uv.lis.professionalpracticesystem.logic.controllers;

import java.util.List;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DataIntegrityException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.EntityNotFoundException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.ValidationException;
import mx.uv.lis.professionalpracticesystem.logic.dataaccessobject.ActivityDAO;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ActivityDTO;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MAX_NAME_LENGTH;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.MIN_VALID_ID;
import mx.uv.lis.professionalpracticesystem.logic.utils.Validator;

public class ActivityController {
    private final ActivityDAO activityDAO = new ActivityDAO();

    public int registerActivity(ActivityDTO activity) throws DataIntegrityException, DatabaseSystemException {
        validateActivityData(activity);
        
        if (activity.getIdProject() <= MIN_VALID_ID) {
            throw new ValidationException("Error: La actividad debe estar asociada a un proyecto valido.");
        }

        return activityDAO.registerActivity(activity);
    }

    public List<ActivityDTO> getActivitiesByProject(int idProject) throws DatabaseSystemException {
        if (idProject <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID del proyecto no es valido.");
        }
        return activityDAO.getActivitiesByIdProject(idProject);
    }

    public int updateActivity(ActivityDTO activity) throws DataIntegrityException, DatabaseSystemException {
        validateActivityData(activity);
        
        if (activity.getIdActivity() <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID de la actividad es invalido.");
        }

        return activityDAO.updateActivity(activity);
    }

    public ActivityDTO getActivityById(int idActivity) throws EntityNotFoundException, DatabaseSystemException {
        if (idActivity <= MIN_VALID_ID) {
            throw new ValidationException("Error: El ID de la actividad no es valido.");
        }
        return activityDAO.getActivityById(idActivity);
    }

    private void validateActivityData(ActivityDTO activity) {
        if (!Validator.isNotEmpty(activity.getActivityName())) {
            throw new ValidationException("Error: El nombre de la actividad es obligatorio.");
        }

        if (activity.getActivityName().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Error: El nombre no puede exceder " + "los " + MAX_NAME_LENGTH + " caracteres.");
        }

        if (!Validator.isNotEmpty(activity.getDescription())) {
            throw new ValidationException("Error: La descripcion de la actividad es obligatoria.");
        }

        if (activity.getDeliveryDate() == null) {
            throw new ValidationException("Error: La fecha de entrega es obligatoria.");
        }
    }
}