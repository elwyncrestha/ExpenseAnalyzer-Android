package com.elvin.expense_analyzer.endpoint.model.dto;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@Data
public class TransactionDurationDto {
    private IncomeExpenseCollection today;
    private IncomeExpenseCollection thisWeek;
    private IncomeExpenseCollection thisMonth;
    private IncomeExpenseCollection thisYear;
}