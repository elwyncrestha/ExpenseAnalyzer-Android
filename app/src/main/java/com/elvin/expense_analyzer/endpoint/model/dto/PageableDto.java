package com.elvin.expense_analyzer.endpoint.model.dto;

import java.util.List;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/9/2020
 */
@Data
public class PageableDto<T> {
    private List<T> content;
    private Long totalElements;
}
