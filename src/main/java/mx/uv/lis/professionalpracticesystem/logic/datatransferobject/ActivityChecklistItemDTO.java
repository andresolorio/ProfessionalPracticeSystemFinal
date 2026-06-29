package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import mx.uv.lis.professionalpracticesystem.logic.datatransferobject.ReportActivityDTO;

/**
 * 
 * @author andre
 * @author cinth
 */
public class ActivityChecklistItemDTO {

    private final SimpleBooleanProperty completed;
    private final SimpleStringProperty name;
    private final int activityId;
    private final boolean permanentlyFrozen;

    public ActivityChecklistItemDTO(ReportActivityDTO dto) {
        this.activityId = dto.getActivityId();
        this.name = new SimpleStringProperty(dto.getActivityName());
        this.completed = new SimpleBooleanProperty(dto.isCompleted());
        this.permanentlyFrozen = dto.isAlreadyApprovedPast();
    }

    public SimpleBooleanProperty completedProperty() {
        return this.completed;
    }

    public boolean isCompleted() {
        return this.completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public SimpleStringProperty nameProperty() {
        return this.name;
    }

    public String getName() {
        return this.name.get();
    }

    public int getActivityId() {
        return this.activityId;
    }

    public boolean isPermanentlyFrozen() {
        return this.permanentlyFrozen;
    }
}