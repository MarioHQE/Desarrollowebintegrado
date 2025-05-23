package com.springboot.desarrolloweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.usuario;

@Service
public class userdetailsservice implements UserDetailsService {
    @Autowired
    usuariorepository userdao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        usuario user = userdao.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

}
