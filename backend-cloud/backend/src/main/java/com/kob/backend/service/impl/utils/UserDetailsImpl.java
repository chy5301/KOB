package com.kob.backend.service.impl.utils;

import com.kob.backend.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private User user;

    /**
     * 获取用户权限集合
     *
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 获取用户密码
     *
     * @return 用户密码
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 检查用户帐户是否未过期
     *
     * @return 如果用户帐户未过期，返回true；否则返回false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 检查用户帐户是否未被锁定
     *
     * @return 如果用户帐户未被锁定，返回true；否则返回false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 检查用户凭证是否未过期
     *
     * @return 如果用户凭证未过期，返回true；否则返回false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 检查用户是否已启用
     *
     * @return 如果用户已启用，返回true；否则返回false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
