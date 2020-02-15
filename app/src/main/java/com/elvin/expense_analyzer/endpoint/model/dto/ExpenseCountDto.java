package com.elvin.expense_analyzer.endpoint.model.dto;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/12/2020
 */
@Data
public class ExpenseCountDto {
    private Long incomeCount;
    private Long expenseCount;
}
