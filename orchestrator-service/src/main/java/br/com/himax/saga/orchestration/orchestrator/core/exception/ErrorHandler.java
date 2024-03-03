package br.com.himax.saga.orchestration.orchestrator.core.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle errors using the "Problem Details for HTTP APIs" standardized
 * following RFC 9457 - Problem Details for HTTP APIs.
 * @see <a href="https://www.rfc-editor.org/info/rfc9457">RFC 9457</a>
 */
@AllArgsConstructor
@Service
public class ErrorHandler {

    public static final String FIELDS = "fields";
    public static final String INVALID_PARAMS = "Invalid Params";
    public static final String INVALID_BODY = "Invalid Body";

    private MessageSource messageSource;

    public ProblemDetail handleException(Exception ex, HttpStatusCode status, HttpServletRequest servletRequest) {
        return setupProblemDetail(ex, status, servletRequest);
    }

    private ProblemDetail setupProblemDetail(Exception ex, HttpStatusCode status,HttpServletRequest servletRequest) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        setTitleAndDetail(problemDetail, ex);
        problemDetail.setProperty("timestamp", OffsetDateTime.now());

        if(hasFields(ex)){
            problemDetail.setDetail(INVALID_PARAMS);
            problemDetail.setProperty(FIELDS, handleFields(ex));
        }

        /*
         * This url could be created in application to provide human-readable documentation for the
         * problem type. If not, its value is assumed to be "about:blank".
         * Remove this url defining null if url will not be created
         */
        String url = servletRequest.getRequestURL().toString().concat(("/error"));
        problemDetail.setType(URI.create(url));

        return problemDetail;
    }

    private void setTitleAndDetail(ProblemDetail problemDetail, Exception ex) {
        if(getRootCause(ex) instanceof JsonParseException){
            problemDetail.setTitle(INVALID_BODY);
            problemDetail.setDetail(getJsonParseExceptionMessage(ex));
            return;
        }

        if(getRootCause(ex) instanceof InvalidFormatException){
            problemDetail.setTitle(INVALID_BODY);
            problemDetail.setDetail(getInvalidFormatExceptionMessage(ex));
            return;
        }

        if(getRootCause(ex) instanceof UnrecognizedPropertyException){
            problemDetail.setTitle(INVALID_BODY);
            problemDetail.setDetail(getUnrecognizedPropertyExceptionMessage(ex));
            return;
        }

        if(getRootCause(ex) instanceof IgnoredPropertyException){
            problemDetail.setTitle(INVALID_BODY);
            problemDetail.setDetail(getIgnoredPropertyExceptionMessage(ex));
            return;
        }

        if(ex instanceof MethodArgumentTypeMismatchException){
            problemDetail.setTitle(INVALID_BODY);
            problemDetail.setDetail(getMethodArgumentTypeMismatchExceptionMessage(ex));
            return;
        }

        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(ex.getMessage());
    }

    private boolean hasFields(Exception ex) {
        return ex instanceof MethodArgumentNotValidException;
    }

    public HttpStatus getHttpStatus(Exception ex){
        return switch (getErrorName(ex)) {
            case "ChildNotFoundException","BusinessException","UnrecognizedPropertyException","IgnoredPropertyException" -> HttpStatus.BAD_REQUEST;
            case "EntityInUseException","AlreadyExistsException" -> HttpStatus.CONFLICT;
            case "EntityNotFoundException" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private Map<String, String> handleFields(Exception ex) {
        MethodArgumentNotValidException error = (MethodArgumentNotValidException) ex;
        /*
         * List of invalid params and theirs error messages customized at
         * messages.properties file
         */
        return error
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, e -> messageSource.getMessage(e, LocaleContextHolder.getLocale())));
    }

    private String getErrorName(Exception exception) {
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        return rootCause.getClass().getSimpleName();
    }

    private Throwable getRootCause(Exception exception) {
        return ExceptionUtils.getRootCause(exception);
    }

    private String getInvalidFormatExceptionMessage(Exception ex){
        InvalidFormatException error = (InvalidFormatException) getRootCause(ex);
        String property = error.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        String value = error.getValue().toString();
        String type = error.getTargetType().getSimpleName();
        return  String.format("The property '%s' received the value '%s' that is invalid. Send a compatible value with '%s'", property, value, type);
    }

    private String getJsonParseExceptionMessage(Exception ex) {
        JsonParseException error = (JsonParseException) getRootCause(ex);
        return error.getMessage();
    }
    private String getUnrecognizedPropertyExceptionMessage(Exception ex) {
        UnrecognizedPropertyException error = (UnrecognizedPropertyException) getRootCause(ex);
        return error.getMessage()
                .split("\\(")[0]
                .replace("\"","'");
    }

    private String getIgnoredPropertyExceptionMessage(Exception ex) {
        IgnoredPropertyException error = (IgnoredPropertyException) getRootCause(ex);
        return error.getMessage()
                .split("\\(")[0]
                .replace("\"","'");
    }

    private String getMethodArgumentTypeMismatchExceptionMessage(Exception ex) {
        MethodArgumentTypeMismatchException error = (MethodArgumentTypeMismatchException) ex;
        String parameter = error.getName();
        String value = (String) error.getValue();
        return  String.format("The path parameter '%s' received the value '%s' with invalid type. The correct type is Long.", parameter, value);
    }
}
