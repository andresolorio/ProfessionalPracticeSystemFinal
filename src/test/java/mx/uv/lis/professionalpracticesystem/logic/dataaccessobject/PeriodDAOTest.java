package mx.uv.lis.professionalpracticesystem.logic.dataaccessobject;

import java.sql.SQLException;
import mx.uv.lis.professionalpracticesystem.logic.customexception.DatabaseSystemException;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.DEFAULT_PERIOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @autor andre
 * @author cinth
 */
public class PeriodDAOTest {
    private PeriodDAO periodDAO;

    @BeforeEach
    public void setUp() {
        this.periodDAO = new PeriodDAO();
    }

    @Test
    public void testGetCurrentPeriodFromServerValidExecutionSuccessful() throws DatabaseSystemException, SQLException {
        String currentPeriod = periodDAO.getCurrentPeriodFromServer();
        
        assertEquals(DEFAULT_PERIOD, currentPeriod);
    }
}
