package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;


    @Value("${spring.security.jwt.expire}")
    int expire;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public String createJwt(UserDetails userDetails,int id,String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24*expire);
        return calendar.getTime();
    }

    public DecodedJWT resolveJwt(String token){
        String m_token=this.convertToken(token);
        if(m_token==null){
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt=jwtVerifier.verify(m_token);
            if(this.isInvalidToken(jwt.getId())){
                return null;
            }
            Date expireAt=jwt.getExpiresAt();
            return new Date().after(expireAt)?null:jwt;
        }catch(JWTVerificationException e){
            return null;
        }
    }

    private String convertToken(String token){
        if (token == null) {
            return null;
        }
        if(token.startsWith("Bearer ")){
            return token.substring(7);
        }
        else return token;

    }

    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims=jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("***")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT jwt){
        Map<String,Claim> claims=jwt.getClaims();
        return claims.get("id").asInt();
    }

    private boolean deleteToken(String uuid,Date time){
        if(this.isInvalidToken(uuid)){
            return false;
        }
        Date now=new Date();
        long expire=Math.max(0,time.getTime()-now.getTime());
        stringRedisTemplate.opsForValue().set(Const.JWT_BALCK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uid){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BALCK_LIST + uid));
    }

    public boolean invalidateJwt(String headerToken){
        String token=this.convertToken(headerToken);
        if(token==null){
            return false;
        }
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt=jwtVerifier.verify(token);
            String id=jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }catch(JWTVerificationException e){
            return false;
        }
    }
}
