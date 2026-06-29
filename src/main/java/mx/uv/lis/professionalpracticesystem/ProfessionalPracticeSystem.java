package mx.uv.lis.professionalpracticesystem;

import java.util.Locale;
import javafx.application.Application;
import mx.uv.lis.professionalpracticesystem.graphicaluserinterface.WelcomeView;
import mx.uv.lis.professionalpracticesystem.logic.utils.LoggingConfigurator;

/**
 *
 * @author andre
 * @author cinth
 */
public class ProfessionalPracticeSystem {

    public static void main(String[] args) {
        LoggingConfigurator.initializeLogger();
        Locale.setDefault(new Locale("es", "MX"));
        
        Application.launch(WelcomeView.class, args);
    }
}
