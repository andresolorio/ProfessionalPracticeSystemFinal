package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import java.sql.Timestamp;

/**
 *
 * @author cinth
 * @author andre
 */
public class ActivityDTO {

    private int idActivity;
    private String activityName;
    private String description;
    private Timestamp deliveryDate;
    private int idProject;

    public ActivityDTO() {
    }

    public ActivityDTO(int idActivity, String activityName, String description,
            Timestamp deliveryDate, int idProject) {
        this.idActivity = idActivity;
        this.activityName = activityName;
        this.description = description;
        this.deliveryDate = deliveryDate;
        this.idProject = idProject;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }
}
