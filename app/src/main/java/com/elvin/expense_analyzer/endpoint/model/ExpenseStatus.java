package com.elvin.expense_analyzer.endpoint.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Elvin Shrestha on 2/5/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExpenseStatus extends BaseEntity {
    private String name;
}
