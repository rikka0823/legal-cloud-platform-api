package SmartLegalSearch.exception;

import SmartLegalSearch.constants.ResMessage;
import SmartLegalSearch.vo.BasicRes;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/*
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * @ControllerAdvice: 用來處理所有異常
 * @ResponseBody: 將返回結果轉為 JSON 格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
     * @ExceptionHandler: 用來指定當捕獲到的特定異常，如 BindException
     * BindException: 所有Spring Boot 中，有使用@註解標示的錯誤
     * 適用使用 @Valid 時
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BindException e) {
        // 建立 Map 儲存@註解的錯誤欄位和訊息
        Map<String, String> errors = new HashMap<>();
        // getBindingResult(): 取得所有錯誤的物件，getAllErrors() 則是將不同錯誤轉成 List
        e.getBindingResult().getAllErrors().forEach(error -> {
           String fieldName = ((FieldError) error).getField();
           String errorMessage = error.getDefaultMessage();
           errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException: @NotNull、@Size 等約束條件，適用使用 @Validated 時
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        // getConstraintViolations(): 返回的是一個 Set
        e.getConstraintViolations().forEach(violation -> {
            // getPropertyPath().toString(): 取得違規對應的屬性名稱，並將其轉換為字串
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // MaxUploadSizeExceededException: 處理上傳檔案大小的問題
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BasicRes> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        BasicRes response = new BasicRes(ResMessage.FILE_TOO_LARGE.getCode(), ResMessage.FILE_TOO_LARGE.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}