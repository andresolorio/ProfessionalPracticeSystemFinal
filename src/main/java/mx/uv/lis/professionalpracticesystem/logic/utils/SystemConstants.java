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
    
    //Test 
    public static final String TEST_STAFF_NUMBER = "999999";
    public static final String TEST_EMAIL = "professor.test@uv.mx";
    public static final String TARGET_REPORT_TYPE = "Mensual";
    public static final String NEW_REPORT_TYPE = "Parcial";
    public static final int EXPECTED_ROWS_INSERTED = 1;
    public static final int EXPECTED_ROWS_UPSERTED = 2;
    public static final int FIRST_ELEMENT_INDEX = 0;
    public static final int TEST_NRC = 12345;
    public static final int TEST_YEAR = 2026;
    public static final int TEST_MAX_HOUR = 23;
    public static final int TEST_MAX_MINUTE = 59;
    public static final int TEST_MAX_SECOND = 59;
    
    public static final String TEST_DOC_ENROLLMENT = "S24099999";
    public static final String TEST_DOC_EMAIL = "doc.test@uv.mx";
    public static final String TEST_DOC_NAME_TARGET = "JUnit_Target_Doc.pdf";
    public static final String TEST_DOC_NAME_NEW = "JUnit_New_Doc.pdf";
    public static final String TEST_DOC_NAME_UPDATED = "JUnit_Updated_Doc.pdf";
    public static final String TEST_DOC_TYPE_TARGET = "Parcial";
    public static final String TEST_DOC_TYPE_NEW = "Final";
    
    public static final int EXPECTED_SINGLE_ROW_AFFECTED = 1;
    public static final int TEST_MONTH_JUNE = 6;
    public static final int TEST_DAY_FIFTEEN = 15;
    
    public static final byte[] TEST_DUMMY_BYTES = new byte[]{1, 2, 3};
    
    public static final int TEST_EE_NRC_TARGET = 80001;
    public static final int TEST_EE_NRC_NEW = 80002;
    public static final String TEST_EE_NRC_TARGET_STRING = "80001";
    public static final String TEST_EE_NRC_NON_EXISTENT = "99999";
    public static final String TEST_EE_NAME_TARGET = "Diseño de Software";
    public static final String TEST_EE_NAME_NEW = "Pruebas de Software";
    public static final String TEST_EE_NAME_UPDATED = "Arquitectura de Software";
    public static final String TEST_EE_SECTION = "Matutino";
    public static final String TEST_EE_STAFF_NUMBER = "777777";
    public static final String TEST_EE_PROFESSOR_EMAIL = "asanchezzprof@uv.mx";
    public static final String INVALID_NRC_STRING = "ABC";
    public static final String EXPECTED_FULL_PROFESSOR_NAME = "JUnit Teacher ";
    
    public static final String TEST_CRITERION_STATEMENT = "JUnit Target Criterion Statement";

    public static final String TEST_EVAL_ENROLLMENT = "S24099997";
    public static final String TEST_EVAL_EMAIL = "eval.ov@uv.mx";
    public static final int TEST_EVAL_ORG_ID = 9999;
    public static final String TEST_EVAL_ORG_NAME = "JUnit Org";
    public static final float TEST_EVAL_SCORE = 9.5f;
    public static final String TEST_EVAL_FILE_PATH = "docs/evaluacion_test.pdf";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: LINKED ORGANIZATION CONTROLLER ---
    public static final String TEST_LOC_ORG_TARGET = "LOC Target Org";
    public static final String TEST_LOC_ORG_NEW = "LOC New Org";
    public static final String TEST_LOC_ORG_UPDATED = "LOC Updated Org";
    public static final String TEST_LOC_ADDRESS = "Avenida LOC 123";
    public static final String TEST_LOC_PHONE_VALID = "2281234567";
    public static final String TEST_LOC_PHONE_INVALID = "228123";
    public static final String TEST_LOC_CITY = "Xalapa";
    public static final String TEST_LOC_EMAIL_VALID = "contacto@loc.mx";
    public static final String TEST_LOC_EMAIL_INVALID = "contacto@loc";
    
    public static final String TEST_ORG_NAME_TARGET = "JUnitt Target Corp";
    public static final String TEST_ORG_NAME_NEW = "JUnit New Enterprise";
    public static final String TEST_ORG_NAME_UPDATED = "JUnit Updated Solutions";
    public static final String TEST_ORG_ADDRESS = "Avenida Testing 123";
    public static final String TEST_ORG_PHONE = "2281234567";
    public static final String TEST_ORG_PHONE_INVALID = "123";
    public static final String TEST_ORG_CITY = "Xalapa";
    public static final String TEST_ORG_EMAIL = "contacto@junitcorp.mx";
    public static final String TEST_ORG_EMAIL_INVALID = "contacto_sin_arroba.mx";
    public static final int TEST_ORG_DIRECT_USERS = 50;
    public static final int TEST_ORG_INDIRECT_USERS = 200;

    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: ACTIVIDADES ---
    public static final String TEST_ACTIVITY_NAME_TARGET = "JUnit Target Activity";
    public static final String TEST_ACTIVITY_NAME_NEW = "JUnit Insertion Activity";
    public static final String TEST_ACTIVITY_NAME_UPDATED = "JUnit Updated Activity";
    public static final String TEST_ACTIVITY_DESC_TARGET = "Initial Verification Description";
    public static final String TEST_ACTIVITY_DESC_NEW = "Testing Linear Insertion Script";
    public static final String TEST_ACTIVITY_DESC_UPDATED = "Testing Linear Update Script";
    public static final int TEST_PROJECT_ID = 1;
    public static final int NON_EXISTENT_ID = -1;
    public static final int TEST_DAY_THIRTY = 30;
    public static final String TEST_NAME_EXCEEDING_LENGTH = "Esta es una cadena generada artificialmente con el unico proposito de exceder el limite maximo de cien caracteres permitido en la validacion del controlador para lanzar la excepcion correspondiente.";

    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: ENTREGABLES DE REPORTE FINAL ---
    public static final String TEST_FRD_ENROLLMENT = "S24099995";
    public static final String TEST_FRD_EMAIL = "frd.student@uv.mx";
    public static final String TEST_FRD_NON_EXISTENT_ENROLLMENT = "S00000000";
    public static final String TEST_FRD_RESULT_TARGET = "JUnit Target Deliverable";
    public static final String TEST_FRD_RESULT_NEW = "JUnit New Deliverable";
    public static final int TEST_FRD_ADVANCE_TARGET = 50;
    public static final int TEST_FRD_ADVANCE_NEW = 100;
    public static final String TEST_FRD_OBSERVATIONS = "Prueba de insercion de entregables";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: PROFESORES ---
    public static final String TEST_PROF_STAFF_TARGET = "111111";
    public static final String TEST_PROF_STAFF_NEW = "222222";
    public static final String TEST_PROF_STAFF_INACTIVE = "333333";
    public static final String TEST_PROF_STAFF_NON_EXISTENT = "000000";
    public static final String TEST_PROF_EMAIL_TARGET = "prof.target@uv.mx";
    public static final String TEST_PROF_EMAIL_NEW = "prof.new@uv.mx";
    public static final String TEST_PROF_EMAIL_INACTIVE = "prof.inactive@uv.mx";
    public static final String TEST_PROF_FIRST_NAME = "JUnitName";
    public static final String TEST_PROF_PATERNAL = "JUnitPaternal";
    public static final String TEST_PROF_MATERNAL = "JUnitMaternal";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: PROYECTOS Y ASIGNACIONES ---
    public static final String TEST_PROJECT_NAME_TARGET = "JUnit Target Project";
    public static final String TEST_PROJECT_NAME_NEW = "JUnit New Project";
    public static final String TEST_PROJECT_NAME_UPDATED = "JUnit Updated Project";
    public static final String TEST_PROJECT_DESC = "JUnit Project Description";
    public static final String TEST_PROJECT_METHODOLOGY = "Scrum Modular";
    public static final String TEST_PROJECT_OBJ_GEN = "General Objective";
    public static final String TEST_PROJECT_OBJ_IMM = "Immediate Objective";
    public static final String TEST_PROJECT_OBJ_MED = "Mediated Objective";
    public static final String TEST_PROJECT_RESPONSIBILITIES = "Develop and test";
    public static final String TEST_PROJECT_RESOURCES = "Laptop, IDE";
    public static final String TEST_PROJECT_DURATION = "400 horas";
    public static final int TEST_PROJECT_VACANCIES = 3;

    // Dependencias de llaves foráneas para Proyecto
    public static final int TEST_PROJECT_ORG_ID = 9998;
    public static final String TEST_PROJECT_ORG_NAME = "JUnit Project Org";
    public static final int TEST_PROJECT_RESP_ID = 9998;

    // Dependencias para métodos de asignación y solicitud
    public static final String TEST_PROJECT_STUDENT_ENROLLMENT = "S24099994";
    public static final String TEST_PROJECT_STUDENT_EMAIL = "proj.student@uv.mx";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: PROFESSOR CONTROLLER (ORQUESTADOR) ---
    public static final String TEST_PC_STAFF_TARGET = "555555";
    public static final String TEST_PC_EMAIL_TARGET = "prof.ctrl@uv.mx";
    public static final String TEST_PC_STUDENT_ENROLLMENT = "S24099993";
    public static final String TEST_PC_STUDENT_EMAIL = "prof.student@uv.mx";
    public static final String TEST_PC_REPORT_TYPE = "Parcial";
    public static final String TEST_PC_REPORT_STATUS_APPROVED = "Aprobado";
    public static final String TEST_PC_REPORT_STATUS_REJECTED = "Rechazado";
    public static final String TEST_PC_OBSERVATIONS = "Faltan firmas";
    public static final int TEST_PC_PROJECT_ID = 500;
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: REPORT CONTROLLER ---
    public static final String TEST_RC_ENROLLMENT = "S24099992";
    public static final String TEST_RC_STUDENT_EMAIL = "report.ctrl@uv.mx";
    public static final String TEST_RC_STAFF_NUMBER = "444444";
    public static final String TEST_RC_PROF_EMAIL = "prof.report.ctrl@uv.mx";
    public static final String TEST_RC_REPORT_TYPE = "Parcial";
    public static final String TEST_RC_INVALID_ENROLLMENT = "12345";
    public static final String TEST_RC_INVALID_STATUS = "Indefinido";
    public static final String TEST_RC_STATUS = "A tiempo";
    public static final int TEST_RC_REPORTED_HOURS = 40;
    public static final int TEST_RC_REPORT_NUMBER = 1;
    public static final int NEGATIVE_HOURS = -5;
    public static final String STATUS_APPROVED = "Aprobado";
    // Arreglo vacío de bytes para simular archivos nulos
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: RESPONSABLE DE PROYECTO ---
    public static final String TEST_RP_FIRST_NAME_TARGET = "JUnit Target Resp";
    public static final String TEST_RP_FIRST_NAME_NEW = "JUnit New Resp";
    public static final String TEST_RP_FIRST_NAME_UPDATED = "JUnit Updated Resp";
    public static final String TEST_RP_LAST_NAME = "JUnit LastName";
    public static final String TEST_RP_POSITION = "Gerente IT";
    
    // Cadena ultra larga para exceder el límite de caracteres del cargo o nombre
    public static final String TEST_STRING_EXCEEDING_LIMIT = "Esta es una cadena generada artificialmente con el unico proposito de exceder el limite maximo de caracteres permitido en la validacion del controlador para lanzar la excepcion correspondiente.";
    
    public static final int TEST_RP_ORG_ID = 9997;
    public static final String TEST_RP_ORG_NAME = "JUnit Resp Org";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: SELF EVALUATION CONTROLLER ---
    public static final String TEST_SEC_ENROLLMENT = "S24099991";
    public static final String TEST_SEC_STUDENT_EMAIL = "sec.student@uv.mx";
    public static final int TEST_SEC_ORG_ID = 9996;
    public static final String TEST_SEC_ORG_NAME = "JUnit SEC Org";
    public static final int TEST_SEC_PROJECT_ID = 600;
    public static final String TEST_SEC_PROJECT_NAME = "JUnit SEC Project";
    public static final int TEST_SEC_CRITERION_ID = 1;
    public static final int TEST_SEC_CRITERION_SCORE = 5;
    public static final String TEST_SEC_INVALID_ENROLLMENT = "S00000000";

    // --- CONSTANTES PARA PRUEBAS UNITARIAS: STUDENT CONTROLLER ---
    public static final String TEST_STC_ENROLLMENT = "S24099990";
    public static final String TEST_STC_STUDENT_EMAIL = "student.ctrl@uv.mx";
    public static final String TEST_STC_STAFF_NUMBER = "666666";
    public static final String TEST_STC_PROF_EMAIL = "prof.stc@uv.mx";
    public static final int TEST_STC_NRC = 88888;
    public static final String TEST_STC_DOC_TYPE = "Carta de Aceptación";
    public static final String TEST_STC_REPORT_TYPE = "Mensual";
    public static final int TEST_STC_REPORT_NUMBER = 1;
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: USER CONTROLLER ---
    public static final String TEST_UC_STUDENT_EMAIL = "student.uc@uv.mx";
    public static final String TEST_UC_INACTIVE_STUDENT_EMAIL = "inactive.uc@uv.mx";
    public static final String TEST_UC_PROFESSOR_EMAIL = "prof.uc@uv.mx";
    public static final String TEST_UC_NEW_PROFESSOR_EMAIL = "new.prof.uc@uv.mx";
    public static final String TEST_UC_STUDENT_ENROLLMENT = "S24099989";
    public static final String TEST_UC_INACTIVE_ENROLLMENT = "S24099988";
    public static final String TEST_UC_STAFF_NUMBER = "777777";
    public static final String TEST_UC_NEW_STAFF_NUMBER = "888888";
    public static final String TEST_UC_PASSWORD_PLAIN = "password123";
    public static final String TEST_UC_PASSWORD_WRONG = "wrongpass";
    public static final String TEST_UC_RECOVERY_TOKEN = "TESTT1";
    
    // Hash BCrypt real de la palabra "password123" para pasar la validación del PasswordManager
    public static final String TEST_UC_HASHED_PASSWORD = "$2a$10$wYQ.P4L5O92T5O3lE5nL9u7F9yZ0u9j.V1n.8g10vV2/4O085.1qS";
    
    // --- CONSTANTES PARA PRUEBAS UNITARIAS: PROJECT REQUEST DAO ---
    public static final String TEST_PR_ENROLLMENT = "S24099980";
    public static final String TEST_PR_STUDENT_EMAIL = "pr.student@uv.mx";
    public static final int TEST_PR_PROJECT_ID = 700;
    public static final String TEST_PR_PROJECT_NAME = "JUnit PR Project";
    public static final int TEST_PR_ORG_ID = 9995;
    public static final String TEST_PR_ORG_NAME = "JUnit PR Org";
    
    // Valores de validación para la prioridad de solicitudes
    public static final int TEST_PR_VALID_PRIORITY = 1;
    public static final int TEST_PR_INVALID_PRIORITY_LOW = 0;
    public static final int TEST_PR_INVALID_PRIORITY_HIGH = 4;
    
    // Constantes para validación de base de datos
    public static final int TEST_PR_NON_EXISTENT_PROJECT_ID = 9999;
    public static final int EXPECTED_STATE_TRUE = 1;
    public static final int EXPECTED_STATE_FALSE = 0;
}
