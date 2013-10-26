package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.utils.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import javax.crypto.Mac;

public class Cifrador{

    public static final int SALT_LENGTH = 20;
    public static final int PBE_ITERATION_COUNT = 1000;

    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String PBE_ALGORITHM = "RSA/ECB/PKCS1PADDING";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final String TAG = Cifrador.class.getSimpleName();
    
    //CLAVES PUBLICA Y PRIVADA YA CODIFICADAS
    RSAPublicKeySpec pub;
    public String getStringPub() {
    	String resultado = b64.encodeToString(pub.toString().getBytes(),false);
		return resultado;
	}
    
    public String getStringPriv() {
    	String resultado = b64.encodeToString(priv.toString().getBytes(),false);
		return resultado;
	}
    
    public PublicKey Stringtokeypublic(String key) throws Exception{
  	  X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey clavePublica = keyFactory.generatePublic(bobPubKeySpec);
        return clavePublica;
    }
    
    public static PrivateKey Stringtokeyprivate(String key) throws Exception{
  	  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(key));
  	  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
  	  PrivateKey clavePrivada = keyFactory.generatePrivate(keySpec);
        return clavePrivada;
    }

    public RSAPublicKeySpec getPub(){
    	return pub;
    }

	public RSAPrivateKeySpec getPriv() {
		return priv;
	}

	public void setPub(RSAPublicKeySpec pub) {
		this.pub = pub;
	}



	public void setPriv(RSAPrivateKeySpec priv) {
		this.priv = priv;
	}

	RSAPrivateKeySpec priv;

    public Cifrador(){
    	super();
    }
    
    static //Base 64 para codificar
    Base64 b64;
    Cipher SymKeyCipher;
    SecretKeySpec secret;
    IvParameterSpec ivspec;
    byte[] salt;
    
    
    
    /*
     * msj: mensaje a cifrar
     * op: operacion (0 cifrar, 1 descifrar)
     * certificate: clave publica del usuario al que le envias el mensaje
     */
    public String encriptar (String msj, PublicKey clave_publica_receptor){ //Key clave
    	byte[] resultado=null;
    	String resultado_str=null;
    	
        try {
            

            	Log.i("CLAVE PUBLICA**********", new String(clave_publica_receptor.getEncoded()));
            	//Log.i("ENCRIPTO Y DESCIFRO","llamo al metodo cifrar");

            	SecretKeySpec key = keyAES();
            	byte [] keyAES = key.getEncoded();
            	//Log.i("MUESTRO KEYAES","cifrado" + new String (key.getEncoded()));
            	
            	byte[] kpubliRec_RSA = cifradoRSA(key, clave_publica_receptor);
            	//Log.i("MUESTRO TXT TRAS RSA","cifrado RSA: " + new String(kpubliRec_RSA));
            	
            	IvParameterSpec ivSpec= generateIV();
            	byte[] iv= ivSpec.getIV();
            	//Log.i("MUESTRO IV",  new String (new String (iv)));
            	byte[] texto_cifrado = AESCBCCifrado(msj, ivSpec, key);
            	//Log.i("MUESTRO TXT TRAS AES","cifrado AES: " + new String (texto_cifrado));

            	int tamTextoRSA = kpubliRec_RSA.length;
            	int tamIV = iv.length;
            	int tamTexCif = texto_cifrado.length;
            	
            	Log.i("TAMAÑO DE KEY, IV, TextoCifrado", tamTextoRSA+" , "+tamIV+" , "+ tamTexCif);
            	byte[] res = new byte[tamTextoRSA+ tamIV+ tamTexCif];
            	
            	System.arraycopy(kpubliRec_RSA, 0, res, 0, tamTextoRSA);

            	System.arraycopy(iv, 0, res, tamTextoRSA, tamIV);
            	System.arraycopy(texto_cifrado, 0, res, (tamTextoRSA+tamIV), tamTexCif);
       	
            	Log.i("Array de bytes al envio new String(res)---------", new String(res));
  
            	 
            	/*Log.i("ENVIO KEY sin 64",new String(kpubliRec_RSA));
            	Log.i("ENVIO IV sin 64",new String(iv));
            	Log.i("ENVIO MSJ sin 64",new String(texto_cifrado));*/
            	//Log.i("MUESTRO TXT TRAS B64 ENCRIPTANDO","cifrado" + texto_codb64.toString());
            	//resultado=texto_codb64;
            	
            	resultado_str = Base64.encodeToString(res, false);
            	
            	
            	Log.i("ENVIO con B64: ", resultado_str);
            	
            	Log.i("ENVIO con B64.encrypted.getbytes: ", new String(resultado_str.getBytes()));
            	
            	/*
            	
            	Log.i("FIRMA","llamo al metodo vaya");
            	firma("pablito tenia un clavito",generateRsaKeyPair());
            	
            	/*
            	
            	//falla el ejemplo, Java Android Error “too much data for RSA block” al no pasar base64
            	
            	
	            //Analisios de seguriodad Clave publica es la misma que clave para cifrar AUNQUE EN REALIDAD NO ES ASI, 
	            // Analisis de vector de iv en claro
	              
	             */
	             
          
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
       // return b64.encodeToString(resultado,false);
       return resultado_str;

    }
    
    
    
    

        public String desencriptar (String msj, Key clave_privada_receptor) throws Exception{ //Key clave
        	String resultado=null;
        	Log.i("entro en desencriptar: ", msj);
        	//String resultado_str= Base64.decode(msj);
        	byte[] resultado_str = Base64.decode(msj);
        	
        	byte[] RSA_clave = new byte[128];
        	System.arraycopy(resultado_str, 0, RSA_clave, 0, RSA_clave.length);
        	
        	byte[] IV=new byte[16];
        	System.arraycopy(resultado_str, RSA_clave.length, IV, 0, IV.length);
        	
        	byte[] mensaje = new byte[resultado_str.length-(RSA_clave.length+IV.length)];
        	System.arraycopy(resultado_str, RSA_clave.length+IV.length, mensaje, 0, mensaje.length);
        	
        	/*Log.i("REC KEY sin B64",new String(RSA_clave));
        	Log.i("REC IV sin B64", new String(IV));
        	Log.i("REC MSJ sin B64",new String(mensaje));
        	Log.i("TAMAÑO DE KEY, IV, TextoCifrado", RSA_clave.length+" , "+IV.length+" , "+ mensaje.length);*/
        	
        	//Pasar a claro la clave recibida
        	Cipher c;
        	byte [] clave_Descifrada = null;
    		try {
    			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    			c.init(c.DECRYPT_MODE,clave_privada_receptor);
    	    	
    	    	clave_Descifrada =  c.doFinal(RSA_clave);
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchPaddingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		Log.i("CLAVE DESCIFRADA",new String(clave_Descifrada) + ":" + clave_Descifrada.length);
    		//Pasar a claro el mensaje
        	

    		
    		Cipher ci= Cipher.getInstance("AES/CBC/PKCS5Padding");
    		SecretKeySpec keyAes = new SecretKeySpec(clave_Descifrada, "AES");
    		IvParameterSpec iv = new IvParameterSpec(IV);
    		ci.init(Cipher.DECRYPT_MODE, keyAes,iv);
    		byte [] msjDescifrado = ci.doFinal(mensaje);
    		Log.i("MENSAJEEEEEE",new String(msjDescifrado));
            return new String(msjDescifrado);

        }
    
        /**
         * Encrypt a sample message using AES in CBC mode with an IV.
         * 
         * @param args not used
         * @throws Exception if the algorithm, key, iv or any other parameter is
         *             invalid.
         */
        public byte[] AESCBCCifrado(String message, IvParameterSpec ivspec, SecretKeySpec key) throws Exception
        {

            byte[] data = message.getBytes("UTF8");

            // initialize the cipher for encrypt mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);

            // encrypt the message
            byte[] encrypted = cipher.doFinal(data);
           // Log.i("texto cifrado: ", hexEncode(encrypted) + "\n");
            
            return encrypted;
        }
        
        public String AESCBCDescifrado(String encrypted, IvParameterSpec ivspec, SecretKeySpec key) throws Exception
        {

        	 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);

           // Log.i("Descifrado decrypted predofinal: ", new String(encrypted.getBytes()));
            // decrypt the message
            byte[] decrypted = cipher.doFinal(encrypted.getBytes());
            Log.i("Descifrado: ", new String(decrypted) + "\n");
            
            return new String(decrypted);
        }
        
        public SecretKeySpec keyAES() throws Exception{
        	KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);  // To use 256 bit keys, you need the "unlimited strength" encryption policy files from Sun.
            byte[] key = keygen.generateKey().getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            return skeySpec;
        }
        
        private IvParameterSpec generateIV() throws NoSuchAlgorithmException {
        	IvParameterSpec ivspec;
        	SecureRandom random = new SecureRandom();
            byte[] iv = random.generateSeed(16);
            ivspec = new IvParameterSpec(iv);
            return ivspec;
        }

        /**
         * Hex encodes a byte array. <BR>
         * Returns an empty string if the input array is null or empty.
         * 
         * @param input bytes to encode
         * @return string containing hex representation of input byte array
         */
        public static String hexEncode(byte[] input)
        {
            if (input == null || input.length == 0)
            {
                return "";
            }

            int inputLength = input.length;
            StringBuilder output = new StringBuilder(inputLength * 2);

            for (int i = 0; i < inputLength; i++)
            {
                int next = input[i] & 0xff;
                if (next < 0x10)
                {
                    output.append("0");
                }

                output.append(Integer.toHexString(next));
            }

            return output.toString();
        }

        public String cifrarBBDD( String password, String loQueQuieroCifrar) {
        	//Log.i("LO QUE QUIERO CIFRAR",loQueQuieroCifrar);
        	int iterationCount = 100;
            int saltLength = 16; // bytes; 16*8=128 bits
            int keyLength = 128; 
            
            //inicializo variables
            SecretKeyFactory keyFactory=null;
            Cipher cipher=null;
            byte[] keyBytes=null;
        	
        	
        	if (password == null || password.length() == 0)
				try {
					throw new Exception("Empty pass");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

             byte[] encrypted = null;

             SecureRandom random = new SecureRandom();
             byte[] salt = new byte[saltLength];
             random.nextBytes(salt);
             //Log.i("SALT:",new String(salt));
             
			try {
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			 byte[] iv = new byte[cipher.getBlockSize()];
             random.nextBytes(iv);
             IvParameterSpec ivParams = new IvParameterSpec(iv);
             
             try {
				password = new String(password.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

             KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                     iterationCount, keyLength);
			try {
				keyFactory = SecretKeyFactory
				         .getInstance("PBKDF2WithHmacSHA1");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
			try {
				keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
     
        	SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        	
        	try {
				cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec , ivParams);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        	byte cifrar [] = new byte[loQueQuieroCifrar.length()+(loQueQuieroCifrar.length()%16)];
        	for(int i=0; i<cifrar.length; i++)
        	{
        		if(i<loQueQuieroCifrar.length())
        			cifrar[i]=loQueQuieroCifrar.getBytes()[i];
        		else
        			cifrar[i]=' ';        		
        		
        	}
        	
			try {
				encrypted = cipher.doFinal(cifrar);
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//Log.i("ENCRIPTED: ",new String(encrypted));
			String aux = Base64.encodeToString(salt, false)+"]"+Base64.encodeToString(encrypted,false)+"]"+Base64.encodeToString(iv,false);
        	//Log.i("Resultado cifrado: ", aux);
       	return aux;
            
         //   return encryptedStr;
        }
        public boolean comprobarPASS (String passMetidaPorUser, String campoBBDD){
        	//Log.i("String passMetidaPorUser, String campoBBDD",passMetidaPorUser + " - "+campoBBDD);
        	int iterationCount = 100;
            int saltLength = 16; // bytes; 64 bits
            int keyLength = 128; 
        	//Log.i("SECRETO DESCIFRAR","PBE"+secreto);
            String[] fields = campoBBDD.split("]");
            byte[] salt = Base64.decode(fields[0]);
            byte[] iv= Base64.decode(fields[2]);
            SecretKeyFactory keyFactory=null;
            byte [] keyBytes=null;
            Cipher cipher=null;
            String passAUX = passMetidaPorUser;
            try {
            	passAUX = new String(passAUX.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            KeySpec keySpec = new PBEKeySpec(passAUX.toCharArray(), salt,
                    iterationCount, keyLength);
			try {
				keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
           
			try {
				keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    
       	SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
       	try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        IvParameterSpec ivParams = new IvParameterSpec(iv);
       	try {
				cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec ,ivParams);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
       	byte cifrar [] = new byte[passAUX.length()+(passAUX.length()%16)];
       	for(int i=0; i<cifrar.length; i++)
       	{
       		if(i<passAUX.length())
       			cifrar[i]=passAUX.getBytes()[i];
       		else
       			cifrar[i]=' ';        		
       		
       	}
       	byte[] encrypted=null;
			try {
				encrypted = cipher.doFinal(cifrar);
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	
       	//Log.i("ENCRIPTED: ",new String(encrypted));
			String aux = Base64.encodeToString(salt, false)+"]"+Base64.encodeToString(encrypted,false)+"]"+Base64.encodeToString(iv,false);
            
			if(aux.equalsIgnoreCase(campoBBDD))
				return true;
			else return false;
            
    
        }
        public String descifrarBBDD(String password, String secreto) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
  	
        	int iterationCount = 100;
            int saltLength = 16; // bytes; 64 bits
            int keyLength = 128; 
        	//Log.i("SECRETO DESCIFRAR","PBE"+secreto);
            String[] fields = secreto.split("]");
            byte[] salt = Base64.decode(fields[0]);
            byte[] cipherBytes =  Base64.decode(fields[1]);
            byte[] iv= Base64.decode(fields[2]);
            // as above
            //Log.i("SECRETO DESCIFRAR","salt"+new String (salt));
            //Log.i("SECRETO DESCIFRAR","cipher"+new String (cipherBytes));
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    iterationCount, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = null;
			try {
				keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			IvParameterSpec ivParams = new IvParameterSpec(iv);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] decrypted = null;
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            decrypted = cipher.doFinal(cipherBytes);

            return new String(decrypted);
        	
        }
//    
//    public void Keystore(){
//    	KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//
//        // get user password and file input stream
//        char[] password = getPassword();
//        java.io.FileInputStream fis =
//            new java.io.FileInputStream("keyStoreName");
//        ks.load(fis, password);
//        fis.close();
//        
//     // get my private key
//        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
//            ks.getEntry("privateKeyAlias", password);
//        PrivateKey myPrivateKey = pkEntry.getPrivateKey();
//
//        // save my secret key
//        javax.crypto.SecretKey mySecretKey;
//        KeyStore.SecretKeyEntry skEntry =
//            new KeyStore.SecretKeyEntry(mySecretKey);
//        ks.setEntry("secretKeyAlias", skEntry, password);
//
//        // store away the keystore
//        java.io.FileOutputStream fos =
//            new java.io.FileOutputStream("newKeyStoreName");
//        ks.store(fos, password);
//        fos.close();
//        
//    }
          
      /**
       * generates RSA key pair
       * 
       * @param keySize
       * @param publicExponent public exponent value (can be RSAKeyGenParameterSpec.F0 or F4)
       * @return
       */
      public static KeyPair generateRsaKeyPair(){
        KeyPair keys = null;
        try
        {
    	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    	    keyPairGenerator.initialize(1024);
    	    keys = keyPairGenerator.genKeyPair();
        }
        catch(Exception e)
        {
        	Log.i("Error generating keys" , e.toString());
        }
        return keys;
      }
      
     public byte[] cifradoRSA(SecretKeySpec key, Key clave_publica_destinatario) throws Exception {
  	    
   	    
   	    //cifrado
   	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
   	    
   	    //cifro con clave publica
   	    cipher.init(Cipher.ENCRYPT_MODE, clave_publica_destinatario);
   	    byte[] cipherText = cipher.doFinal(key.getEncoded());
   	    Log.i("CIPHERMODE",new String(cipherText));
   	    
   	    return cipherText;
   	    
   	  }
      
      /**
       * RSA encrypt function (RSA / ECB / PKCS1-Padding)
       * 
       * @param original
       * @param key
       * @return
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws SignatureException 
       */
      public String firmo(String original,  PrivateKey key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
      {
  	   
	    
  	    //Creo la firma
  	    //Signature sig = Signature.getInstance("MD5WithRSA");
  	    Signature sig = Signature.getInstance("SHA1withRSA");
  	    
  	    Log.i("VOY A FIRMAR ESTO:", original+ ":::::"+original.length());
  	    //Obtengo la clave privada del par creado anteriormente
  	    sig.initSign(key);
  	    //firmo el mensaje CON MI CLAVE PRIVADA
  	    Log.i("CLAVE PRIVADA",new String(key.getEncoded())+ ":::::"+key.getEncoded().length);
  	   
  	    byte[] data = original.getBytes("UTF8");
  	    
  	    sig.update(data);
  	    byte[] signatureBytes = sig.sign();
  	    String firma = Base64.encodeToString(signatureBytes, false);
  	    Log.i("LA FIRMA ES:", firma+ ":::::"+firma.length());
  	    
  	    

  	    return firma;
  	   // return new String(signatureBytes);
      }
    	
      
      /**
       * RSA decrypt function (RSA / ECB / PKCS1-Padding)
       * 
       * @param encrypted
       * @param key
       * @return
     * @throws InvalidKeyException 
     * @throws SignatureException 
     * @throws NoSuchAlgorithmException 
       */
      public static boolean verifico(byte[] encrypted, PublicKey key, byte[] firma) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException
      {
    	  Signature sig = Signature.getInstance("SHA1WithRSA");
    	 //byte[] a = Base64.decode(encrypted);
  	    //verifico la firma CON LA CLAVE PUBLICA
  	    sig.initVerify(key);
  	    sig.update(encrypted);
  	    Log.i("CLAVE PUBLICA EMISOR:", new String(key.getEncoded())+ ":::::"+key.getEncoded().length	);
  	   // Log.i("TEXTO A VERIFICAR", new String (encrypted)+ ":::::"+encrypted.length);
  	    //Log.i("Con la firma: ", new String (firma)+ ":::::"+firma.length);
  	    
  	    return sig.verify(firma);   
    	  
    	  
  	    
      }

      /**
       * converts given byte array to a hex string
       * 
       * @param bytes
       * @return
       */
      public static String byteArrayToHexString(byte[] bytes)
      {
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<bytes.length; i++)
        {
          if(((int)bytes[i] & 0xff) < 0x10)
            buffer.append("0");
          buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buffer.toString();
      }
      
      /**
       * converts given hex string to a byte array
       * (ex: "0D0A" => {0x0D, 0x0A,})
       * 
       * @param str
       * @return
       */
      public static final byte[] hexStringToByteArray(String str)
      {
        int i = 0;
        byte[] results = new byte[str.length() / 2];
        for (int k = 0; k < str.length();)
        {
          results[i] = (byte)(Character.digit(str.charAt(k++), 16) << 4);
          results[i] += (byte)(Character.digit(str.charAt(k++), 16));
          i++;
        } 
        return results;
      }
      
      public void Base64EncodingExample(){
    	        String orig = "original String before base64 encoding in Java";

    	        //encoding  byte array into base 64
    	        byte[] encoded = Base64.encodeToByte(orig.getBytes(), true);     
    	      
    	       // Log.i("Original String: " , orig + ":" + orig.length() );
    	        Log.i("Base64 Encoded String : " , new String(encoded) + ":" + encoded.length);
    	      
    	        //decoding byte array into base64
    	        byte[] decoded = Base64.decode(encoded);      
    	        Log.i("Base 64 Decoded  String : ", new String(decoded));

    	    }     
      
      public void Signature() throws Exception {
    	   //genero claves RSA
    	    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    	    kpg.initialize(1024);
    	    KeyPair keyPair = kpg.genKeyPair();
    	    
    	    //data es el texto codificado en UTF-8
    	    byte[] data = "test".getBytes("UTF8");
    	    
    	    //Creo la firma
    	    //Signature sig = Signature.getInstance("MD5WithRSA");
    	    Signature sig = Signature.getInstance("MD5WithRSA");
    	    //Obtengo la clave privada del par creado anteriormente
    	    sig.initSign(keyPair.getPrivate());
    	    //firmo el mensaje CON MI CLAVE PRIVADA
    	    sig.update(data);
    	    byte[] signatureBytes = sig.sign();
    	    Log.i("Singature:", Base64.encodeToString(signatureBytes, false));
    	    //verifico la firma CON LA CLAVE PUBLICA
    	    sig.initVerify(keyPair.getPublic());
    	    sig.update(data);
    	    Log.i("Verificacion de firma", Boolean.toString(sig.verify(signatureBytes)));   
    	  }
      
      
      /** Encodes a raw byte array into a BASE64 <code>String</code> representation i accordance with RFC 2045.
  	 * @param sArr The bytes to convert. If <code>null</code> or length 0 an empty array will be returned.
  	 * @param lineSep Optional "\r\n" after 76 characters, unless end of file.<br>
  	 * No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
  	 * little faster.
  	 * @return A BASE64 encoded array. Never <code>null</code>.
  	 */
  	public final static byte[] encodeStrToByte(String sArr)
  	{
  	        // Reuse char[] since we can't create a String incrementally anyway and StringBuffer/Builder would be slower.
  	        return Base64.encodeToByte(sArr.getBytes(), true);
  	}
}