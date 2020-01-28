package the.wind.library.utils;

import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * In this library
 * <ul>
 * <li>For symmetric key: we use AES (Cipher algorithm) and GCM (Galois/Counter Mode) </li>
 * <li>For asymmetric key: we use RAS (Cipher algorithm) and CBC (Cipher Block Chaining Mode)</li>
 * </ul>
 *
 * <p>
 * Encrypt/Decrypt using symmetric key.
 * <pre><code>
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      // For encryption:
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      1. encrypt(app_secret_key, user_secret_key) -> encrypted_key (should be stored with user info)
 *      2. encrypt(user_secret_key, data) -> encrypted_data
 *
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      // For decryption: need user_secret_key and app_secret_key
 *      // The attacker need two keys to decrypt the data
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      1. decrypted_key = decrypt(app_secret_key, encrypted_key) -> user_secret_key
 *      2. decrypt(user_secret_key, encrypted_data) -> data
 * </code></pre>
 * <p>
 * <p>
 * Encrypt/Decrypt using asymmetric key.
 * <pre><code>
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      // For encryption:
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      1. encrypt(app_secret_key, private_key) -> encrypted_private_key (should be stored with user info)
 *      2. generate random session_key.
 *      3. encrypt(session_key, public_key) -> encrypted_session_key (will sent to other users)
 *      4. encrypt(session_key, data) -> encrypted_data
 *
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      // For decryption: need encrypted_session_key and encrypted_private_key and app_secret_key
 *      // The attacker need three keys to decrypt the data
 *      // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *      1. decrypt(app_secret_key, encrypted_private_key) -> private_key
 *      2. decrypt(private_key, encrypted_session_key) -> session_key
 *      3. decrypt(session_key, encrypted_data) -> data
 * </code></pre>
 * <p>
 * --->> extremely secure
 * <p>
 * Note: See CryptoUtilsTest#encryptDecryptUsingAsymmetricKey() for the case of using encrypt with asymmetric key
 * <p>
 * Thanks to:
 * http://tutorials.jenkov.com/java-cryptography/keypairgenerator.html
 * https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 * https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html
 * https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation
 * https://proprivacy.com/guides/aes-encryption
 * https://www.devglan.com/java8/rsa-encryption-decryption-java
 */
public final class CWCryptoUtils {

    /**
     * Encrypt data using symmetric key with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey secret key (can be any size)
     * @param data      byte value
     * @return encrypted data
     *
     * @throws Exception exception
     */
    private static byte[] encrypt(SecretKey secretKey, byte[] data) throws Exception {
        // Generate initialization vector.
        // For GCM a 12 byte random byte-array is recommend by NIST because it’s faster and more secure
        byte[] ivBytes = generateSymmetricKey(null, 12).getEncoded();

        // Encrypt data by using AES with GCM (Galois/Counter Mode)
        // Because GCM provides authentication and also slightly faster than CBC
        // since it uses hardware acceleration
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes); //128 bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] cipherData = cipher.doFinal(data);

