/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.tools;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author haerwynn
 */
public class Encryptions {

	private static final boolean dolog = true;

	//private static final Log log = LogFactory.getLog(Encryptions.class);
	/**
	 *
	 * @param mode SHA-256, SHA-1 ou MD5
	 * @param x
	 * @return
	 */
	public static String encryptIn(String mode, String x) {
		java.security.MessageDigest md;
		try {
			md = java.security.MessageDigest.getInstance(mode);
			// FileInputStream fis = new FileInputStream("c:\\loging.log");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}

		md.update(x.getBytes());

		byte byteData[] = md.digest();

		//convert the byte to hex format method 1
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		if (dolog) {
			System.out.println("Hex format : " + sb.toString());
		}

		//convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		if (dolog) {
			System.out.println("Hex format : " + hexString.toString());
		}

		hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			Character c = new Character((char) (byteData[i]));
			hexString.append(c);
		}
		if (dolog) {
			System.out.println("Ascii format? : " + hexString.toString());
		}
		return sb.toString();
	}

	public static String encryptWithKey(String mode, String text, String key) throws Exception {
		//String text = "Hello World";
		//String key = "Bar12345Bar12345"; // 128 bit key
		// Create key and cipher
		if (key == null) {
			throw new NullPointerException("key==null");
		}
		if (key.length() > 16) {
			key = key.substring(0, 16);
		}
		Key aesKey = new SecretKeySpec(key.getBytes("UTF-8"), mode);//"AES"
		Cipher cipher = Cipher.getInstance(mode);
		// encrypt the text
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));
		/*StringBuilder sb = new StringBuilder();
		for (byte b : encrypted) {
			sb.append((char) b);
		}*/
		
		// the encrypted String
		String enc = new String(encrypted,"UTF-8");//Arrays.toString(encrypted);
		return enc;
		// decrypt the text
		//cipher.init(Cipher.DECRYPT_MODE, aesKey);
		//String decrypted = new String(cipher.doFinal(encrypted));
		//System.err.println(decrypted);

	}

	public static String decryptWithKey(String mode, String text, String key) throws Exception {
		//String text = "Hello World";
		//String key = "Bar12345Bar12345"; // 128 bit key
		// Create key and cipher
		if (key == null) {
			throw new NullPointerException("key==null");
		}
		if (text == null) {
			throw new NullPointerException("text==null");
		}
		if (key.length() > 16) {
			key = key.substring(0, 16);
		}
		Key aesKey = new SecretKeySpec(key.getBytes("UTF-8"), mode);//"AES"
		Cipher cipher = Cipher.getInstance(mode);
		// encrypt the text
		//cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		//byte[] encrypted = cipher.doFinal(text.getBytes());
		//System.err.println(new String(encrypted));
		// decrypt the text
		byte[] bb = text.getBytes(Charset.forName("UTF-8"));/*new byte[text.length()];
		for (int i = 0; i < text.length(); i++) {
			bb[i] = (byte) text.charAt(i);
		}*/
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		byte[] db = cipher.doFinal(bb);
		String decrypted = new String(db,Charset.forName("UTF-8"));//Arrays.toString(cipher.doFinal(bb));//
		return decrypted;

	}

	/*
	import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AEStest {
    public static void main (String[] args) throws Exception {
        String text = "This is a simple text Message.";

        System.out.println("UnEncryptedage: "+ text + "\n");
        System.out.println("UnEncryptedage as Array: "+ Arrays.toString(text.getBytes()) + "\n");

        //Encrypted data is binary data
        String encryptTextA = Arrays.toString(encryptAES(text));
        System.out.println("Encryptedage as Array: "+ encryptTextA + "\n");
        String encryptText = new String(encryptAES(text), "UTF-8");
        System.out.println("Encryptedage as String: "+ encryptText + "\n");

        String decryptTextA = Arrays.toString(decryptAES(encryptAES(text)));
        System.out.println("Decryptedage as Array: "+ decryptTextA + "\n");
        String decryptText = new String(decryptAES(encryptAES(text)), "UTF-8");
        System.out.println("Decryptedage: "+ decryptText);
    }
//  public static byte[] decryptAES(String message) throws Exception {
    public static byte[] decryptAES(byte[] message) throws Exception {
        // 16 byte secretKey
        String secretKey = "TestSecretKey111";
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        // return cipher.doFinal(message.getBytes());
        return cipher.doFinal(message);
    }

    public static byte[] encryptAES(String message) throws Exception {
        // 16 byte secretKey
        String secretKey = "TestSecretKey111";
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message.getBytes());
    }
}
	*/
}
