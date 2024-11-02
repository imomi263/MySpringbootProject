package com.example.controller;

import com.alibaba.fastjson2.JSON;
import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset)")String type,
                                        HttpServletRequest request) {
//        String message= accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr());
//        if(message==null){
//            return RestBean.<Void>success();
//        }else{
//           return RestBean.failure(400,message);
//        }
        return this.messageHandle(()->
                accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return this.messageHandle(() -> accountService.registerEmailAccount(vo));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message=action.get();
        if(message==null){
            return RestBean.<Void>success();
        }else{
            return RestBean.failure(400,message);
        }
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return this.messageHandle(() -> accountService.resetConfirm(vo));
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
        return this.messageHandle(() -> accountService.resetEmailAccountPassword(vo));
    }
}












