/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 20.05.2024
 */

package ru.crystal2033.jwtapp2.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeysFromFileExtractor {

    KeysFromFileExtractor(){
        java.security.Security.addProvider(
                new BouncyCastleProvider()
        ); //This helps to create private RSA KEY (Error was "Algid parse error, not a sequence" when PKCS8EncodedKeySpec(encoded))
    }


    private String readFile(final String fileName) throws IOException {
        final File file = new File(fileName);
        return new String(Files.readAllBytes(file.toPath()));
    }
    public PrivateKey getPrivateKeyFromFile(String fileName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String pemString = readFile(fileName);

        String privateKeyPEM = pemString
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replaceAll("\\r*\\n", "")
                .replace("-----END RSA PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);

    }

    public PublicKey getPublicKeyFromFile(String fileName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String pemString = readFile(fileName);

        String publicKeyPEM = pemString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("\\r*\\n", "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);

    }
}
