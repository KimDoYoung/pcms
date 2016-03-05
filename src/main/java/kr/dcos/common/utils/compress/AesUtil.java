package kr.dcos.common.utils.compress;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class AesUtil {
    private final int keySize;
    private final int iterationCount;
    private final Cipher cipher;
    private static final String SALT = "4FF2EC019C627B945225DEBAD71A01B6985FE84C95A70EB132882F88C0A59A55";
    private static final String IV = "127D5C9927726BCEFE752eB1BDD3E138";
    public AesUtil() {
    	this(128,100);
    }
    public AesUtil(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }
        catch (NoSuchAlgorithmException e){
            throw fail(e);
        } catch (NoSuchPaddingException e) {
        	 throw fail(e);
		}
    }
    public String encrypt(String passphrase, String plaintext) {
    	return encrypt(SALT,IV,passphrase,plaintext);
    }
    public String encrypt(String salt, String iv, String passphrase, String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
            return hex(encrypted);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }
    public String decrypt(String passphrase, String ciphertext) {
    	return decrypt(SALT,IV,passphrase,ciphertext);
    }
    public String decrypt(String salt, String iv, String passphrase, String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, hex(ciphertext));
            return new String(decrypted, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }
    
    public String encryptBase64(String passphrase, String plaintext) {
    	return encryptBase64(SALT,IV,passphrase,plaintext);
    }
    public String encryptBase64(String salt, String iv, String passphrase, String plaintext) {
    	try {
    		SecretKey key = generateKey(salt, passphrase);
    		byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
    		return base64(encrypted);
    	}
    	catch (UnsupportedEncodingException e) {
    		throw fail(e);
    	}
    }
    public String decryptBase64(String passphrase, String ciphertext) {
    	return decryptBase64(SALT,IV,passphrase,ciphertext);
    }
    public String decryptBase64(String salt, String iv, String passphrase, String ciphertext) {
    	try {
    		SecretKey key = generateKey(salt, passphrase);
    		byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext));
    		return new String(decrypted, "UTF-8");
    	}
    	catch (UnsupportedEncodingException e) {
    		throw fail(e);
    	}
    }
    
    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        }
        catch (InvalidKeyException e) {
            throw fail(e);
        } catch (InvalidAlgorithmParameterException e) {
        	 throw fail(e);
		} catch (IllegalBlockSizeException e) {
			 throw fail(e);
		} catch (BadPaddingException e) {
			 throw fail(e);
		}
    }
    
    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return key;
        }
        catch (NoSuchAlgorithmException e) {
            throw fail(e);
        } catch (InvalidKeySpecException e) {
        	throw fail(e);
		}
    }
    
    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }
    
    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
    
    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }
    
    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    	
    }
    
    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        }
        catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }
    /**
     * clp에게 제공할 password
     * @return
     */
	public String generatePassphase() {
		String s = "01234567890-=!@#$%^&*()_+|abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ:\"{}?></";
		int len = s.length();
		Random oRandom = new Random();
	    StringBuilder sb = new StringBuilder();
	    int idx = 0;
		for(int i=0;i<16;i++){
			idx = oRandom.nextInt(len);
			sb.append(s.toCharArray()[idx]);
		}
		String s2 = new String(Base64.encodeBase64(sb.toString().getBytes()));
		String authKey = Hex.encodeHexString(s2.getBytes());
		return authKey;
	}
	/**
	 * MD5 hash코드를 얻는다
	 * text가 null이면 null을 리턴한다
	 * @param text
	 * @return
	 */
	public static String MD5(String text) {
		if (text == null)
			return null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte byteData[] = md.digest();
			return hex(byteData);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * SHA  hash코드를 얻는다<br>
	 * text가 null이면 null을 리턴한다
	 * @param text
	 * @return
	 */
	public static String SHA(String text) {
		if(text == null) return null;
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(text.getBytes()); 
			byte byteData[] = sh.digest();
			return hex(byteData);
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	
}
