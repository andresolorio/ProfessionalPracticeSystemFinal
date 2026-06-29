package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 
 * @author andre
 * @author cinth
 */
public class TimelineRowAdapterDTO {

    private final SimpleStringProperty activityName;
    private final SimpleStringProperty timeType;
    private final SimpleBooleanProperty weekOne = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekTwo = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekThree = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekFour = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekFive = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekSix = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekSeven = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekEight = 
            new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty editable = 
            new SimpleBooleanProperty(true);

    public TimelineRowAdapterDTO(String name, String type) {
        this.activityName = new SimpleStringProperty(name);
        this.timeType = new SimpleStringProperty(type);
    }

    public boolean isEditable() {
        return this.editable.get();
    }

    public SimpleBooleanProperty editableProperty() {
        return this.editable;
    }

    public void setEditable(boolean val) {
        this.editable.set(val);
    }

    public String getActivityName() {
        return this.activityName.get();
    }

    public SimpleStringProperty activityNameProperty() {
        return this.activityName;
    }

    public String getTimeType() {
        return this.timeType.get();
    }

    public SimpleStringProperty timeTypeProperty() {
        return this.timeType;
    }

    public boolean isWeekOne() {
        return this.weekOne.get();
    }

    public SimpleBooleanProperty weekOneProperty() {
        return this.weekOne;
    }

    public void setWeekOne(boolean val) {
        this.weekOne.set(val);
    }

    public boolean isWeekTwo() {
        return this.weekTwo.get();
    }

    public SimpleBooleanProperty weekTwoProperty() {
        return this.weekTwo;
    }

    public void setWeekTwo(boolean val) {
        this.weekTwo.set(val);
    }

    public boolean isWeekThree() {
        return this.weekThree.get();
    }

    public SimpleBooleanProperty weekThreeProperty() {
        return this.weekThree;
    }

    public void setWeekThree(boolean val) {
        this.weekThree.set(val);
    }

    public boolean isWeekFour() {
        return this.weekFour.get();
    }

    public SimpleBooleanProperty weekFourProperty() {
        return this.weekFour;
    }

    public void setWeekFour(boolean val) {
        this.weekFour.set(val);
    }

    public boolean isWeekFive() {
        return this.weekFive.get();
    }

    public SimpleBooleanProperty weekFiveProperty() {
        return this.weekFive;
    }

    public void setWeekFive(boolean val) {
        this.weekFive.set(val);
    }

    public boolean isWeekSix() {
        return this.weekSix.get();
    }

    public SimpleBooleanProperty weekSixProperty() {
        return this.weekSix;
    }

    public void setWeekSix(boolean val) {
        this.weekSix.set(val);
    }

    public boolean isPlanWeekSeven() {
        return this.weekSeven.get();
    }

    public SimpleBooleanProperty weekSevenProperty() {
        return this.weekSeven;
    }

    public void setWeekSeven(boolean val) {
        this.weekSeven.set(val);
    }

    public boolean isPlanWeekEight() {
        return this.weekEight.get();
    }

    public SimpleBooleanProperty weekEightProperty() {
        return this.weekEight;
    }

    public void setWeekEight(boolean val) {
        this.weekEight.set(val);
    }
    
    public boolean isWeekSeven() {
        return this.weekSeven.get();
    }

    public boolean isWeekEight() {
        return this.weekEight.get();
    }
}