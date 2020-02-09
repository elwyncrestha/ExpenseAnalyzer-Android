package com.elvin.expense_analyzer.endpoint.model.dto;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/9/2020
 */
@Data
public class CategoryCountDto {
    private Long incomeCount;
    private Long expenseCount;
}
