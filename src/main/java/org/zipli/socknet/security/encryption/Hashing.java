package org.zipli.socknet.security.encryption;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Hashing {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    public String generateHashing(String idChat) {

        return Jwts.builder()
                .setSubject(idChat)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

//    public String getIdChatByHashing(String hashingIdChat) {
//        return Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(hashingIdChat)
//                .getBody()
//                .getSubject();
//    }

}
