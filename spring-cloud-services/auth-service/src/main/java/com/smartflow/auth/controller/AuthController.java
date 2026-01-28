package com.smartflow.auth.controller;

import com.smartflow.auth.dto.LoginRequest;
import com.smartflow.auth.dto.LoginResponse;
import com.smartflow.auth.entity.User;
import com.smartflow.auth.service.AuthService;
import com.smartflow.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (user != null && user.getIsActive()) {
                String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole().name());
                Long expiresIn = jwtService.getRemainingTime(token);
                
                LoginResponse response = new LoginResponse(token, user.getId(), user.getUsername(), 
                    user.getRole().name(), expiresIn);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("用户名或密码错误，或账户已被禁用");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证令牌
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.getUsernameFromToken(token);
                Long userId = jwtService.getUserIdFromToken(token);
                String role = jwtService.getRoleFromToken(token);
                
                if (jwtService.validateToken(token, username)) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username,
                        "userId", userId,
                        "role", role
                    ));
                }
            }
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "无效的令牌"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "令牌验证失败"));
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User savedUser = authService.register(user);
            return ResponseEntity.ok(Map.of("message", "注册成功", "userId", savedUser.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.getUsernameFromToken(token);
                
                if (jwtService.validateToken(token, username)) {
                    User user = authService.findByUsername(username);
                    if (user != null && user.getIsActive()) {
                        String newToken = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole().name());
                        Long expiresIn = jwtService.getRemainingTime(newToken);
                        
                        LoginResponse response = new LoginResponse(newToken, user.getId(), user.getUsername(), 
                            user.getRole().name(), expiresIn);
                        
                        return ResponseEntity.ok(response);
                    }
                }
            }
            return ResponseEntity.badRequest().body("令牌刷新失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("令牌刷新失败: " + e.getMessage());
        }
    }
}

