package com.eduication.language.service;

import com.eduication.language.entity.UserAccount;
import com.eduication.language.exception.BusinessException;
import com.eduication.language.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("未获取到当前用户");
        }
        String username = authentication.getName();
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("当前用户不存在"));
    }
}
