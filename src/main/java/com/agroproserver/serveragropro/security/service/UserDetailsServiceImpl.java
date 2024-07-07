package com.agroproserver.serveragropro.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.service.UsuarioService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    UsuarioService usuarioService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Usuario usuario = usuarioService.getByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario " + username + "no encontrado"));
        
        return UserDetailsImpl.build(usuario);
    }

}
