package the.wind.library;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import the.wind.library.utils.CWCryptoUtils;
import the.wind.library.utils.CWFileUtils;
import the.wind.library.utils.CWStreamUtils;


public class CryptoUtilsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File testFile;
    private File publicKeyFile;
    private File privateKeyFile;
    private File sessionKeyFile;
    private File appKeyFile;

    @Before
    public void beforeEachTest() {
        testFile = new File(folder.getRoot(), "crypto.test");
        publicKeyFile = new File(folder.getRoot(), "publicKeyFile");
        privateKeyFile = new File(folder.getRoot(), "privateKeyFile");
        sessionKeyFile = new File(folder.getRoot(), "sessionKeyFile");
        appKeyFile = new File(folder.getRoot(), "appKeyFile");
    }

    @After
    public void afterEachTest() {
        CWFileUtils.deleteFile(testFile);
        CWFileUtils.deleteFile(publicKeyFile);
        CWFileUtils.deleteFile(privateKeyFile);
        CWFileUtils.deleteFile(sessionKeyFile);
        CWFileUtils.deleteFile(appKeyFile);
    }

    @Test
    public void encryptDecryptString() throws Exception {
        String seed = "v56JBdk75^&*GU156OJ^*(x"; // any length
        byte[] secretKey = CWCryptoUtils.generateSymmetricKey(seed, 16).getEncoded();

        // Testcase 1: encrypt/decrypt short string
        {
            String origin = "Color the wind";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
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
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 3: encrypt/decrypt with different key
        {
            String origin = "Color the wind";
            // wrong secret key
            try {
                byte[] encrypted = CWCryptoUtils.encrypt(CWStreamUtils.stringToBytes("XXX-XXX-XXX-XXX."), CWStreamUtils.stringToBytes(origin));
                CWCryptoUtils.decrypt(CWStreamUtils.stringToBytes("ZZZ-ZZZ-ZZZ"), encrypted);
            } catch (Exception ex) {
                Assert.assertTrue("Wrong secret key - " + ex.getMessage(), true);
            }
        }

        // Testcase 4: encrypt/decrypt Vietnamese language
        {
            String origin = "Tô màu cho gió";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 5: encrypt/decrypt Japanese language
        {
            String origin = "風を彩る。";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, CWStreamUtils.stringToBytes(origin));
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
        // Expect the keys have different value
        {
            String seed = "$^IP)O_}+BbhGI:^&*DTR";
            SecretKey key = CWCryptoUtils.generateSymmetricKey(seed, 16); // 128 bits
            byte[] encode = key.getEncoded();

            // check value
            Assert.assertFalse(Arrays.equals(encode, CWStreamUtils.stringToBytes(seed)));

            // check size
            Assert.assertEquals(16, key.getEncoded().length);

            // test generate key 100 times
            for (int i = 0; i < 1000; i++) {
                SecretKey sameKey = CWCryptoUtils.generateSymmetricKey(seed, 16);

                Assert.assertEquals(16, sameKey.getEncoded().length);
                Assert.assertFalse(Arrays.equals(encode, sameKey.getEncoded()));
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
                Assert.assertNotEquals(key1.getEncoded(), key2.getEncoded());
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
                Assert.assertNotEquals(key1.getEncoded(), key2.getEncoded());
            }
        }

        // Testcase 4: generate random asymmetric key without the seed
        // Expect the keys have different value
        {
            List<byte[]> keyList = new LinkedList<>();
            for (int i = 0; i < 1000; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, 16);

                Assert.assertFalse(keyList.contains(key.getEncoded()));
                keyList.add(key.getEncoded());
            }
        }

        // Testcase 5: generate asymmetric key with given key size (bits)
        {
            List<byte[]> keyList = new LinkedList<>();
            for (int i = 1; i <= 32; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, i);

                Assert.assertEquals(i, key.getEncoded().length);
                Assert.assertFalse(keyList.contains(key.getEncoded()));
                keyList.add(key.getEncoded());
            }
        }
    }

    @Test
    public void generateAsymmetricKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();

        // private/public key generated each time is unique
        for (int i = 0; i < 100; i++) {
            KeyPair newKeyPair = CWCryptoUtils.generateAsymmetricKey();
            Assert.assertFalse(Arrays.equals(publicKey, newKeyPair.getPublic().getEncoded()));
            Assert.assertFalse(Arrays.equals(privateKey, newKeyPair.getPrivate().getEncoded()));
        }
    }

    @Test
    public void decodePubicKey() throws Exception {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();

        CWFileUtils.write(keyPair.getPublic().getEncoded(), testFile);
        PublicKey pk = CWCryptoUtils.decodePubicKey(CWFileUtils.read(testFile));
        Assert.assertNotNull(pk);
        Assert.assertArrayEquals(keyPair.getPublic().getEncoded(), pk.getEncoded());
    }

    @Test
    public void decodePrivateKey() throws Exception {
        KeyPair keyPair = CWCryptoUtils.generateAsymmetricKey();
        CWFileUtils.write(keyPair.getPrivate().getEncoded(), testFile);
        PrivateKey pk = CWCryptoUtils.decodePrivateKey(CWFileUtils.read(testFile));
        Assert.assertNotNull(pk);
        Assert.assertArrayEquals(keyPair.getPrivate().getEncoded(), pk.getEncoded());
    }

    @Test
    public void encryptDecryptUsingAsymmetricKey() throws Exception {
        // generate 24 bytes random data string
        byte[] originData = CWCryptoUtils.generateSymmetricKey(null, 24).getEncoded();
        KeyPair asymmetricKey = CWCryptoUtils.generateAsymmetricKey();
        byte[] encryptedData = CWCryptoUtils.encrypt(asymmetricKey.getPublic(), originData); // encrypt with public data

        // Testcase: decrypt data using private key
        {
            byte[] decryptedData = CWCryptoUtils.decrypt(asymmetricKey.getPrivate(), encryptedData);
            Assert.assertArrayEquals(originData, decryptedData);
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

    /**
     * The basic idea is
     * 1. use app secret key to encrypt private key.
     * 2. Use public key to encrypt session key.
     * 3. Use session key to encrypt data
     * 4. To decrypt data, we need to app secret key -> private key -> session key -> data
     */
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
        {
            SecretKey appKey = CWCryptoUtils.generateSymmetricKey("BVST^&Y()I#)_MKLBNDB164T&^*Y)i0-r", 16);
            CWFileUtils.write(appKey.getEncoded(), appKeyFile);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 2. Generate asymmetric key (public/private key)
        {
            // These two key should be remain unchanged forever
            KeyPair asymmetricKey = CWCryptoUtils.generateAsymmetricKey();
            // Store public key to file
            CWFileUtils.write(asymmetricKey.getPublic().getEncoded(), publicKeyFile);
            // While for private key, use app secret key to encrypt then store to file
            CWFileUtils.write(CWCryptoUtils.encrypt(
                    CWFileUtils.read(appKeyFile),
                    asymmetricKey.getPrivate().getEncoded()), privateKeyFile);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 3. Create a random session key
        // We use this key to encrypt data before sending to other users.
        SecretKey sessionKey = CWCryptoUtils.generateSymmetricKey("%&*&)I_BV_VB_JHI)_Y", 16);
        {
            // Encrypt session key using public key
            // This encrypted session key will be sent to other user
            PublicKey publicKey = CWCryptoUtils.decodePubicKey(CWFileUtils.read(publicKeyFile));
            CWFileUtils.write(CWCryptoUtils.encrypt(publicKey, sessionKey.getEncoded()), sessionKeyFile);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 4. encrypt the text using session key
        // After that, We only can use private key generated at step 1 to decrypt the encrypted data
        byte[] encryptedText = CWCryptoUtils.encrypt(sessionKey.getEncoded(), CWStreamUtils.stringToBytes(originText));

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // 5. For decryption: we need the following keys:
        // +++ app secret key
        // +++ encrypted private key
        // +++ encrypted session key
        // First, we decrypt private key using app secret key first
        PrivateKey privateKey = CWCryptoUtils.decodePrivateKey(
                /* decrypt encrypted private key using app secret key */
                CWCryptoUtils.decrypt(
                        CWFileUtils.read(appKeyFile),
                        /* decode base64 string */
                        CWFileUtils.read(privateKeyFile))
        );
        // Secondly, we decrypt session key using private key
        byte[] decryptedSessionKey = CWCryptoUtils.decrypt(privateKey, CWFileUtils.read(sessionKeyFile));
        // Thirdly, we decrypt data using session key
        String decryptedText = CWStreamUtils.bytesToString(CWCryptoUtils.decrypt(decryptedSessionKey, encryptedText));
        Assert.assertEquals(originText, decryptedText);
    }
}
