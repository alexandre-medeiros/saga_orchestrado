package br.com.himax.saga.orchestration.inventory.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * RestControllerAdvice that handles exceptions and provides a standardized
 * problem details response following RFC 9457 - Problem Details for HTTP APIs.
 *
 * @see <a href="https://www.rfc-editor.org/info/rfc9457">RFC 9457</a>
 */
@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private HttpServletRequest servletRequest;
    private ErrorHandler handler;

    /*
     * This method customize the response body (problemDetail) following RFC 9457 for exceptions
     * threaded by spring framework inside ResponseEntityExceptionHandler
     */

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = handler.handleException(ex, status, servletRequest);
        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    /*
     * If you want to customize some handler specific for any reason, you can override the specific
     * spring framework ResponseEntityExceptionHandler handler.
     * For example, I customize the detail field of problem detail because I do not want to show
     * details of my api inside my response.
     */

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = handler.handleException(ex, status, servletRequest);
        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
    /*
     * This method customize the response body (problemDetail) following RFC 9457 for exceptions
     * that I created
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleBusinessException(Exception ex, WebRequest request) {
        HttpStatus status = handler.getHttpStatus(ex);
        ProblemDetail problemDetail = handler.handleException(ex, status, servletRequest);
        return super.handleExceptionInternal(ex, problemDetail, HttpHeaders.EMPTY, status, request);
    }
}
