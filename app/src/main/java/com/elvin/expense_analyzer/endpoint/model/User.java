package com.elvin.expense_analyzer.endpoint.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Object tokens;
    private String image;

}
