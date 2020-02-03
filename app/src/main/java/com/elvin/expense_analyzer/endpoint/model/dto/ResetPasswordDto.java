package com.elvin.expense_analyzer.endpoint.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    private String email;
    private String token;
    private String password;
}
