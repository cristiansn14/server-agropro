package com.agroproserver.serveragropro.security.service;

import java.security.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioFinca;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails{

    private UUID id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private String codigoPostal;
    private String cuenta;
    private String username;
    private String password;   
    private boolean superUsuario;
    private Timestamp fechaAlta;
    private Timestamp fechaBaja;
    //private Collection<? extends GrantedAuthority> authorities;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private HashSet<UsuarioFinca> usuarioFincas;

    public static UserDetailsImpl build(Usuario usuario){
        // List<GrantedAuthority> authorities = 
        //          usuario.getRoles.stream().map(rol -> new SimpleGrandedAuthority(rol
        //          .getRol().name())).collect(Collectors.toList());

        return new UserDetailsImpl(usuario.getId(), usuario.getNombre(), usuario.getApellido1(), usuario.getApellido2(), 
                                    usuario.getDni(), usuario.getEmail(), usuario.getTelefono(), usuario.getDireccion(), 
                                    usuario.getCodigoPostal(), usuario.getCuenta(), usuario.getUsername(), usuario.getPassword(), 
                                    usuario.isSuperUsuario(), usuario.getFechaAlta(), usuario.getFechaBaja(), usuario.getUsuarioFincas());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
        //return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
