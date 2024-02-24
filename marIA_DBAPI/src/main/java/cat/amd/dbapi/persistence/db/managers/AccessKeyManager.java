package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AccessKeyManager {

    private AccessKeyManager() {

    }

    /**
     * Generates an AccessKey for desired user
     *
     * @param user user to generate access key into
     * @return access key
     */
    public static String generateAccessKey(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);

        Date currentDate = new Date();
        Date expirationDate = calendar.getTime();
        Algorithm algorithm = Algorithm.HMAC256(user.getNickname());

        return JWT.create()
                .withIssuer(user.getId().toString())
                .withSubject("access token")
                .withIssuedAt(currentDate)
                .withExpiresAt(expirationDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    /**
     * Verifies the received access key
     *
     * @param user user that sent the key
     * @return decoded key
     * @throws JWTVerificationException if not valid throws exception
     */
    public static DecodedJWT verifyAccessKey(User user) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(user.getNickname());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(user.getId().toString())
                .build();

        return verifier.verify(user.getAccessKey());
    }


}
