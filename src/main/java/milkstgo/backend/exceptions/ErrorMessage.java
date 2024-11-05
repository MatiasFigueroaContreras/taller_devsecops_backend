package milkstgo.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {
    private Integer status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;


    private ErrorMessage() {
        timestamp = LocalDateTime.now();
    }

    ErrorMessage(Integer status) {
        this();
        this.status = status;
        this.message = "Se ha producido un error inesperado";
    }

    ErrorMessage(Integer status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}