package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailResetVO extends ConfirmResetVO{
//    @Email
//    String email;
//
//    @Length(min=6,max=6)
//    String code;

    @Length(min=5,max=20)
    String password;
}
