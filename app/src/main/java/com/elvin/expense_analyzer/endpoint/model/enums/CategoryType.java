package com.elvin.expense_analyzer.endpoint.model.enums;

import androidx.annotation.NonNull;

/**
 * @author Elvin Shrestha on 2/5/2020
 */
public enum CategoryType {
    EXPENSE("Expense"),
    INCOME("Income");

    private String value;

    CategoryType(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }

    public static CategoryType getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (CategoryType v : values())
            if (value.equalsIgnoreCase(v.toString()))
                return v;
        throw new IllegalArgumentException();
    }
}
