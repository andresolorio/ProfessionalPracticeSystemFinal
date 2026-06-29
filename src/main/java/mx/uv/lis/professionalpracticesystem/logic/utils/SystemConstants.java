package mx.uv.lis.professionalpracticesystem.logic.utils;

import java.time.LocalDate;

/**
 * 
 * @author andre
 */

public final class SystemConstants {

    private SystemConstants() {
    }
    
    //PasswordManager
    public static final String HASH_ALGORITHM = "SHA-256";
    public static final int MIN_PASSWORD_LENGTH = 10;
    public static final String PASSWORD_COMPLEXITY_REGEX = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=(.*\\d){2,}).{" + MIN_PASSWORD_LENGTH + ",}$";

    //Validator
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String PHONE_PATTERN = "^[0-9]{10}$";
    public static final String ENROLLMENT_PATTERN = "^S[0-9]{8}$";
    public static final String STAFF_NUMBER_PATTERN = "^[0-9]{6}$";
    public static final String NAME_PATTERN = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$";
    public static final String NRC_PATTERN = "^[0-9]{5}$";
    public static final float MIN_GRADE = 0.0f;
    public static final float MAX_GRADE = 10.0f;
    public static final int MIN_CREDITS = 0;
    public static final int MAX_CREDITS = 365;
    public static final int ZERO_THRESHOLD = 0;
    public static final double BASE64_SIZE_FACTOR = 0.75;
    
    //Monthly Report
    public static final String PATH_LOGO_UV = "/images/LogoUV.png";
    public static final String PATH_LOGO_LIS = "/images/LogoLIS.png";
    public static final float PDF_LOGO_SCALE_SIZE = 55f;
    public static final int PDF_WEEKS_PER_MONTH = 4;
    public static final int PDF_EVALUATION_MAX_SCORE = 5; 
    public static final float[] WIDTHS_PERCENT_HEADER_IMAGES = {15f, 70f, 15f};
    public static final float[] WIDTHS_PERCENT_GENERAL_INFO = {2f, 4f, 2f, 4f};
    public static final float[] WIDTHS_PERCENT_STUDENT_INFO = {2f, 4f};
    public static final float[] WIDTHS_PERCENT_ACTIVITY_GRID = {40f, 12f, 12f, 12f, 12f, 12f};
    public static final float[] WIDTHS_PERCENT_CRITERIA_GRID = {70f, 30f};
    public static final float[] WIDTHS_PERCENT_EVALUATION_GRID = {10f, 65f, 5f, 5f, 5f, 5f, 5f};
    public static final float[] WIDTHS_PERCENT_SIGNATURES_GRID = {50f, 50f};
    
    //Partial Report
    public static final int PDF_WEEKS_PER_PARTIAL_REPORT = 24;
    public static final float[] WIDTHS_PERCENT_PARTIAL_ACTIVITY_GRID = {
        18f, 6f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 
        3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 
        3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f
    };
    
    //Data Mapper
    public static final String CAREER_SOFTWARE_ENGINEERING = "Licenciatura en Ingeniería de Software";
    public static final String REPORT_COVERAGE_PREFIX = "Entrega mensual (";
    public static final String REPORT_HOURS_SEPARATOR = ") - Horas cubiertas en el periodo: ";
    

    // Self Evaluation
    public static final float CELL_HEADER_FONT_SIZE = 8.5f;
    public static final float PDF_SCALE_TABLE_WIDTH = 220f;
    public static final String PATH_SELFEVALUATIONS_DIRECTORY = "reports/selfevaluations/";
    public static final String PREFIX_SELFEVALUATION_FILE = "Autoevaluacion_";
    public static final String DATE_FORMAT_LONG_SPANISH = "dd 'de' MMMM 'de' yyyy";
    public static final float[] WIDTHS_PERCENT_STUDENT_INFO_GRID = {30f, 70f};
    public static final float[] WIDTHS_PERCENT_SCALE_GRID = {75f, 25f};
    public static final float[] WIDTHS_PERCENT_EVALUATION_GRID_SE = {75f, 5f, 5f, 5f, 5f, 5f};
    
    //GenereateReportViewController
    public static final String MONTHLY_REPORT_KEYWORD = "mensual";
    public static final String TIMELINE_PLAN_LABEL = "Plan";
    public static final String TIMELINE_REAL_LABEL = "Real";
    
    //AddReportVieController
    public static final String FILTER_ALL = "Todos";
    public static final String PARTIAL_REPORT_KEYWORD = "Parcial";
    public static final String FINAL_REPORT_KEYWORD = "Final";
    
    //Student Controller
    public static final String STATUS_LATE_SUBMISSION = "Extemporáneo";
    public static final String STATUS_ON_TIME_SUBMISSION = "A tiempo";
    
    //DocumentDAO
    public static final String DOCUMENT_STATUS_PENDING = "Pendiente"; 
    public static final String DOC_TYPE_ACCEPTANCE = "Oficio de Aceptación";
    public static final String DOC_TYPE_ACCEPTANCE_ALT = "OficioAceptacion"; 
    public static final String DOC_TYPE_SCHEDULE = "Horario Escolar";
    public static final String DOC_TYPE_SCHEDULE_ALT = "Horario";  
    public static final String DOC_TYPE_INSURANCE = "Certificado de Seguro";
    public static final String DOC_TYPE_INSURANCE_ALT = "CertificadoSeguro";   
    public static final String DOC_TYPE_TIMELINE = "Cronograma de Actividades";
    public static final String DOC_TYPE_TIMELINE_ALT = "CronogramaActividades";   
    public static final String DOC_TYPE_ORGANIZATION_EVAL = "Evaluación Organización Vinculada";
    public static final String DOC_TYPE_ORGANIZATION_EVAL_ALT = "Evaluación OV";
    
    //ProjectDAO
    public static final String PROJECT_STATUS_ACTIVE = "Activo";
    public static final String PROJECT_STATUS_INACTIVE = "Inactivo";
    public static final String PROJECT_RESPONSIBLE_UNASSIGNED = "Por asignar";
    
    //ProjectRequestDAO
    public static final int PROJECT_REQUEST_MIN_PRIORITY = 1;
    public static final int PROJECT_REQUEST_MAX_PRIORITY = 3;
    public static final int STATEMENT_PARAMETER_INDEX_FIRST = 1;
    public static final int STATEMENT_PARAMETER_INDEX_SECOND = 2;
    public static final int STATEMENT_PARAMETER_INDEX_THIRD = 3;
    
    //ReportActivityDAO
    public static final String REPORT_STATUS_APPROVED = "Aprobado";
    public static final int FOREIGN_KEY_CHECKS_DISABLE = 0;
    public static final int FOREIGN_KEY_CHECKS_ENABLE = 1;
    public static final int VALUE_CONVERSION_TRUE_INT = 1;
    
    
    //Logger
    public static final String PATH_LOGS_DIRECTORY = "logs/";
    public static final String PATH_LOGS_FILE = "logs/professional_practice.log";
    public static final int LOG_FILE_SIZE_LIMIT = 5242880;
    public static final int LOG_FILE_COUNT = 3;
    

    public static final String TITLE_STAGE_PROFESSOR_REGISTRATION = "Registrar Nuevo Profesor";
    public static final String TITLE_STAGE_LOGIN = "Professional Practice System - Login";
    
    
    //ProfessorMenu
    public static final String PATH_FXML_STUDENT_SELECTOR_VERIFICATION = "/fxml/StudentSelectorForVerificationView.fxml";
    public static final String PATH_FXML_ASSIGNED_EE = "/fxml/AssignedEducationalExperiencesView.fxml";
    public static final String PATH_FXML_STUDENT_HOURS_MONITOR = "/fxml/StudentHoursMonitorView.fxml";
    public static final String PATH_FXML_STUDENT_DELIVERIES_MONITOR = "/fxml/StudentDeliveriesMonitorView.fxml";
    public static final String PATH_FXML_DEADLINE_CONFIGURATION = "/fxml/DeadlineConfigurationView.fxml";
    public static final String PATH_FXML_EVALUATE_REPORTS_LIST = "/fxml/EvaluateReportsListView.fxml";
    public static final String TITLE_STAGE_ASSIGNED_EE = "Sistema de Prácticas Profesionales - Experiencias Educativas Impartidas";
    public static final String TITLE_STAGE_STUDENT_HOURS = "Sistema de Prácticas Profesionales - Monitoreo de Alumnos";
    public static final String TITLE_STAGE_STUDENT_DELIVERIES = "Sistema de Prácticas Profesionales - Monitoreo de Entregas";
    public static final String TITLE_STAGE_DEADLINE_CONFIG = "Configurar Plazos de Entregas";
    public static final String TITLE_STAGE_EVALUATE_REPORTS = "Sistema de Prácticas Profesionales - Evaluar Reportes";
    
    //StudentMenu
    public static final String STUDENT_STATUS_ACREDITED = "Acreditado";
    public static final int REQUIRED_HOURS_PARTIAL_REPORT = 210;
    public static final String TITLE_STAGE_REPORT_FORM = "Sistema de Prácticas Profesionales - Formulario de Reporte";
    public static final String TITLE_STAGE_PARTIAL_REPORT = "Sistema de Prácticas Profesionales - Informe Parcial de Actividades";
    public static final String TITLE_STAGE_FINAL_REPORT = "Sistema de Prácticas Profesionales - Formulario de Informe Final";
    
    //ReportDAO
    public static final int DEFAULT_REPORT_NUMBER = 1;
    
    //ProfessorController
    public static final int EMPTY_PROJECTS = 0;
    
    
    
    public static final int IS_COORDINATOR = 1;
    
    
    
   public static final String ROLE_ADMINISTRATOR = "Administrador";
    
    
    
    
    
    public static final int BYTES_IN_MEGABYTE = 1024 * 1024;
    public static final int MAX_FILE_SIZE_MB = 15;
    public static final int MAX_FILE_SIZE = MAX_FILE_SIZE_MB * BYTES_IN_MEGABYTE;
    public static final int EMPTY_CONTENT_SIZE = 0;
    public static final double EXPANDED_HEIGHT = 100.0;
    public static final double COLLAPSED_HEIGHT = 38.0;

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EXPERIENCE_NAME_LENGTH = 60;
    public static final int MAX_POSITION_LENGTH = 100;
    public static final int PHONE_NUMBER_LENGTH = 10;
    public static final int MAX_RESPONSIBLE_NAME_LENGTH = 50;
    public static final int MYSQL_DUPLICATE_KEY_ERROR = 1062;
    public static final int MYSQL_FOREIGN_KEY_NOT_FOUND = 1452;

    public static final int MIN_VALID_ID = 0;

    public static final String DEFAULT_PERIOD = "FEB-JUL 2026";

    public static final String ROLE_STUDENT = "Estudiante";
    public static final String ROLE_ADMIN = "Administrador";
    public static final String ROLE_PROFESSOR = "Profesor";
    public static final String ROLE_COORDINATOR = "Coordinador";

    public static final String STATUS_ACTIVE = "Activo";
    public static final String STATUS_INACTIVE = "Inactivo";

    public static final int RESET = 0;
    public static final int SUCCESS = 0;

    public static final int INITIAL_CREDITS = 0;
    public static final int MINIMUM_CREDITS_FOR_PRACTICES = 255;
    public static final int TEACHER_ID_INDEX = 0;


    public static final String PDF_EXTENSION_DESCRIPTION = "Archivos PDF";
    public static final String PDF_EXTENSION_FILTER = "*.pdf";

    public static final String SECTOR_PUBLIC = "Público";
    public static final String SECTOR_PRIVATE = "Privado";
    
    public static final int ENROLLMENT_START_INDEX = 0;
    public static final int ENROLLMENT_NUMERIC_INDEX = 1;
    public static final int ENROLLMENT_MAX_LENGTH = 9;
    
    public static final String ENROLLMENT_PREFIX_S = "S";
    public static final String ONLY_NUMBERS_PATTERN = "^[0-9]*$";
    public static final String KEYBOARD_NAME_PATTERN = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ'\\s-]*$";
    public static final String NAME_FILTER_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*";
    
    public static final String WELCOME_PREFIX = "Bienvenido: ";
    public static final String SESSION_DATE_PREFIX = "Ingreso al sistema: ";
    
    public static final String DOC_TYPE_EVALUATION = "Evaluación OV";
    public static final String DOC_TYPE_ACTIVITY = "Cronograma de Actividades";
    public static final String COL_STATUS_SCHEDULE = "horario";
    public static final String COL_STATUS_INSURANCE = "certificadoSeguro";
    public static final String COL_STATUS_EVALUATION = "evaluacionOV";
    public static final String COL_STATUS_ACTIVITY = "cronogramaActividades";
    public static final String COL_STATUS_ACCEPTANCE = "oficioAceptacion";
    
    public static final String AUXILIARY_MODE_LABEL = "Modo auxiliar";
    public static final String TITLE_REGISTER_STUDENT = "Registrar Alumno";
    public static final String TITLE_INACTIVATE_STUDENT = "Inactivar Alumno";
    public static final String TITLE_REGISTER_PROJECT = "Registrar Proyecto";
    public static final String TITLE_ASSIGN_PROJECT = "Asignar Proyecto a Estudiante";
    public static final String TITLE_PROJECTS_MANAGEMENT = "Gestión de Proyectos";
    public static final String TITLE_REGISTER_ORGANIZATION = "Registrar Organización";
    public static final String TITLE_CONSULT_ORGANIZATIONS = "Consultar Organizaciones";
    public static final String TITLE_REGISTER_RESPONSIBLE = "Registrar Responsable Técnico";
    public static final String TITLE_MANAGE_EE = "Gestionar Experiencia Educativa";
    public static final String TITLE_RESPONSIBLES_LIST = "Sistema de Prácticas - Lista de Responsables";
    
    public static final String PROP_EE_NRC = "nrc";
    public static final String PROP_EE_NAME = "educativeExperienceName";
    public static final String PROP_EE_SECTION = "section";
    public static final String PROP_EE_PROFESSOR = "professorName";
    public static final String ALERT_TITLE_DATABASE_ERROR = "Error de Base de Datos";
    
    public static final String TITLE_MENU_ADMIN = "Sistema de Prácticas - Administrador";
    public static final String TITLE_MENU_PROFESSOR = "Sistema de Prácticas - Profesor";
    public static final String TITLE_MENU_STUDENT = "Sistema de Prácticas - Estudiante";
    public static final String TITLE_MENU_COORDINATOR = "Sistema de Prácticas - Coordinador";
    public static final String TITLE_FORGOT_PASSWORD = "Sistema de Prácticas - Recuperación de Contraseña";
    
    public static final String TITLE_STUDENT_SELECTOR = "Sistema de Prácticas - Seleccionar Practicante";
    
    public static final int DOCUMENT_REJECTED = 0;
    public static final int DOCUMENT_UNDER_REVIEW = 1;
    public static final int DOCUMENT_VALIDATED = 2;
    
    public static final int MAX_PROJECT_NAME_LENGTH = 255;
    public static final int MAX_PROJECT_OBJECTIVE_LENGTH = 200;
    public static final int MAX_PROJECT_DURATION_LABEL_LENGTH = 50;

    public static final int TOTAL_COLUMNS_EVALUATION = 6;
    public static final int STATEMENT_COLUMN_INDEX = 0;
    public static final String MARK_X = "X";
    public static final String EMPTY_STRING = "";
  
    public static final float HEADER_FONT_SIZE = 14f;
    public static final float BODY_FONT_SIZE = 10f;
    public static final float SCALE_TABLE_WIDTH = 250f;
    
    public static final float[] STUDENT_INFO_COLUMNS = {2f, 4f};
    public static final float[] CRITERIA_SCALE_COLUMNS = {4f, 1f};
    public static final float[] EVALUATION_MATRIX_COLUMNS = {10f, 1f, 1f, 1f, 1f, 1f};
    
    public static final int MIN_SCORE_VALUE = 1;
    public static final int MAX_SCORE_VALUE = 5;
    public static final int INITIAL_COUNTER_INDEX = 1;
 
    public static final int SCORE_ONE = 1;
    public static final int SCORE_TWO = 2;
    public static final int SCORE_THREE = 3;
    public static final int SCORE_FOUR = 4;
    public static final int SCORE_FIVE = 5;
    public static final int SCORE_THRESHOLD =0;
    public static final int MAXIMUM_PROJECT_OPTIONS = 3;
        
    public static final int MAX_OBJECTIVE_LENGTH = 200;
    public static final int MAX_DURATION_LENGTH = 50;
    
    public static final String TIME_PATTERN_FORMAT = "HH:mm";

    public static final String TEXT_STATUS_VALIDATED = "Validado";
    public static final String TEXT_STATUS_REJECTED = "Rechazado";
    public static final String TEXT_STATUS_UNDER_REVIEW = "En revisión";

    public static final String REPORT_TITLE_PARTIAL = "PRAIS-P-02 REPORTE PARCIAL";
    public static final String LABEL_CARRERA = "Carrera:";
    public static final String LABEL_NRC = "NRC:";
    public static final String LABEL_PROFESSOR = "Profesor:";
    public static final String LABEL_PERIOD = "Período escolar:";
    public static final String LABEL_STUDENTS = "Alumno(s):";
    public static final String LABEL_ORGANIZATION = "Organización vinculada:";
    public static final String LABEL_PROJECT = "Proyecto:";
    public static final String LABEL_REPORT_PERIOD = "Período del reporte y horas cubiertas:";
    public static final String LABEL_REPORT_DATE = "Fecha del reporte:";
    public static final String LABEL_REPORT_NUMBER = "Número del informe:";
    public static final String LABEL_OBJECTIVES = "Objetivo(s) general del proyecto";
    public static final String LABEL_METHODOLOGY = "Metodología";
    public static final String LABEL_ACTIVITIES_PROGRESS = "Avance de actividades realizadas en relación al plan de trabajo";
    public static final String LABEL_RESULTS = "Resultados obtenidos al momento";
    public static final String LABEL_OBSERVATIONS = "Observaciones";
    public static final String LABEL_EVALUATION_SECTION = "Sección EXCLUSIVA para llenado por parte del Responsable Técnico";
    public static final String LABEL_EVALUATION_INSTRUCTIONS = "INSTRUCCIONES: Tache según corresponda a los criterios de evaluación del desempeño.";

    public static final float[] PARTIAL_GRID_COLUMNS = {2f, 4f, 2f, 4f};
    public static final float[] TIMELINE_MATRIX_COLUMNS = {4f, 2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};

    public static final String REPORT_TITLE_FINAL = "PRAIS-05 REPORTE FINAL";
    public static final String LABEL_TOTAL_HOURS = "Total de horas cubiertas:";
    public static final String LABEL_FINAL_REPORT_TYPE = "Reporte: FINAL";
    public static final String LABEL_APPLIED_METHODOLOGY = "Metodología aplicada";
    public static final String LABEL_PROGRAMMED_ACTIVITY = "Actividad programada";
    public static final String LABEL_PERCENT_ADVANCE = "% Avance";
    public static final String LABEL_DELIVERABLE_RESULTS = "Resultados en término de productos comprometidos";
    public static final String LABEL_DELIVERABLE_PRODUCT = "Resultado entregable";

    public static final float[] FINAL_MATRIX_COLUMNS = {6f, 2f, 4f};
    
    public static final int MAX_STAFF_NUMBER_LENGTH = 6;
    public static final int MINIMUM_ACTIVE_COORDINATORS = 0;
    public static final String TITLE_LOGIN = "Sistema de Prácticas - Inicio de Sesión";
    public static final String TITLE_ASSIGN_STUDENT_EE = "Asignar Alumno a Experiencia Educativa";
    public static final String TITLE_REGISTER_ADMIN = "Registrar adminsitrador";
    public static final String TITLE_ACTIVITIES_MANAGEMENT = "Sistema de Prácticas - Asignación de Actividades";
    
    public static final int MAX_DEGREE_CREDITS = 365;
    public static final String DEFAULT_FALLBACK_PERIOD = "202651";
    public static final String TITLE_COORDINATOR_MENU = "Sistema de Prácticas - Menú Coordinador";
    public static final String ERROR_SYSTEM_TITLE = "Error de Sistema";
    public static final String ERROR_DATABASE_TITLE = "Error de Base de Datos";

    public static final String STUDENT_INACTIVE_LABEL = "Inactivo";
    public static final String TITLE_PROFESSOR_MENU_FALLBACK = "Sistema de Prácticas Profesionales - Menú Profesor";
    public static final String ERROR_DATA_TITLE = "Error de Datos";
    
    public static final int MAX_NRC_LENGTH = 5;
    public static final String TITLE_COORDINATOR_MENU_EE = "Sistema de Prácticas - Menú Profesor";
    
    public static final String TITLE_PROFESSOR_MENU = "Sistema de Prácticas Profesionales - Menú Docente";
    public static final String PROP_EE_NRC_SHORT = "nrc";
    public static final String PROP_EE_NAME_LONG = "educativeExperienceName";
    public static final String PROP_EE_SECTION_SHORT = "section";
    
    public static final String HOURS_PARTIAL_REPORT = "210";
    public static final int REQUIRED_HOURS_FINAL_REPORT = 420;
    
    public static final String FILTER_TYPE_ALL = "Todos";
    public static final String FILTER_TYPE_PARTIAL = "Reporte Parcial";
    public static final String FILTER_TYPE_FINAL = "Informe Final";
    public static final String FILTER_TYPE_SELF_EVALUATION = "Autoevaluación";
    
    public static final String LOCAL_MODE_LABEL = "Modo local";
    
    public static final String TITLE_PROJECT_SELECTION = "Solicitar Proyecto";
    public static final String TITLE_UPLOAD_FORMATS = "Subir Formatos Iniciales";
    public static final String TITLE_GENERATE_REPORT = "Generar Reporte";
    public static final String TITLE_GENERATE_PARTIAL = "Generar Reporte Parcial";
    public static final String TITLE_GENERATE_FINAL = "Generar Informe Final";
    public static final String TITLE_REPORTS_AVAILABLE = "Entregas de Reportes Disponibles";
    public static final String TITLE_SELF_EVALUATION_VIEW = "Hacer Autoevaluación";
    
    public static final String TITLE_MAIN_MENU_PROFESSOR = "Sistema de Prácticas - Menú Principal";
    public static final String TITLE_VERIFICATION_STAGE = "Error del Sistema";
    public static final String FXML_VERIFICATION_WORKSPACE = "/fxml/InitialDocumentsVerificationView.fxml";
    
    public static final String STATUS_OVERDUE = "Plazo Vencido";
    public static final String STYLE_STATUS_OVERDUE = "-fx-text-fill: #dc3545; -fx-font-weight: bold;";
    public static final String STYLE_STATUS_ON_TIME = "-fx-text-fill: #28a745; -fx-font-weight: bold;";
    
    public static final String REPORT_TYPE_FINAL_MATCH = "Reporte Final";
    public static final String DB_COLUMN_FINAL_REPORT = "reporteFinal";
    public static final String DB_COLUMN_PARTIAL_REPORT = "reporteParcial";
    
    public static final String REGEX_EMAIL_SPLIT = "@";
    public static final String REGEX_ENROLLMENT_CLEAN = "^[^sS]*";
    
    public static final String TITLE_ADD_REPORT_CONTAINER = "Sistema de Prácticas Profesionales - Plazos de Entrega";
    public static final String FILE_CHOOSER_WIZARD_TITLE = "Seleccionar Reporte PDF Firmado";
    
    public static final String TITLE_PROFESSOR_DASHBOARD = "Sistema de Prácticas Profesionales - Menú Docente";
    
    public static final String EVALUATION_DELIVERED_STATUS = "Sí";
    public static final String TITLE_PROFESSOR_DELIVERIES_MENU = "Sistema de Prácticas Profesionales - Menú Docente";
    
    public static final String REPORT_TYPE_WEEKLY = "Reporte Semanal";
    public static final String REPORT_TYPE_MONTHLY = "Reporte Mensual";
    
    public static final String ROLE_COORDINATOR_MATCH = "COORDINADOR";
    
    public static final String STATUS_TEXT_NOT_ESTABLISHED = "No establecido";
    public static final String STATUS_TEXT_EXPIRED = "Expirado / Extemporáneo";
    public static final String STATUS_TEXT_ACTIVE = "Vigente / Activo";
    
    public static final String DATE_PATTERN_DISPLAY = "dd/MM/yyyy HH:mm";
    
    public static final String TITLE_COORDINATOR_MENU_DEADLINE = "Sistema de Prácticas Profesionales - Menú Coordinador";
    public static final String TITLE_PROFESSOR_MENU_DEADLINE = "Sistema de Prácticas Profesionales - Menú Docente";
    
    public static final String ALERT_TITLE_FIELDS_INCOMPLETE = "Campos Incompletos";
    public static final String ALERT_TITLE_TIME_INVALID = "Formato de Hora Inválido";
    public static final String ALERT_TITLE_DATE_INVALID = "Fecha Inválida";
    public static final String ALERT_TITLE_DEADLINE_SAVED = "Plazo Registrado";
    
    public static final String LABEL_NOT_ASSIGNED = "No asignado";
    public static final String LABEL_NOT_DEFINED = "No definida";
    public static final String LABEL_LOADING_PREFIX = "Por cargar...";
    public static final String NRC_PREFIX_LABEL = "NRC: ";
    public static final String SEPARATOR_LABEL = " - ";
    public static final String TITLE_COORDINATOR_MENU_ASSIGNMENT = "Sistema de Prácticas - Menú Coordinador";
    
    public static final String FXML_STUDENT_MENU = "/fxml/StudentMenuView.fxml";
    public static final String CONFIRM_ALERT_TITLE = "¿Desea generar la autoevaluación ahora?";
    public static final String ALERT_TITLE_SUCCESS_PROCESS = "Proceso Exitoso";
    public static final String PATH_REPORTS_SELFEVALUATIONS_PREFIX = "reports/selfevaluations/Autoevaluacion_";
    public static final String PATH_REPORTS_EXTENSION_PDF = ".pdf";
    public static final String PROP_CRITERIA_STATEMENT = "statement";
    
    public static final String REVIEW_STATUS_APPROVED = "Aprobado";
    public static final String REVIEW_STATUS_REJECTED = "Rechazado";
    public static final String REVIEW_STATUS_PENDING = "Pendiente";
    
    public static final String REPORT_FILE_PREFIX = "Reporte_";
    public static final String REPORT_FILE_SEPARATOR = "_";
    
    public static final int MAX_CREDITABLE_HOURS = 420;
    
    public static final String PROP_CHECKLIST_NAME = "name";
    public static final String PROP_CHECKLIST_COMPLETED = "completed";
    
    public static final String DEADLINE_NO_DATE_LABEL = "Sin fecha";
    public static final String DEADLINE_NOT_CONFIGURED_LABEL = "No configurado";
    public static final String DEADLINE_STATUS_EXPIRED = "Expirado / Cerrado";
    public static final String DEADLINE_STATUS_ACTIVE = "Vigente / Activo";
    
    public static final String TIME_FORMAT_SHORT = "HH:mm";
    public static final String TIME_FORMAT_FALLBACK_EXAMPLE = "23:59";
    
    public static final String ERROR_CONTEXT_TITLE = "Error de Contexto";
    public static final String ERROR_NETWORK_TITLE = "Fallo de Red";
    public static final String ERROR_NAVIGATION_TITLE = "Error de Navegación";
    
    public static final String TITLE_PROFESSOR_MENU_SYSTEM = "Sistema de Prácticas - Menú Profesor";
    
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final String TIME_PATTERN = "HH:mm";
    
    
    public static final String PERIOD_FEB_JUL_MATCH = "FEB-JUL 2026";
    public static final LocalDate FEB_JUL_START_DATE = LocalDate.of(2026, 2, 1);
    public static final LocalDate FEB_JUL_END_DATE = LocalDate.of(2026, 7, 31);
            
    public static final LocalDate AGO_ENE_START_DATE = LocalDate.of(2026, 8, 1);
    public static final LocalDate AGO_ENE_END_DATE = LocalDate.of(2027, 1, 31);
}
