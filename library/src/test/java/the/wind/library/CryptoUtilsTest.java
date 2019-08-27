package the.wind.library;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

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
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, origin.getBytes(StandardCharsets.UTF_8));
            System.out.println("encryptDecryptString - case 1 - base64: " + Base64.encodeBase64String(encrypted));
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
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, origin.getBytes(StandardCharsets.UTF_8));
            System.out.println("encryptDecryptString - case 2: " + Base64.encodeBase64String(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 3: encrypt/decrypt with different key
        {
            String origin = "Color the wind";
            // wrong secret key
            try {
                byte[] encrypted = CWCryptoUtils.encrypt("XXX-XXX-XXX-XXX.", origin.getBytes(StandardCharsets.UTF_8));
                System.out.println("encryptDecryptString - case 3: " + Base64.encodeBase64String(encrypted));
                CWCryptoUtils.decrypt("ZZZ-ZZZ-ZZZ", encrypted);
            } catch (Exception ex) {
                Assert.assertTrue("Wrong secret key - " + ex.getMessage(), true);
            }
        }

        // Testcase 4: encrypt/decrypt Vietnamese language
        {
            String origin = "Tô màu cho gió";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, origin.getBytes(StandardCharsets.UTF_8));
            System.out.println("encryptDecryptString - case 4: " + Base64.encodeBase64String(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 5: encrypt/decrypt Japanese language
        {
            String origin = "風を彩る。";
            byte[] encrypted = CWCryptoUtils.encrypt(secretKey, origin.getBytes(StandardCharsets.UTF_8));
            System.out.println("encryptDecryptString - case 5: " + Base64.encodeBase64String(encrypted));
            byte[] decrypted = CWCryptoUtils.decrypt(secretKey, encrypted);
            Assert.assertEquals(origin, CWStreamUtils.bytesToString(decrypted));
        }

        // Testcase 6: encrypt/decrypt the same string but multiple time
        {
            String origin = "Color the wind";
            for (int i = 0; i < 10; i++) {
                byte[] encrypted = CWCryptoUtils.encrypt(secretKey, origin.getBytes(StandardCharsets.UTF_8));
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
        // Expect the keys have the same value
        {
            String seed = "$^IP)O_}+BbhGI:^&*DTR";
            SecretKey key = CWCryptoUtils.generateSymmetricKey(seed, 16); // 128 bits
            String keyBase64 = Base64.encodeBase64String(key.getEncoded());
            System.out.println("generateSymmetricKey - case 1 - non base64: " + CWStreamUtils.bytesToString(key.getEncoded()));
            System.out.println("generateSymmetricKey - case 1 - base64: " + keyBase64);

            // check value
            Assert.assertEquals("GVOSVhedFBgAPC/NgDjlwA==", keyBase64);
            Assert.assertNotEquals(keyBase64, Base64.encodeBase64String(seed.getBytes(StandardCharsets.UTF_8)));
            // check size
            Assert.assertEquals(16, key.getEncoded().length);
            Assert.assertEquals(16, Base64.decodeBase64(keyBase64).length);

            // test generate key 100 times
            for (int i = 0; i < 1000; i++) {
                SecretKey sameKey = CWCryptoUtils.generateSymmetricKey(seed, 16);

                Assert.assertEquals(16, sameKey.getEncoded().length);
                Assert.assertEquals(keyBase64, Base64.encodeBase64String(sameKey.getEncoded()));
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
                        Base64.encodeBase64String(key1.getEncoded()),
                        Base64.encodeBase64String(key2.getEncoded()));
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
                        Base64.encodeBase64String(key1.getEncoded()),
                        Base64.encodeBase64String(key2.getEncoded()));
            }
        }

        // Testcase 4: generate random asymmetric key without the seed
        // Expect the keys have different value
        {
            List<String> keyList = new LinkedList<>();
            for (int i = 0; i < 1000; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, 16);
                String base64 = Base64.encodeBase64String(key.getEncoded());

                Assert.assertFalse(keyList.contains(base64));
                keyList.add(base64);
            }
        }

        // Testcase 5: generate asymmetric key with given key size (bits)
        {
            List<String> keyList = new LinkedList<>();
            for (int i = 1; i <= 32; i++) {
                SecretKey key = CWCryptoUtils.generateSymmetricKey(null, i);
                String base64 = Base64.encodeBase64String(key.getEncoded());

                Assert.assertEquals(i, key.getEncoded().length);
                Assert.assertFalse(keyList.contains(base64));
                keyList.add(base64);
            }
        }

    }
}