        // Attach initialization vector to encrypted data
        // each data will contain its own random initialization vector
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + ivBytes.length + cipherData.length);
        byteBuffer.putInt(ivBytes.length);
        byteBuffer.put(ivBytes);
        byteBuffer.put(cipherData);

        // clear keys bytes date for the purpose of security
        Arrays.fill(secretKey.getEncoded(), (byte) 0);
        Arrays.fill(ivBytes, (byte) 0);

        // return encrypted data
        return byteBuffer.array();
    }

    /**
     * Encrypt data using symmetric key with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey secret key (can be any size)
     * @param data      byte value
     * @return encrypted data
     *
     * @throws Exception exception
     */
    public static byte[] encrypt(String secretKey, byte[] data) throws Exception {
        // generate 16 bytes = 128 bits secret key
        SecretKey key = generateSymmetricKey(secretKey, 16);
        return encrypt(key, data);
    }

    /**
     * Encrypt data using asymmetric key with RSA (Cipher algorithm) and CBC (Cipher Block Chaining Mode)
     *
     * @param publicKey public key
     * @param data      less than 245 bytes
     * @return encrypted data
     *
     * @throws Exception exception
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        // Encrypt data by asymmetric key with RSA and CBC
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * Decrypt encrypted data using symmetric key with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey     secret key (can be any size)
     * @param encryptedData encrypted data
     * @return decrypted data
     *
     * @throws Exception exception
     */
    private static byte[] decrypt(SecretKey secretKey, byte[] encryptedData) throws Exception {
        // extract initialization vector and cipher data from encrypted data
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        int ivLength = byteBuffer.getInt();
        if (ivLength != 12) {
            throw new IllegalArgumentException("invalid iv length");
        }
        byte[] ivBytes = new byte[ivLength];
        byteBuffer.get(ivBytes);
        byte[] cipherData = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherData);

        // Decrypt cipher data by using AES with GCM (Galois/Counter Mode)
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes); //128 bit auth tag length
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] decryptedData = cipher.doFinal(cipherData);

        // clear keys bytes date for the purpose of security
        Arrays.fill(secretKey.getEncoded(), (byte) 0);
        Arrays.fill(ivBytes, (byte) 0);

        // return decrypted data
        return decryptedData;
    }

    /**
     * Decrypt encrypted data using symmetric key with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey     secret key (can be any size)
     * @param encryptedData encrypted data
     * @return decrypted data
     *
     * @throws Exception exception
     */
    public static byte[] decrypt(String secretKey, byte[] encryptedData) throws Exception {
        // generate 16 bytes = 128 bits secret key
        SecretKey key = generateSymmetricKey(secretKey, 16);
        return decrypt(key, encryptedData);
    }

    /**
     * Decrypt data using asymmetric key with RSA (Cipher algorithm) and CBC (Cipher Block Chaining Mode)
     *
     * @param privateKey    private key
     * @param encryptedData encrypted data
     * @return decrypted data
     *
     * @throws Exception exception
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Encode byte data to string
     *
     * @param data byte data
     * @return encoded base64 string
     */
    public static String encode64(byte[] data) {
        return CWStreamUtils.bytesToString(Base64.encodeBase64(data));
    }

    /**
     * Decode string to byte data
     *
     * @param encode encoded string
     * @return decoded byte data
     */
    public static byte[] decode64(String encode) {
        return Base64.decodeBase64(CWStreamUtils.stringToBytes(encode));
    }

    /**
     * Convert string to hash using specific algorithm
     * Available algorithm
     * https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html#messagedigest-algorithms
     * <pre>
     *     MD2, MD5
     *     SHA-1, SHA-224, SHA-256, SHA-384, SHA-512, SHA-512/224, SHA-512/256
     *     SHA3-224, SHA3-256, SHA3-384, SHA3-512
     * </pre>
     *
     * @param text original string
     * @return hash string
     */
    @Nullable
    public static String hash(String text, String algorithm) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(CWStreamUtils.stringToBytes(text), 0, text.length());
            byte[] hashByte = md.digest();
            return CWStreamUtils.bytesToHex(hashByte);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Convert string to hash using md5 algorithm
     *
     * @param text string
     * @return MD5 hash string
     */
    @Nullable
    public static String md5(String text) {
        return hash(text, "MD5");
    }

    /**
     * Convert string to hash using SHA-1 algorithm
     *
     * @param text string
     * @return SHA1 hash string
     */
    @Nullable
    public static String sha1(String text) {
        return hash(text, "SHA-1");
    }

    /**
     * Convert string to hash using SHA-256 algorithm
     *
     * @param text string
     * @return SHA256 hash string
     */
    @Nullable
    public static String sha256(String text) {
        return hash(text, "SHA-256");
    }

    /**
     * Convert string to hash using SHA-512 algorithm
     *
     * @param text string
     * @return SHA512 hash string
     */
    @Nullable
    public static String sha512(String text) {
        return hash(text, "SHA-512");
    }

    /**
     * Generate a symmetric key using AES algorithm
     * If seed is not given, this function will generate a random key each time
     * Otherwise, It will generate the same key with the same given seed
     * https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html#keygenerator-algorithms
     *
     * @param seed    a user generated seed.
     * @param keySize unit is bytes
     * @return a symmetric key with the given size and seed
     */
    public static SecretKey generateSymmetricKey(@Nullable String seed, int keySize) {
        SecureRandom secureRandom;
        if (seed != null) {
            secureRandom = new SecureRandom(CWStreamUtils.stringToBytes(seed));
        } else {
            secureRandom = new SecureRandom();
        }
        byte[] key = new byte[keySize];
        secureRandom.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

    /**
     * Generate asymmetric keypair
     *
     * @return a keypair (private key + public key)
     */
    public static KeyPair generateAsymmetricKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Convert bytes data to public key.
     * Public key format: X.509
     *
     * @param data byte data
     * @return public key
     */
    public static PublicKey decodePubicKey(@NonNull byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Convert bytes data to private key
     * Private key format: PKCS#8
     *
     * @param data byte data
     * @return private key
     */
    public static PrivateKey decodePrivateKey(@NonNull byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

}
