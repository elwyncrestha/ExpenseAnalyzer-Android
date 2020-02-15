package com.elvin.expense_analyzer.endpoint.model.dto;

import com.elvin.expense_analyzer.endpoint.model.Expense;

import java.util.List;

import lombok.Data;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@Data
public class IncomeExpenseCollection {
    private List<Expense> income;
    private List<Expense> expense;
}
