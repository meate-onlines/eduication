package com.eduication.language.service;

import com.eduication.language.entity.UserAccount;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.UserAccountRepository;
import com.eduication.language.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Map<String, Object> register(String username, String email, String password) {
        if (userAccountRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        if (userAccountRepository.existsByEmail(email)) {
            throw new BusinessException("邮箱已存在");
        }
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setVip(false);
        UserAccount saved = userAccountRepository.save(account);
        String token = jwtService.generateToken(saved.getUsername(), saved.getId());
        return buildAuthResult(saved, token);
    }

    public Map<String, Object> login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        String token = jwtService.generateToken(account.getUsername(), account.getId());
        return buildAuthResult(account, token);
    }

    private Map<String, Object> buildAuthResult(UserAccount account, String token) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", account.getId());
        profile.put("username", account.getUsername());
        profile.put("email", account.getEmail());
        profile.put("vip", account.isVip());
        profile.put("vipExpireAt", account.getVipExpireAt());
        profile.put("role", account.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("profile", profile);
        return result;
    }
}
