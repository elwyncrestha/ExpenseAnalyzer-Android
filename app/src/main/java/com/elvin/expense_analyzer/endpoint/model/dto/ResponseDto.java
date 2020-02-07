package com.elvin.expense_analyzer.endpoint.model.dto;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/4/2020
 */
@Data
public class ResponseDto<T> {
    private T detail;
    private Object error;
}
