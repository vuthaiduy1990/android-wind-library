package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import the.wind.library.utils.CWCryptoUtils;
import the.wind.library.utils.CWStreamUtils;

public class CryptoUtilsTest {

    @Test
    public void encryptDecryptString() throws Exception {
        String secretKey = "v56JBdk75^&*GU156OJ^*(x"; // any length

        // Testcase 1: encrypt/decrypt short string
        {
            String origin = "Color the wind";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
            System.out.println("encryptDecryptString - case 1 - base64: " + CWCryptoUtils.encode64(encrypted));
            System.out.println("encryptDecryptString - case 1: " + CWStreamUtils.bytesToString(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 2: encrypt/decrypt long string
        {
            String origin = "Codec was formed as an attempt to focus development effort on one " +
                    "definitive implementation of the Base64 encoder. At the time of Codec's proposal," +
                    "there were approximately 34 different Java classes that dealt with Base64 encoding " +
                    "spread over the Foundation's CVS repository. Developers in the Jakarta Tomcat project" +
                    "had implemented an original version of the Base64 codec which had been copied by the " +
                    "Commons HttpClient and Apache XML project's XML-RPC sub-project. After almost one year," +
                    "the two forked versions of Base64 had significantly diverged from one another. XML-RPC " +
                    "had applied numerous fixes and patches which were not applied to the Commons HttpClient Base64." +
                    "Different subprojects had differing implementations at various levels of compliance with the";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
            System.out.println("encryptDecryptString - case 2: " + CWCryptoUtils.encode64(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 3: encrypt/decrypt with different key
        {
            String origin = "Color the wind";
            // wrong secret key
            try {
                byte[] encrypted = CWCryptoUtils.encrypt("XXX-XXX-XXX-XXX.", CWStreamUtils.stringToBytes(origin));
                System.out.println("encryptDecryptString - case 3: " + CWCryptoUtils.encode64(encrypted));
                CWCryptoUtils.decrypt("ZZZ-ZZZ-ZZZ", encrypted);
            } catch (Exception ex) {
                Assert.assertTrue("Wrong secret key - " + ex.getMessage(), true);
            }
        }

        // Testcase 4: encrypt/decrypt Vietnamese language
        {
            String origin = "Tô màu cho gió";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
            System.out.println("encryptDecryptString - case 4: " + CWCryptoUtils.encode64(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 5: encrypt/decrypt Japanese language
        {
            String origin = "風を彩る。";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
            System.out.println("encryptDecryptString - case 5: " + CWCryptoUtils.encode64(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 6: encrypt/decrypt the same string but multiple time
        {
            String origin = "Color the wind";
            for (int i = 0; i < 10; i++) {
                byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
                byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
                Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
            }
        }
    }

    @Test
    public void encodeDecode64() {
        String secret = "You never know???";
        String encode = CWCryptoUtils.encode64(CWStreamUtils.stringToBytes(secret));
        System.out.println("encode string: " + encode);
        Assert.assertEquals(secret, CWStreamUtils.bytesToString(CWCryptoUtils.decode64(encode)));
    }

    @Test
    public void hash() {
        String origin = "Color the wind";

        // MD5
        Assert.assertEquals("f4f882a8a81e7a12aae5e2edc1eb0f22",
                            CWCryptoUtils.md5(origin));
        Assert.assertEquals("f4f882a8a81e7a12aae5e2edc1eb0f22",
                            CWCryptoUtils.md5(origin));

        // SHA-1
        Assert.assertEquals("b201315c1d1f933d692fcfde21f9314e4757e86b",
                            CWCryptoUtils.sha1(origin));
        Assert.assertEquals("b201315c1d1f933d692fcfde21f9314e4757e86b",
                            CWCryptoUtils.sha1(origin));

        // SHA-256
        Assert.assertEquals("97896017546977de50263a630e3bece2c92126fc2d5f8bba70ad4f5eb3eb56eb",
                            CWCryptoUtils.sha256(origin));
        Assert.assertEquals("97896017546977de50263a630e3bece2c92126fc2d5f8bba70ad4f5eb3eb56eb",
                            CWCryptoUtils.sha256(origin));

        // SHA-512
        Assert.assertEquals("cdd39789d2e43bcc06195231e51103a1ba20373e47049048fccbfd7fca983f84f8fd19547e3a173f02f7fcaf0204d51ad7a4ccd060319885671dffdbd99a5a8b",
                            CWCryptoUtils.sha512(origin));
        Assert.assertEquals("cdd39789d2e43bcc06195231e51103a1ba20373e47049048fccbfd7fca983f84f8fd19547e3a173f02f7fcaf0204d51ad7a4ccd060319885671dffdbd99a5a8b",
                            CWCryptoUtils.sha512(origin));
    }

    @Test
    public void genSymmetricKey() {
        // Testcase 1: generate symmetric keys with the same given seed and bits
        // Expect the keys have the same value
        {
            String seed = "$^IP)O_}+BbhGI:^&*DTR";
            SecretKey key = CWCryptoUtils.generateSymmetricKey(seed, 16); // 128 bits
            String keyBase64 = CWCryptoUtils.encode64(key.getEncoded());
            System.out.println("generateSymmetricKey - case 1 - non base64: " + CWStreamUtils.bytesToString(key.getEncoded()));
            System.out.println("generateSymmetricKey - case 1 - base64: " + keyBase64);

            // check value
            Assert.assertEquals("GVOSVhedFBgAPC/NgDjlwA==", keyBase64);
            Assert.assertNotEquals(keyBase64, CWCryptoUtils.encode64(CWStreamUtils.stringToBytes(seed)));
            // check size
            Assert.assertEquals(16, key.getEncoded().length);
            Assert.assertEquals(16, CWCryptoUtils.decode64(keyBase64).length);

            // test generate key 100 times
            for (int i = 0; i < 1000; i++) {
                SecretKey sameKey = CWCryptoUtils.generateSymmetricKey(seed, 16);

                Assert.assertEquals(16, sameKey.getEncoded().length);
                Assert.assertEquals(keyBase64, CWCryptoUtils.encode64(sameKey.getEncoded()));
            }
        }

        // Testcase 2: generate symmetric keys with the same key size but different seed
        // Expect the keys have different value
        {
            String seed1 = "_(KJG2564%^%N";
            String seed2 = "%&*UJ1265bh^&I)I)";
            for (int i = 0; i < 1000; i++) {
                SecretKey key1 = CWCryptoUtils.generateSymmetricKey(seed1, 16);
                SecretKey key2 = CWCryptoUtils.generateSymmetricKey(seed2, 16);

                Assert.assertEquals(16, key1.getEncoded().length);
                Assert.assertEquals(16, key2.getEncoded().length);
                Assert.assertNotEquals(
                        CWCryptoUtils.encode64(key1.getEncoded()),
                        CWCryptoUtils.encode64(key2.getEncoded()));
            }
        }

        // Testcase 3: generate symmetric keys with the the same seed but different in key length
        // Expect the keys have different value
        {
            String seed = "_(KJG2564%^%N";
            for (int i = 0; i < 1000; i++) {
                SecretKey key1 = CWCryptoUtils.generateSymmetricKey(seed, 16);
                SecretKey key2 = CWCryptoUtils.generateSymmetricKey(seed, 32);

                Assert.assertEquals(16, key1.getEncoded().length);
                Assert.assertEquals(32, key2.getEncoded().length);
                Assert.assertNotEquals(
                        CWCryptoUtils.encode64(key1.getEncoded()),
                        CWCryptoUtils.encode64(key2.getEncoded()));
            }
        }

        // Testcase 4: generate random asymmetric key without the seed
        // Expect the keys have different value
        {
            List<String> keyList = new LinkedList<>();
            for (int i = 0; i < 1000; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, 16);
                String base64 = CWCryptoUtils.encode64(key.getEncoded());

                Assert.assertFalse(keyList.contains(base64));
                keyList.add(base64);
            }
        }

        // Testcase 5: generate asymmetric key with given key size (bits)
        {
            List<String> keyList = new LinkedList<>();
            for (int i = 1; i <= 32; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, i);
                String base64 = CWCryptoUtils.encode64(key.getEncoded());

                Assert.assertEquals(i, key.getEncoded().length);
                Assert.assertFalse(keyList.contains(base64));
                keyList.add(base64);
            }
        }
    }

    @Test
    public void generateAsymmetricKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();

        String publicKey = CWCryptoUtils.encode64(keyPair.getPublic().getEncoded());
        String privateKey = CWCryptoUtils.encode64(keyPair.getPrivate().getEncoded());
        System.out.println("public key format: " + keyPair.getPublic().getFormat());
        System.out.println("public key: " + publicKey);
        System.out.println("private key format: " + keyPair.getPrivate().getFormat());
        System.out.println("private key: " + privateKey);

        // Testcase: private/public key remain the same after encoding/decoding
        Assert.assertArrayEquals(keyPair.getPublic().getEncoded(), CWCryptoUtils.decode64(publicKey));
        Assert.assertArrayEquals(keyPair.getPrivate().getEncoded(), CWCryptoUtils.decode64(privateKey));

        // private/public key generated each time is unique
        for (int i = 0; i < 100; i++) {
            KeyPair newKeyPair = CWCryptoUtils.generateAsymmetricKey();
            Assert.assertNotEquals(publicKey, CWCryptoUtils.encode64(newKeyPair.getPublic().getEncoded()));
            Assert.assertNotEquals(privateKey, CWCryptoUtils.encode64(newKeyPair.getPrivate().getEncoded()));
        }
    }

    @Test
    public void decodePubicKey() throws Exception {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();
        String publicKey = CWCryptoUtils.encode64(keyPair.getPublic().getEncoded());
        PublicKey pk = CWCryptoUtils.decodePubicKey(CWCryptoUtils.decode64(publicKey));
        Assert.assertNotNull(pk);
        Assert.assertArrayEquals(keyPair.getPublic().getEncoded(), pk.getEncoded());
    }

    @Test
    public void decodePrivateKey() throws Exception {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();
        String privateKey = CWCryptoUtils.encode64(keyPair.getPrivate().getEncoded());
        PrivateKey pk = CWCryptoUtils.decodePrivateKey(CWCryptoUtils.decode64(privateKey));
        Assert.assertNotNull(pk);
        Assert.assertArrayEquals(keyPair.getPrivate().getEncoded(), pk.getEncoded());
    }

    @Test
    public void encryptDecryptUsingAsymmetricKey() throws Exception {
        // generate 24 bytes random data string
        byte[] originData = CWCryptoUtils.generateSymmetricKey(null, 24).getEncoded();
        KeyPair asymmetricKey = CWCryptoUtils.generateAsymmetricKey();
        byte[] encryptedData = CWCryptoUtils.encrypt(asymmetricKey.getPublic(), originData);

        // Testcase: decrypt data using private key
        {
            byte[] decryptedData = CWCryptoUtils.decrypt(asymmetricKey.getPrivate(), encryptedData);
            Assert.assertArrayEquals(originData, decryptedData);
            Assert.assertEquals(
                    CWStreamUtils.bytesToString(originData),
                    CWStreamUtils.bytesToString(decryptedData));
        }

        // Testcase: cannot decrypt data using public key
        {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, asymmetricKey.getPublic());
            Exception exception = null;
            try {
                cipher.doFinal(encryptedData);
            } catch (Exception ex) {
                exception = ex;
            }
            Assert.assertNotNull(exception);
        }

        // Testcase: cannot decrypt data using other private key
        {
            PrivateKey otherPrivateKey = CWCryptoUtils.generateAsymmetricKey().getPrivate();
            Exception exception = null;
            try {
                CWCryptoUtils.decrypt(otherPrivateKey, encryptedData);
            } catch (Exception ex) {
                exception = ex;
            }
            Assert.assertNotNull(exception);
        }
    }

    @Test
    public void encryptDecryptUsingSessionKey() throws Exception {
        String originText = "Codec was formed as an attempt to focus development effort on one " +
                "definitive implementation of the Base64 encoder. At the time of Codec's proposal," +
                "there were approximately 34 different Java classes that dealt with Base64 encoding " +
                "spread over the Foundation's CVS repository. Developers in the Jakarta Tomcat project" +
                "had implemented an original version of the Base64 codec which had been copied by the " +
                "Commons HttpClient and Apache XML project's XML-RPC sub-project. After almost one year," +
                "the two forked versions of Base64 had significantly diverged from one another. XML-RPC " +
                "had applied numerous fixes and patches which were not applied to the Commons HttpClient Base64." +
                "Different subprojects had differing implementations at various levels of compliance with the";

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 1. The app secret key which remains unchanged forever
        String appKey = "BVST^&Y()I#)_MKLBNDB164T&^*Y)i0-r";

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 2. Generate asymmetric key (public/private key)
        // These two key should be remain unchanged forever
        KeyPair asymmetricKey = CWCryptoUtils.generateAsymmetricKey();
        // Use base64 to encode public key only
        String publicKeyString = CWCryptoUtils.encode64(asymmetricKey.getPublic().getEncoded());
        // While for private key, use app secret key to encrypt
        String encryptedPrivateKeyString = CWCryptoUtils.encode64(
                CWCryptoUtils.encrypt(appKey, asymmetricKey.getPrivate().getEncoded()));

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 3. Create a random session key
        // We use this key to encrypt data before sending to other users.
        String sessionKey = "%&*&)I_BV_VB_JHI)_Y";
        // Encrypt session key using public key
        // This encrypted session key will be sent to other user
        PublicKey publicKey = CWCryptoUtils.decodePubicKey(CWCryptoUtils.decode64(publicKeyString));
        String encryptedSessionKey = CWCryptoUtils.encode64(
                CWCryptoUtils.encrypt(publicKey, CWStreamUtils.stringToBytes(sessionKey))
        );

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 3. encrypt the text using session key
        byte[] encryptedText = CWCryptoUtils.encrypt(sessionKey, CWStreamUtils.stringToBytes(originText));
        // We only can use private key to decrypt the encrypted data

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 4. For decryption: we need the following keys:
        // +++ app secret key
        // +++ encrypted private key
        // +++ encrypted session key
        // First, we decrypt private key using app secret key first
        PrivateKey privateKey = CWCryptoUtils.decodePrivateKey(
                /* decrypt encrypted private key using app secret key */
                CWCryptoUtils.decrypt(
                        appKey,
                        /* decode base64 string */
                        CWCryptoUtils.decode64(encryptedPrivateKeyString))
        );
        // Secondly, we decrypt session key using private key
        String decryptedSessionKey = CWStreamUtils.bytesToString(
                CWCryptoUtils.decrypt(privateKey, CWCryptoUtils.decode64(encryptedSessionKey))
        );
        Assert.assertEquals(sessionKey, decryptedSessionKey);
        // Thirdly, we decrypt data using session key
        String decryptedText = CWStreamUtils.bytesToString(CWCryptoUtils.decrypt(sessionKey, encryptedText));
        Assert.assertEquals(originText, decryptedText);
    }
}
