/**
 * 
 */
package client;

import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.security.SecureRandom;

/**
 * @author ArunIyengar
 * Class to perform encryption and decryption
 *
 */
public class Encryption {

    private final static String DEFAULT_ALGORITHM = "AES";
    private final static String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    
    public static class Key implements Serializable {
        private static final long serialVersionUID = 78;
        private byte[] iv;
        private SecretKey secretKey;
     }
    
    static SecureRandom randomSecureRandom = new SecureRandom();

    /**
     * Decrypt an encrypted object
     * 
     * @param sealedObject
     *            encrypted object
     * @param secretKey
     *            encryption key
     * @return decrypted object
     * 
     * */
    public static <T> T decrypt(SealedObject sealedObject, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key.secretKey, new IvParameterSpec(key.iv));
            return (T) sealedObject.getObject(cipher);
        }
        catch (Exception e) {
            Util.describeException(e, "Exception in Encryption.decrypt");
            return null;
        }
    }

    
    /**
     * Encrypt a serializable object using AES
     * 
     * @param object
     *            object which implements Serializable
     * @param secretKey
     *            encryption key
     * @return encrypted object
     * 
     * */
    public static SealedObject encrypt(Serializable object, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key.secretKey, new IvParameterSpec(key.iv));
            return new SealedObject(object, cipher);
        }
        catch (Exception e) {
            Util.describeException(e, "Exception in Encryption.encrypt");
            return null;
        }
    }

    /**
     * Generate and return an encryption key
     * 
     * @return encryption key
     * 
     * */
    public static Key generateKey() {
        Key key = new Key();
        try {
            key.secretKey = KeyGenerator.getInstance(DEFAULT_ALGORITHM).generateKey();
        }
        catch (Exception e) {
            Util.describeException(e, "Exception in Encryption.generateKey");
            return null;
        }
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);
//        key.ivParams = new IvParameterSpec(iv);
        key.iv = iv;
        return key;
    }
    
}
