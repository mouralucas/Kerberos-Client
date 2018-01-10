package client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Lucas Penha de Moura - 1208977
 */
public class Encode {
	
	/**
	 * A chave tem que ter 16 bytes
	 * @param msg
	 * @param key
	 * @return mesnagem encriptada
	 */
	public static String encode(String msg, String key) {
		Cipher cipher;
		
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] mensagem = msg.getBytes();

	        // Usando chave de 128-bits (16 bytes)
	        byte[] chave = key.getBytes();
//	        System.out.println("Tamanho da chave: " + chave.length);

	        // Encriptando...
	        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(chave, "AES"));
	        byte[] encrypted = cipher.doFinal(mensagem);
	        
	        return DatatypeConverter.printHexBinary(encrypted);
	        
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return "";
	}
	
	public static String decode(String msg, String key) {
		Cipher cipher;
		
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] mensagem = DatatypeConverter.parseHexBinary(msg);

	        // Usando chave de 128-bits (16 bytes)
	        byte[] chave = key.getBytes();
//	        System.out.println("Tamanho da chave: " + chave.length);

	        // Decriptando...
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chave, "AES"));
            
            byte[] decrypted = cipher.doFinal(mensagem);
	        
	        return new String(decrypted);
	        
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return "";
	}

}
