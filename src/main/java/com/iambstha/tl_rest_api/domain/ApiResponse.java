package com.iambstha.tl_rest_api.domain;

import com.iambstha.tl_rest_api.constant.StatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
@XmlRootElement
@Builder
@NoArgsConstructor
public class ApiResponse {

    @Schema(description = "Status of request")
    private String status;

    @Schema(description = "Status code of request")
    private Integer statusCode;

    @Schema(description = "Message returned by request")
    private String message;

    @Schema(description = "Detailed information returned by request")
    private List<String> details;

    @Schema(description = "Detailed information of the error if occurred")
    private String errorMessage;

    @Schema(description = "Data returned by request")
    private Object data;

    public ApiResponse(String status, Integer statusCode, String message, List<String> details, String errorMessage, Object data) {
        super();
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public ApiResponse(String status, String message, List<String> details) {
        super();
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public static ApiResponseBuilder builder() {
        ApiResponseBuilder builder = new ApiResponseBuilder();
        builder.status(StatusConstants.OK);
        builder.statusCode(Response.SC_OK);
        return builder;
    }

}
