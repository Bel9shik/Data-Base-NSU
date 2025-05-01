package nsu.kardash.backendsportevents.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class VerifyCodeErrorResponse implements Serializable {

    private String error;
    private long timestamp;

}
