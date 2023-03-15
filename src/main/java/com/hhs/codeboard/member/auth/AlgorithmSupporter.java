package com.hhs.codeboard.member.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.hhs.codeboard.member.expt.InitiationFailPemkey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Optional;

/**
 * jwt 알고리즘 생성 보조용
 */
public class AlgorithmSupporter {

    protected static Key getPemKey(String keyPath, AlgorithmType algorithmType) throws NoSuchAlgorithmException, InitiationFailPemkey {
        // 파일 path가 빈값일경우, 자바에서 직접 키생성
        Path keyFilePath = Path.of(keyPath);
        if (!Files.exists(keyFilePath)) {
            throw new InitiationFailPemkey("incorrect private key file path");
        }
        // public key read
        KeyFactory factory = KeyFactory.getInstance(algorithmType.getInstanceType());

        try (PemReader pemReader = new PemReader(Files.newBufferedReader(keyFilePath))) {
            PemObject pemObject = pemReader.readPemObject();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pemObject.getContent());
            return factory.generatePublic(pubKeySpec);
        } catch (IOException | InvalidKeySpecException e) {
            throw new InitiationFailPemkey("fail pem key initiation");
        }
    }

    /**
     * 해당 enum은 singletone 객체처럼 사용한다.
     * enum에서 생성된 객체는 호출시 같은 주소를 가지는걸 확인.
     */
    @RequiredArgsConstructor
    @Getter
    public enum AlgorithmType {
        HS("HMAC", null, new HMAC_AlgorithmProvider())
        , RS("RSASSA-PKCS1-v1_5", "RSA", new RSA_AlgorithmProvider())
        , ES("ECDSA", "EC", new ECDSA_AlgorithmProvider())
        , PS("RSASSA-PSS", "RSASSA-PSS", new RSA_AlgorithmProvider());

        private final String title;
        private final String instanceType;
        private final AlgorithmProvider provider;

        public Algorithm getAlgorithm(AlgorithmKeySize keySize, AlgorithmDto algorithmDto) throws NoSuchAlgorithmException {
            return this.provider.supply(this, keySize, algorithmDto);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum AlgorithmKeySize {
        _256(256)
        , _384(384)
        , _512(512);

        private final int size;

        public static AlgorithmKeySize findBySize(int size) {
            return Arrays.stream(AlgorithmKeySize.values())
                    .filter(keySize->keySize.getSize()==size).findFirst()
                    .orElse(AlgorithmKeySize._384);
        }
    }

    /**
     * 각각의 알고리즘별 로직용 인터페이스
     */
    interface AlgorithmProvider {
        Algorithm supply(AlgorithmType algorithmType, AlgorithmKeySize keySize, AlgorithmDto data) throws NoSuchAlgorithmException;
    }

    static class HMAC_AlgorithmProvider implements AlgorithmProvider {
        @Override
        public Algorithm supply(AlgorithmType algorithmType, AlgorithmKeySize keySize, AlgorithmDto data) throws NoSuchAlgorithmException {
            String secretKey = Optional.ofNullable(data.getSecretKey()).orElseThrow(()->new NoSuchAlgorithmException("not found secret key"));
            Algorithm algorithm = null;
            switch (keySize) {
                case _256 -> algorithm = Algorithm.HMAC256(secretKey);
                case _384 -> algorithm = Algorithm.HMAC384(secretKey);
                case _512 -> algorithm = Algorithm.HMAC512(secretKey);
            }
            return algorithm;
        }
    }

    static class RSA_AlgorithmProvider implements AlgorithmProvider {
        @Override
        public Algorithm supply(AlgorithmType algorithmType, AlgorithmKeySize keySize, AlgorithmDto data) throws NoSuchAlgorithmException {
            RSAPublicKey publicKey = (RSAPublicKey) getPemKey(data.getPublicKeyPath(), AlgorithmType.RS);
            RSAPrivateKey privateKey = (RSAPrivateKey) getPemKey(data.getPrivateKeyPath(), AlgorithmType.RS);
            Algorithm algorithm = null;
            switch (keySize) {
                case _256 -> algorithm = Algorithm.RSA256(publicKey, privateKey);
                case _384 -> algorithm = Algorithm.RSA384(publicKey, privateKey);
                case _512 -> algorithm = Algorithm.RSA512(publicKey, privateKey);
            }
            return algorithm;
        }
    }

    static class ECDSA_AlgorithmProvider implements AlgorithmProvider {
        @Override
        public Algorithm supply(AlgorithmType algorithmType, AlgorithmKeySize keySize, AlgorithmDto data) throws NoSuchAlgorithmException {
            ECPublicKey publicKey = (ECPublicKey) getPemKey(data.getPublicKeyPath(), AlgorithmType.ES);
            ECPrivateKey privateKey = (ECPrivateKey) getPemKey(data.getPrivateKeyPath(), AlgorithmType.ES);
            Algorithm algorithm = null;
            switch (keySize) {
                case _256 -> throw new NoSuchAlgorithmException("this verification is disabled in java15");
                case _384 -> algorithm = Algorithm.ECDSA384(publicKey, privateKey);
                case _512 -> algorithm = Algorithm.ECDSA512(publicKey, privateKey);
            }
            return algorithm;
        }
    }

    /**
     * 알고리즘 생성에 필요한 정보를 담은 DTO
     */
    @Getter
    static class AlgorithmDto {

        // 일반
        protected AlgorithmDto (String secretKey, String publicKeyPath, String privateKeyPath, AlgorithmType type, AlgorithmKeySize size) {
            this.secretKey = secretKey;
            this.privateKeyPath = privateKeyPath;
            this.publicKeyPath = publicKeyPath;
            this.type = type;
            this.size = size;
        }

        // 키 자바생성용
        protected AlgorithmDto (AlgorithmType type) {
            this.type = type;
        }

        private String privateKeyPath;
        private String publicKeyPath;
        private String secretKey;
        private AlgorithmType type;
        private AlgorithmKeySize size;
    }

}
