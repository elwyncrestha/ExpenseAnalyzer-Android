package com.elvin.expense_analyzer.endpoint.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    @SerializedName(value = "_id")
    private String id;

    @SerializedName(value = "__v")
    private Long version;
}
