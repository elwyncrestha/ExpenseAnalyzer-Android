package com.elvin.expense_analyzer.endpoint.model;

import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;

import java.util.Date;

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
public class Expense extends BaseEntity {
    private Date date;
    private String time;
    private Double amount;
    private String payeeOrPayer;
    private Object category;
    private Object paymentMethod;
    private Object status;
    private String description;
    private CategoryType type;
}
