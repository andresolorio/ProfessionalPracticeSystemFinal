package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 
 * @author andre
 * @author cinth
 */
public class PartialTimelineRowAdapterDTO {

    private final SimpleStringProperty activityName;
    private final SimpleStringProperty timeType;
    private final SimpleBooleanProperty editable
            = new SimpleBooleanProperty(true);

    private final SimpleBooleanProperty weekOne = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekTwo = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekThree = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekFour = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekFive = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekSix = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekSeven = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekEight = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekNine = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekTen = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekEleven = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty weekTwelve = new SimpleBooleanProperty(false);

    public PartialTimelineRowAdapterDTO(String name, String type) {
        this.activityName = new SimpleStringProperty(name);
        this.timeType = new SimpleStringProperty(type);
    }

    public boolean isEditable() {
        return editable.get();
    }

    public void setEditable(boolean val) {
        this.editable.set(val);
    }

    public String getActivityName() {
        return activityName.get();
    }

    public String getTimeType() {
        return timeType.get();
    }

    public boolean isWeekOne() {
        return weekOne.get();
    }

    public void setWeekOne(boolean val) {
        this.weekOne.set(val);
    }

    public SimpleBooleanProperty weekOneProperty() {
        return weekOne;
    }

    public boolean isWeekTwo() {
        return weekTwo.get();
    }

    public void setWeekTwo(boolean val) {
        this.weekTwo.set(val);
    }

    public SimpleBooleanProperty weekTwoProperty() {
        return weekTwo;
    }

    public boolean isWeekThree() {
        return weekThree.get();
    }

    public void setWeekThree(boolean val) {
        this.weekThree.set(val);
    }

    public SimpleBooleanProperty weekThreeProperty() {
        return weekThree;
    }

    public boolean isWeekFour() {
        return weekFour.get();
    }

    public void setWeekFour(boolean val) {
        this.weekFour.set(val);
    }

    public SimpleBooleanProperty weekFourProperty() {
        return weekFour;
    }

    public boolean isWeekFive() {
        return weekFive.get();
    }

    public void setWeekFive(boolean val) {
        this.weekFive.set(val);
    }

    public SimpleBooleanProperty weekFiveProperty() {
        return weekFive;
    }

    public boolean isWeekSix() {
        return weekSix.get();
    }

    public void setWeekSix(boolean val) {
        this.weekSix.set(val);
    }

    public SimpleBooleanProperty weekSixProperty() {
        return weekSix;
    }

    public boolean isWeekSeven() {
        return weekSeven.get();
    }

    public void setWeekSeven(boolean val) {
        this.weekSeven.set(val);
    }

    public SimpleBooleanProperty weekSevenProperty() {
        return weekSeven;
    }

    public boolean isWeekEight() {
        return weekEight.get();
    }

    public void setWeekEight(boolean val) {
        this.weekEight.set(val);
    }

    public SimpleBooleanProperty weekEightProperty() {
        return weekEight;
    }

    public boolean isWeekNine() {
        return weekNine.get();
    }

    public void setWeekNine(boolean val) {
        this.weekNine.set(val);
    }

    public SimpleBooleanProperty weekNineProperty() {
        return weekNine;
    }

    public boolean isWeekTen() {
        return weekTen.get();
    }

    public void setWeekTen(boolean val) {
        this.weekTen.set(val);
    }

    public SimpleBooleanProperty weekTenProperty() {
        return weekTen;
    }

    public boolean isWeekEleven() {
        return weekEleven.get();
    }

    public void setWeekEleven(boolean val) {
        this.weekEleven.set(val);
    }

    public SimpleBooleanProperty weekElevenProperty() {
        return weekEleven;
    }

    public boolean isWeekTwelve() {
        return weekTwelve.get();
    }

    public void setWeekTwelve(boolean val) {
        this.weekTwelve.set(val);
    }

    public SimpleBooleanProperty weekTwelveProperty() {
        return weekTwelve;
    }
}
