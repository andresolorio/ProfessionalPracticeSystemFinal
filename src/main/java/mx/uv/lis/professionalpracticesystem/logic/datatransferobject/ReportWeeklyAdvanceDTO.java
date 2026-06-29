package mx.uv.lis.professionalpracticesystem.logic.datatransferobject;

/**
 *
 * @author andre
 */
public class ReportWeeklyAdvanceDTO {

    private int idReport;
    private int idActivity;
    private String registrationType;
    private boolean weekOne;
    private boolean weekTwo;
    private boolean weekThree;
    private boolean weekFour;

    public ReportWeeklyAdvanceDTO() {
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public boolean isWeekOne() {
        return weekOne;
    }

    public void setWeekOne(boolean weekOne) {
        this.weekOne = weekOne;
    }

    public boolean isWeekTwo() {
        return weekTwo;
    }

    public void setWeekTwo(boolean weekTwo) {
        this.weekTwo = weekTwo;
    }

    public boolean isWeekThree() {
        return weekThree;
    }

    public void setWeekThree(boolean weekThree) {
        this.weekThree = weekThree;
    }

    public boolean isWeekFour() {
        return weekFour;
    }

    public void setWeekFour(boolean weekFour) {
        this.weekFour = weekFour;
    }
}
