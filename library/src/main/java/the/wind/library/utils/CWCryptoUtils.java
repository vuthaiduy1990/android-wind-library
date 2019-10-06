package the.wind.library.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.Nullable;

/**
 * In this library, we use AES (Cipher algorithm) and GCM (Galois/Counter Mode) for encrypting/decrypting data
 *
 * <p>
 * The best usage of encrypt library.
 * For encryption:
 * <pre><code>
 *      // 1. hash the user generated secret key.
 *      hash_key = sha1(user_secret_key);
 *      // 2. encrypt the user hash key with app secret key
 *      // This encrypted_hash key should be stored together with the user's info.
 *      // We can use Base64 to encode the encrypted_hash to string before storing it with user's info
 *      encrypted_hash = encrypt(app_secret_key, hash_key);
 *      // 3. Use encrypted user hash key to encrypt data
 *      encrypted_data =  encrypt(hash_key, data);
 * </code></pre>
 * <p>
 * For decryption
 * <pre><code>
 *      // 1. decrypt the user hash key with app secret key
 *      hash_key = decrypt(app_secret_key, encrypted_hash);
 *      // 2. decrypt data with the hash_key
 *      encrypted_data =  decrypt(hash_key, data);
 * </code></pre>
 * So the attacker need two keys (user_secret key and app_secret key) to decrypt the data
 * --->> extremely secure
 *
 * <p>
 * Thanks to:
 * http://tutorials.jenkov.com/java-cryptography/keypairgenerator.html
 * https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 * https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html
 * https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation
 * https://proprivacy.com/guides/aes-encryption
 */
public final class CWCryptoUtils {

    /**
     * Encrypt data with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey secret key (can be any size)
     * @param data      byte value
     * @return encrypted data
     * @throws Exception exception
     */
    public static byte[] encrypt(String secretKey, byte[] data) throws Exception {
        // generate 16 bytes = 128 bits secret key
        SecretKey key = generateSymmetricKey(secretKey, 16);

        // Generate initialization vector.
        // For GCM a 12 byte random byte-array is recommend by NIST because it’s faster and more secure
        byte[] ivBytes = generateSymmetricKey(null, 12).getEncoded();

        // Encrypt data by using AES with GCM (Galois/Counter Mode)
        // Because GCM provides authentication and also slightly faster than CBC
        // since it uses hardware acceleration
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes); //128 bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] cipherData = cipher.doFinal(data);

        // Attach initialization vector to encrypted data
        // each data will contain its own random initialization vector
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + ivBytes.length + cipherData.length);
        byteBuffer.putInt(ivBytes.length);
        byteBuffer.put(ivBytes);
        byteBuffer.put(cipherData);
        Arrays.fill(key.getEncoded(), (byte) 0);
        Arrays.fill(ivBytes, (byte) 0);
        return byteBuffer.array();
    }

    /**
     * Encrypt data with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey secret key (can be any size)
     * @param data      string value
     * @return encrypted data
     * @throws Exception exception
     */
    public static byte[] encrypt(String secretKey, String data) throws Exception {
        return encrypt(secretKey, data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypt encrypted data with AES (Cipher algorithm) and GCM (Galois/Counter Mode)
     *
     * @param secretKey     secret key (can be any size)
     * @param encryptedData encrypted data
     * @return decrypted data
     * @throws Exception exception
     */
    public static byte[] decrypt(String secretKey, byte[] encryptedData) throws Exception {
        // generate 16 bytes = 128 bits secret key
        SecretKey key = generateSymmetricKey(secretKey, 16);

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
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedData = cipher.doFinal(cipherData);
        Arrays.fill(key.getEncoded(), (byte) 0);
        Arrays.fill(ivBytes, (byte) 0);
        return decryptedData;
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
            md.update(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
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
            secureRandom = new SecureRandom(seed.getBytes(StandardCharsets.UTF_8));
        } else {
            secureRandom = new SecureRandom();
        }
        byte[] key = new byte[keySize];
        secureRandom.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

}
