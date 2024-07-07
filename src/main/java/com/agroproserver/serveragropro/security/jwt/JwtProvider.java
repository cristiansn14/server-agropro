package com.agroproserver.serveragropro.security.jwt;

import java.util.Date;

import java.security.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.agroproserver.serveragropro.security.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtProvider {
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication){
        UserDetailsImpl usuarioPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject(usuarioPrincipal.getUsername())
            .setIssuedAt(new Date())               
            .setExpiration(new Date(new Date(expiration).getTime() + expiration * 1000))
            .signWith(getKey(), SignatureAlgorithm.HS512).compact();
    }

    private Key getKey(){
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String getUsenameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        catch (SignatureException e){
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }

        return false;
    }
}


