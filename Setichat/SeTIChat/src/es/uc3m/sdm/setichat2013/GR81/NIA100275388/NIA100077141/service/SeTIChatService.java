
package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service;



import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Time;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.gvsu.cis.masl.channelAPI.ChannelAPI;
import edu.gvsu.cis.masl.channelAPI.ChannelService;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.LoginActivity;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity.MainActivity;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity.SeTIChatConversationActivity;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.utils.Base64;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.renderscript.Element;
import android.util.Log;
import android.widget.Toast;

/**
 * This service is used to connecto to the SeTIChat server. 
 * It should remain running even if the app is not in the foreground
 *  
 * 
 * @author Guillermo Suarez de Tangil <guillermo.suarez.tangil@uc3m.es>
 * @author Jorge Blasco Al�s <jbalis@inf.uc3m.es>
 */

public class SeTIChatService extends Service implements ChannelService {

	String msjDescifrado=null;
	String listaContactos="";
	String [] listaTelefonosContactos = null;
	String [] listcontactos;
	String [] listkey;
	String pass;
	public String getPass() {
		return pass;
	}
	public boolean isCifrar() {
		return cifrar;
	}



	public boolean isFirmar() {
		return firmar;
	}
	public boolean cifrar = true;
	public boolean firmar = true;
	
	public void setCifrar(boolean cifrar) {
		this.cifrar = cifrar;
	}

	public boolean firmaCorrecta = false;




	public void setFirmar(boolean firmar) {
		this.firmar = firmar;
	}


	//Cifrado
	Cifrador cf = new Cifrador();
	//Almaceno claves
	PublicKey pub;
	//PrivateKey priv;
	Key priv;
	String Spriv;
	String Spub;

	
	public String[] getListcontactos() {
		return listcontactos;
	}



	public void setListcontactos(String[] listcontactos) {
		this.listcontactos = listcontactos;
	}



	public String[] getListnick() {
		return listaTelefonosContactos;
	}



	public void setListnick(String[] listnick) {
		this.listaTelefonosContactos = listnick;
	}



	public String[] getListtel() {
		return listaNicksContactos;
	}



	public void setListtel(String[] listtel) {
		this.listaNicksContactos = listtel;
	}


	String [] listaNicksContactos = null;

	
	public String getListaContactos() {
		return listaContactos;
	}



	public void setListaContactos(String listaContactos) {
		this.listaContactos = listaContactos;
	}


	//VARIABLES GLOBALES PARA MANEJAR DATOS DE USUARIO
	String nickUsuario = "";
	public String getNickUsuario() {
		return nickUsuario;
	}



	public void setNickUsuario(String nickUsuario) {
		this.nickUsuario = nickUsuario;
	}


	//String tokenUsuario="5A1CA40A4B316915F300FF217A927DAC";//"8BD8A28F96081BF5EF91A95F00A87B41";// - 199999999.100077141;
	String tokenUsuario="";
	String numeroUsuario="";
	public String getTokenUsuario() {
		return tokenUsuario;
	}



	public void setTokenUsuario(String tokenUsuario) {
		this.tokenUsuario = tokenUsuario;
	}

	
	//String numeroUsuario="100275388.100077141";////////////////////////TOCAR ESTO PARA CAMBIAR DE USUARIO DE TRABAJO
	//"100275388.100077141"
	
	public String getNumeroUsuario() {
		return numeroUsuario;
	}



	public void setNumeroUsuario(String numeroUsuario) {
		this.numeroUsuario = numeroUsuario;
	}


	// Used to communicate with the server
	ChannelAPI channel;
	// Used to bind activities
	private final SeTIChatServiceBinder binder=new SeTIChatServiceBinder();

	

	
	public SeTIChatService() {
		Log.i("SeTIChat Service", "Service constructor");
	}

	
	
	  @Override
	  public void onCreate() {
	    super.onCreate();
	    Log.i("SeTIChat Service", "Service created");
	    Log.i("CREACION","SETICHATSERVICE");
	    
	    // SeTIChat connection is seted up in this step. 
	    // Mobile phone should be changed with the appropiate value
	    channel = new ChannelAPI();
		//this.connect("100275388.100077141"); 

	    binder.onCreate(this);
	    
		 
	  }
	  
	  public void conectar (String telefono){
		    this.connect(telefono);
		    Log.e("CONECTAMOS AL SERVIDOR","CON NUMERO: "+telefono);
		  
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
		  Log.i("SeTIChat Service", "Service binded");
		  return(binder);
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    Log.i("SeTIChat Service", "Service destrotyed");
	    // When the service is destroyed, the connection is closed 
	    try {
			channel.close();
		} catch (Exception e){
			System.out.println("Problem Closing Channel");
		}
	    binder.onDestroy();    
	  }
	  

		//Methods exposed to service binders
		// Login user, send message, update public key, etc.
	  
	  	// All of them are implemented with AsyncTask examples to avoid UI Thread blocks.
		 public void connect(String key){
			 final String ki=key;
			 final SeTIChatService current = this;
			 class ChannelConnect extends AsyncTask<String, String, String> {
			    
				 protected String doInBackground(String... keys) {
					 Log.i("Service connect", "Connect test"+ki);
					 String key = keys[0];
					 try {
							channel = new ChannelAPI("https://setichat.appspot.com", key.trim(), current); //Production Example
							channel.open();
							
						} catch (Exception e){
							System.out.println("Something went wrong...");
							Log.i("Service connect", "Error connecting..."+e.getLocalizedMessage());
						}
					 return "ok";
			     }

			     protected void onProgressUpdate(String... progress) {
			         //setProgressPercent(progress[0]);
			     }

			     protected void onPostExecute(String result) {
			         //
			     }
			 }
			 new ChannelConnect().execute(key,key,key);
		 }

		 
		 public void sendMessage(String message, String TelefonoDestino){
			 final String tlf = TelefonoDestino;
			 final String aux=message;
			 class SendMessage extends AsyncTask<String, String, String> {
				 protected String doInBackground(String... messages) {
					 
					 //signUp(numeroUsuario,nickUsuario);//cambiar para otro perfil
					//contactRequest();
					//connection();
					 Log.i("MESNA0",aux+" "+tlf);
					 try {
						chatMessage(tlf, aux);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 return "ok";					 
			     }			 
			 }
			 new SendMessage().execute(message,message,message);
		 }
		 
		 //SLogIn
		 public void LogIn(String message,final String numuser, final String nick, final String password){
			 class SendMessage extends AsyncTask<String, String, String> {
				 protected String doInBackground(String... messages) {
					 if(numuser==null && nick == null){
						 Log.i("LogIN","Usuario ya registrado, PASSWORD:" +password);
						 pass = password;
						 contactRequest();
					 }
					 else
					 {
						 pass = password;
						 Log.i("LogIN","Alta nuevo usuario con pass: "+pass );
						 signUp(numuser,nick);//cambiar para otro perfil
					 }
					 return "ok";
			     } 
			 }
			 new SendMessage().execute(message,message,message);
		 }
		 
		//METODOS DE CADA ENVIO DE MENSAJE DEL TLF AL SERVER 
		 
/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                          CREACION DE METODOS PARA CADA OPCION - SU, CR, CN, UP, DW 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
		 
		public byte[] cifrar(String pwd, String campo) throws Exception{
			String password  = pwd;
			int iterationCount = 1000;
			int saltLength = 8; // bytes; 64 bits
			int keyLength = 256;
	
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[saltLength];
			random.nextBytes(salt);
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
			                    iterationCount, keyLength);
			SecretKeyFactory keyFactory = SecretKeyFactory
			                    .getInstance("PBKDF2WithHmacSHA1");
			byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			SecretKey key = new SecretKeySpec(keyBytes, "AES");
	
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
			byte[] ciphertext = cipher.doFinal(campo.getBytes("UTF-8"));
			return ciphertext;
		}
		 
		 public String signUp(String concatNias, String nick){
			 Log.i("singUp", "send message singUp");
			 //String NIAS = "100275388.100077141";//=concatNias;
			 String NIAS = concatNias;
			 String nombre = nick;//=nick;
			 nickUsuario = nick;
			 Mensaje msj = new Mensaje();
			 String cabecera = msj.creaCabecera(NIAS,"setichat@appspot.com",1,false,false);
			 String [] singUP = new String[2];
			 singUP[0]=nombre;
			 singUP[1]=NIAS;
			 String contenido = msj.creaContenido(1, singUP);
			 String mensaje="<message>"+cabecera+contenido+"</message>";
			 Log.i("NUESTRO", "Envia mensaje "+mensaje);
			 try {
					channel.send(mensaje, "/chat");
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		 public String contactRequest(){
			 
			 //amF2YS5zZWN1cm10eS5zcGVjL1JTQVB1YmxpY0tleVNwZWNANDE3YjRmZjg=
			 
			 
			 String token = tokenUsuario;//8BD8A28F96081BF5EF91A95F00A87B41 - 199999999.100077141
			 String dirServer = "setichat@appspot.com";
			 Log.i("contactRequest", "send message contactRequest");
			 Mensaje mensaje = new Mensaje();
			 String cabecera = mensaje.creaCabecera(token,dirServer,2,false,false);
			 
			 int contador=0;
			 Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			 while (phones.moveToNext())
			 {
			   String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			   String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			   contador++;

			 }
			 phones.close();
			 
			 
			 String [] contactRequestParam = new String[contador+2];
			 int i=0;
			phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			 while (phones.moveToNext())
			 {
			   String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			   String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			   phoneNumber = phoneNumber.replace("-", "");
			   phoneNumber = phoneNumber.replace(" ", "");
			   //Log.i("obteniendo contacto",name);
			   contactRequestParam[i]=phoneNumber;
			   i++;

			 }
			 Log.i("obteniendo contactos","numero de contactos: " + i);
			 
			 //obtengo estos que si existen
			 contactRequestParam[contactRequestParam.length-2]="1645.1645";
			 contactRequestParam[contactRequestParam.length-2]="010010";
			 contactRequestParam[contactRequestParam.length-1]="4567890987";

			 
			 
			 phones.close();
			 
			 String contenido = mensaje.creaContenido(2, contactRequestParam);
			 String msj="<message>"+cabecera+contenido+"</message>";
			 Log.i("NUESTRO", "Envia mensaje "+msj);
			 try {
					channel.send(msj, "/chat");
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		 public String chatMessage(String destino, String mensajeTexto) throws Exception{
			 String token = tokenUsuario;//"5A1CA40A4B316915F300FF217A927DAC" NIA BUENOS
			 Mensaje mensaje = new Mensaje();
			 Log.i("chatMessage", "send message to: "+destino+": "+mensajeTexto);
			//TOCAR AQUI CUANDO SE CIFRE ESTO!!!!
			// String cabecera = mensaje.creaCabecera(token,destino,4,false,false);
			 String cabecera = mensaje.creaCabecera(token,destino,4,cifrar,firmar);
			 String [] MENSAJE = new String[1];
			 cf=new Cifrador();
			 String clavePrivada=null;
			 PublicKey puKey=null;
		if(cifrar)
		{
			 SQLiteManager usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				SQLiteDatabase db = usdbh.getReadableDatabase();
				//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");
				numeroUsuario=numeroUsuario.trim();

				String[] a = new String[1];
				a[0]=destino;
	
				Cursor  c1 = db.rawQuery("SELECT kpublica FROM Contactos WHERE telefono=?",a);//Antes contactos
			
				c1.moveToFirst();
		
				String pk=null;
				
					try{
						Log.i("pk cifrada,",c1.getString(0));
						Log.i("PASS PARA DESCIFRAR:",pass);
						
						Log.i("pk descifrada",cf.descifrarBBDD(pass, c1.getString(0)));
						pk = cf.descifrarBBDD(pass, c1.getString(0));
						Log.i("Obtengo clave de BBDD", c1.getString(0));
					}
					catch (Exception e){
						Log.i("ENTRA EN CATCH1","ENTRA EN CATCH1");
						e.printStackTrace();
					}
				
			
				db.close();
				
				puKey = cf.Stringtokeypublic(pk);
		}
		if(firmar)
		{
			

			 SQLiteManager usdbh10 =new SQLiteManager(this, "DBUsuarios", null, 1);
				SQLiteDatabase dbq = usdbh10.getReadableDatabase();
				//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");

				Cursor  c = dbq.rawQuery("SELECT kprivada FROM Usuarios WHERE telefono=? ",new String[] {numeroUsuario});
				c.moveToFirst();
				
				
				
					try{
						clavePrivada = cf.descifrarBBDD(pass, c.getString(0));
						
						Log.i("Obtengo clavePRIVADA", c.getString(0));
					}
					catch (Exception e){
						Log.i("ENTRA EN CATCH2","ENTRA EN CATCH2");
						e.printStackTrace();
					}
				
				dbq.close();
				
		}
		
				MENSAJE[0]=mensajeTexto;

				 
				String contenidoSinCifrar = mensaje.creaContenido(4, MENSAJE);
				String firma=null;
		if(firmar)
		{
				String textoFirma="";
				textoFirma="<idDestination>"+destino+"</idDestination>"+"<idMessage>"+mensaje.idMessage+"</idMessage>"+contenidoSinCifrar;
				
			 try {
				 PrivateKey key = cf.Stringtokeyprivate(clavePrivada);
				// Log.i("CLAVE PRIVADA",new String(key.getEncoded()));
				 
				 /*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	             FIRMA DE MENSAJE CON CLAVE PRIVADA
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
				firma=cf.firmo(textoFirma, key);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         //NO ES MENSAJE TEXTO; ES MENSAJE TEXTO YA CIFRADO
		}

				
				/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	            CIFRADO DE MENSAJE CON CLAVE PUBLICA
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
		String msj=null;	
		if(cifrar&&firmar)
			{
				 String msjEncriptado = cf.encriptar (mensajeTexto,puKey);
				 String [] param = new String[]{msjEncriptado};
				 //String msj="<message>"+cabecera+mensaje.creaContenido(4, param)+"</message>";
				 
				msj="<message>"+cabecera+mensaje.creaContenido(4, param)+"<signature>"+firma+"</signature></message>";
			}
			
			if(cifrar&&!firmar)
			{
				 String msjEncriptado = cf.encriptar (mensajeTexto,puKey);
				 String [] param = new String[]{msjEncriptado};
				 //String msj="<message>"+cabecera+mensaje.creaContenido(4, param)+"</message>";
				 
				msj="<message>"+cabecera+mensaje.creaContenido(4, param)+"</message>";
			}
			if(!cifrar&&!firmar)
			{
				
				msj="<message>"+cabecera+mensaje.creaContenido(4, new String[]{mensajeTexto})+"</message>";
			}
			 Log.i("NUESTRO MENSAJE A ENVIAR", "Envia mensaje "+msj);
			 try {
					channel.send(msj, "/chat");
					
					Time time = new Time(System.currentTimeMillis());
					SQLiteManager usdbh1 =new SQLiteManager(this, "DBUsuarios", null, 1);
					SQLiteDatabase db1 = usdbh1.getReadableDatabase();
					//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");

					db1.execSQL("INSERT INTO Mensajes (Origen,Destino,mensaje,fecha) VALUES ('"+numeroUsuario+"','"+destino+"','"+cf.cifrarBBDD(pass, mensajeTexto)+"','"+time+"') ");
					
					db1.close();
					
				
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		 public String connection(){
			 String token =tokenUsuario;
			 Mensaje mensaje = new Mensaje();
			 
			 Log.i("conecction", "Enviamos al servidor mensaje de que me he conectado");
			 
			 String cabecera = mensaje.creaCabecera(token,"setichat@appspot.com",5,false,false);


			 
			 String contenido = mensaje.creaContenido(5, null);
			 String msj="<message>"+cabecera+contenido+"</message>";
			 Log.i("NUESTRO", "Enviamos conectado: "+msj);
			 try {
					channel.send(msj, "/chat");
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		 public String KeyRequest(String s){
			 String parametros[]= new String[1];
			 parametros[0]=s;
			 String token =tokenUsuario;
			 Mensaje mensaje = new Mensaje();
			 Log.i("keyrequest", "Enviamos al servidor mensaje de peticion de key");
			 String cabecera = mensaje.creaCabecera(token,"setichat@appspot.com",10,false,false);
			 String contenido = mensaje.creaContenido(8, parametros);
			 String msj="<message>"+cabecera+contenido+"</message>";
			 Log.i("NUESTRO", "Enviamos conectado: "+msj);
			 try {
					channel.send(msj, "/chat");
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		 //genero las claves publica y privada y las almaceno
		 public String Upload(String s){

			 String parametros[]= new String[1];

			 if(s==null){
				 cf= new Cifrador();
				 KeyPair par_claves = Cifrador.generateRsaKeyPair();
				 priv=par_claves.getPrivate();
				 pub= par_claves.getPublic();
				 Spriv=	 Base64.encodeToString(priv.getEncoded(), false);
				 Spub=	Base64.encodeToString(pub.getEncoded(), false);
				 Log.i("Muestro clave Publica", Spub);
				 Log.i("Muestro clave Privada", Spriv);
				 parametros[0]=Spub;
			 }
			 else{
				 parametros[0]=s;
			 }
			 
			 String token =tokenUsuario;
			 Mensaje mensaje = new Mensaje();
			 Log.i("keyupload", "Enviamos al servidor nuestra clave publica");
			 String cabecera = mensaje.creaCabecera(token,"setichat@appspot.com",9,false,false);
			 String contenido = mensaje.creaContenido(10, parametros);
			 String msj="<message>"+cabecera+contenido+"</message>";
			 Log.i("NUESTRO", "Enviamos conectado: "+msj);
			 try {
					channel.send(msj, "/chat");
				} catch (IOException e) {
					System.out.println("Problem Sending the Message");
				}
			 return "ok";
		 }
		 
		
/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         fin CREACION DE METODOS PARA CADA OPCION - SU, CR, CN, UP, DW 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/		 
		 
		 
	     protected void onProgressUpdate(String... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	    	// TODO Auto-generated method stub
	    	
	     }
		 
		 // Callback method for the Channel API. This methods are called by ChannelService when some kind 
		 // of event happens
		 
		 
		 /**
		  *  Called when the client is able to correctly establish a connection to the server. In this case,
		  *  the main activity is notified with a Broadcast Intent.
		  */
		@Override
		public void onOpen() {
			Log.i("onOpen", "Channel Opened");
			String intentKey = "es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CHAT_OPEN";
			Intent openIntent = new Intent(intentKey);
			// �Why should we set a Package?
			openIntent.setPackage("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141");
			Context context = getApplicationContext();
			context.sendBroadcast(openIntent);  
			
		}

		
/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        RECEPCION DE LOS MENSAJES QUE RECIBO DEL METODO PARSEARMENSAJES() DE LA CLASE MENSAJE 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
		/**
		  *  Called when the client receives a chatMessage. In this case,
		  *  the main activity is notified with a Broadcast Intent.
		  */
		@Override
		public void onMessage(String message) {
			//Log.i("onMessage", "Message received :"+message);
			
			
			//recibo resultado del metodo ParsearMensaje()
			String resultadoPM;
			
			//Array para controlar el contactrequest
			Mensaje mensaje = new Mensaje(message);
			resultadoPM = mensaje.parsearMensaje();
			Log.i("!!!!!!!!ResultadoPM: ",resultadoPM);

			char clave=resultadoPM.charAt(0);
			String r= ""+clave;
			Log.i("clave resultado",r);
			
			Log.i("!!!!!!!!!!!!!!!!!!!!!!!! ",tokenUsuario+nickUsuario+numeroUsuario);
			//en funcion de los dos primeros caracteres enviados por la clase Mensaje en el metodo ParsearMensaje, ejecuto los metodos.SIGNUP
			SQLiteManager usdbh;
			SQLiteDatabase db;
			
			String intentKey;
			Intent openIntent;
			Context context;
			
			switch(clave) {
			case '1'://TOKEN (SIGNUP)
				tokenUsuario=resultadoPM.substring(1,(resultadoPM.length()));
				//Log.e("!!!!!!!!token: ",tokenUsuario);
				//Log.e("!!!!!!!!numeroUsuario: ",numeroUsuario);
				 usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				 db = usdbh.getReadableDatabase();
				 //db.execSQL("DELETE FROM Usuarios WHERE telefono='"+numeroUsuario+"' ");
				// Log.i("!!!!!!!!delete BBDD: ","borro linea");
				 
				 Log.i("!!!!!!!!insert BBDD sin cifrar: ",tokenUsuario+numeroUsuario+" pass: "+ pass);
				 String passcifrada=pass;
				 					
					 passcifrada= cf.cifrarBBDD(pass, pass);///////////////////////////
				
				 Log.i("PASS CIFRADA",passcifrada+" - " +cf.cifrarBBDD(pass, passcifrada));
				 try {
					Log.i("DESCIFRANDO PASS,", cf.descifrarBBDD(pass, passcifrada)+" - " +cf.descifrarBBDD(pass, cf.cifrarBBDD(pass, passcifrada)));
				} catch (InvalidKeyException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (NoSuchAlgorithmException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (NoSuchPaddingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IllegalBlockSizeException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (BadPaddingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (InvalidAlgorithmParameterException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				 db.execSQL("INSERT INTO Usuarios (telefono,nick,token,password) VALUES ('"+numeroUsuario+"','"+cf.cifrarBBDD(pass, nickUsuario)+"','"+cf.cifrarBBDD(pass, tokenUsuario)+"','"+cf.cifrarBBDD(pass, passcifrada)+"') ");
				
	            db.close();
	           // pass = null;
	            //Una vez recibo confirmacion de signup y el token, hago un upload de claves
	            Upload(null);
	            
	            break;
	            
			case '2'://CONTACT-REQUEST
				
				Log.i("CR recibido de PM", resultadoPM);
				
				int contador=0;
				for(int i=0;i<resultadoPM.length();i++){
					if(resultadoPM.charAt(i)=='/')
						contador++;
				}
				
				String resultado = resultadoPM.substring(1);
				listcontactos=resultado.split("/");
				listaTelefonosContactos = new String[contador];
				listaNicksContactos = new String[contador];
				
				for(int i=0;i<contador;i++)
				{
					//Log.i("mostrando contactos", listcontactos[i]);
					listaTelefonosContactos[i]=listcontactos[i].split("-")[0];
					listaNicksContactos[i]=listcontactos[i].split("-")[1];
					Log.i("nick  telefono",listaTelefonosContactos[i]+"   "+listaNicksContactos[i]);
				}
				 usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				 db = usdbh.getReadableDatabase();
				 db.execSQL("DROP TABLE IF EXISTS Contactos");
				 db.execSQL("CREATE TABLE Contactos (telefono TEXT, telfnick TEXT, kpublica TEXT)");
				
//				 for (int i = 0 ; i < contador ; i ++){
//					 db.execSQL("INSERT INTO Contactos (telefono,telfnick) VALUES ('"+cf.cifrarBBDD(pass, listtel[i])+"','"+cf.cifrarBBDD(pass, listnick[i])+"') ");
//				 }
				// db.execSQL("DROP TABLE IF EXISTS Mensajes");
				 db.execSQL("CREATE TABLE IF NOT EXISTS Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT, fecha TEXT)");
				 
	            //Cerramos la base de datos
	            db.close();
				listaContactos=resultadoPM;
				Log.i("CREANDO BR service","STS");
				intentKey = "es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CONTACTREC";
				openIntent = new Intent(intentKey);
				openIntent.putExtra("Contactos",listcontactos );
				// �Why should we set a Package?
				openIntent.setPackage("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141");
				context = getApplicationContext();
				context.sendBroadcast(openIntent); 
				
				// Acciones siguientes a ejecutar
			
				
				for(int i =0; i<listaNicksContactos.length;i++){
					KeyRequest(listaTelefonosContactos[i]);
				}
				
				connection();
				
				break;
				
			case '3'://CONNECTION
				Log.i("CN recibido de PM", resultadoPM);
				usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				db = usdbh.getReadableDatabase();
				Cursor  c;
				
					/*
	                * MUESTRO BBDD
	                */
	               
	               Log.i("/////////BBDD-U////////","/////////////////////////////");
	                
	                c = db.rawQuery("SELECT * FROM Usuarios",null);
	                if (c.moveToFirst()) {
	                    //Recorremos el cursor hasta que no haya m�s registros
	                    do {
	                        
	          	    	   Log.i("Campos de la BBDD U ",c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+ " "+c.getString(3) + " " + c.getString(4)+"  pass: "+c.getString(5));
	          	    	   try {
							Log.i("USUARIOS DESCIFRADO ",c.getString(0)+" "+cf.descifrarBBDD(pass, c.getString(1))+" "+cf.descifrarBBDD(pass, c.getString(2))+ " "+cf.descifrarBBDD(pass, c.getString(3)) + " " + cf.descifrarBBDD(pass, c.getString(4))+"  pass: "+cf.descifrarBBDD(pass, c.getString(5)));
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    } while(c.moveToNext());
	                }
	                
	                Log.i("/////////BBDD-C////////","/////////////////////////////");
	                
	                c = db.rawQuery("SELECT * FROM Contactos",null);
	                if (c.moveToFirst()) {
	                    //Recorremos el cursor hasta que no haya m�s registros
	                    do {
	                       Log.i("Campos de la BBDD C ",c.getString(0)+" "+c.getString(1)+" "+c.getString(2));
	                       try {
							Log.i("CONTACTOS DESCIFRADO",c.getString(0)+" "+cf.descifrarBBDD(pass, c.getString(1))+" "+cf.descifrarBBDD(pass, c.getString(2)));
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    } while(c.moveToNext());
	                }
	                
	                /*Log.i("/////////BBDD-M////////","/////////////////////////////");
	                
	                c = db.rawQuery("SELECT * FROM Mensajes",null);
	                if (c.moveToFirst()) {
	                    //Recorremos el cursor hasta que no haya m�s registros
	                    do {
	                    	Log.i("Campos de la BBDD M ",c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+ " " + c.getString(3));
	          	    	   
	          	    	 try {
							Log.i("MENSAJES DESCIFRADO",c.getString(0)+" "+c.getString(1)+" "+cf.descifrarBBDD(pass, c.getString(2))+ " "+c.getString(3));
						} catch (InvalidKeyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    } while(c.moveToNext());
	                }*/
	                db.close();
				
				
				
				break;
				
			case '4'://CHAT-MESSAGE

				Log.i("CH recibido de PM", resultadoPM+" "+(resultadoPM.length()-1));
				String msj=resultadoPM.substring(1,resultadoPM.length());
				Log.i("Mensaje encriptado?",mensaje.encrypted);
				Log.i("Mensaje firmado?",mensaje.signed);
				
				
					Log.i("Paso a desencriptar mensaje recivido","GO GO");
					
					 usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
					 db = usdbh.getReadableDatabase();
						//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");
					 Cursor  c2 = db.rawQuery("SELECT kprivada,kpublica FROM Usuarios",null);
					c2.moveToFirst();
					try {
						if(mensaje.encrypted.equals("true"))
						{
							Spriv=cf.descifrarBBDD(pass, c2.getString(0));
							priv= cf.Stringtokeyprivate(cf.descifrarBBDD(pass, c2.getString(0)));
						}
						if(mensaje.signed.equals("true"))
							Spub=cf.descifrarBBDD(pass, c2.getString(1));
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					db.close();
					msjDescifrado=msj;
					if(mensaje.encrypted.equals("true"))
					{
						try {
							
								msjDescifrado = cf.desencriptar(msj, priv);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						Log.i("MENSAJE DESCIFRADO: ",msjDescifrado);
					}
				
					if(mensaje.signed.equals("true"))
					{
					 Log.i("chatMessageSERVICE", "Obtengo clave de BBDD");
					 SQLiteManager usdbh10 =new SQLiteManager(this, "DBUsuarios", null, 1);
						SQLiteDatabase dbq = usdbh10.getReadableDatabase();
						//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");
	
						c2 = dbq.rawQuery("SELECT kpublica FROM Contactos WHERE telefono=? ",new String[] {mensaje.idSource});
						c2.moveToFirst();
						
						String clavepublica=null;
						
							try{
								clavepublica = cf.descifrarBBDD(pass, c2.getString(0));
								Log.i("Obtengo clave PUBLICA", c2.getString(0));
							}
							catch (Exception e){
								Log.i("ENTRA EN CATCH3","ENTRA EN CATCH3");
								e.printStackTrace();
							}
						
						dbq.close();
						byte[] aVerificar =mensaje.cuerpo.substring(mensaje.cuerpo.indexOf("<signature>")+11,mensaje.cuerpo.indexOf("</signature>")).getBytes();
						byte[] averi = Base64.decode(aVerificar);
						PublicKey key;
						try {
							key = cf.Stringtokeypublic(clavepublica);
							if(mensaje.signed.equals("true")){
								Log.i("Mensaje firmado","Verifico la firma");
								Log.i("PAR DE CLAVES PRIVADA", Spriv + ":" + Spriv.length());
								Log.i("PAR DE CLAVES PUBLICA", Spub + ":" + Spub.length() );
								Log.i("PAR DE CLAVES RECIBIDA", clavepublica + ":" + clavepublica.length());
								
								Log.i("cuerpo",mensaje.cuerpo);
								
										//msj.substring(msj.indexOf("<signature>"+11,msj.indexOf("</signature>"))).getBytes();
								Log.i("Voy a verificar la firma:",new String(aVerificar)+ "     "+ aVerificar.length);
								
								/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					             VERIFICO MENSAJE
								/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
								
								String textoFirma="<idDestination>"+mensaje.idDestination+"</idDestination>"+"<idMessage>"+mensaje.idMessage+"</idMessage>"+
										"<content><chatMessage>"+msjDescifrado+"</chatMessage></content>";
								Log.i("MENSAJE DESCIFRADO: ",msjDescifrado);
								Log.i("Texto a verificar con la firma: ",textoFirma);
							
								firmaCorrecta = cf.verifico(textoFirma.getBytes(), key, averi);
								//String A = new String(aVerificar);
								

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					
					
				Time time = new Time(System.currentTimeMillis());
				usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				db = usdbh.getReadableDatabase();
				//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");

				db.execSQL("INSERT INTO Mensajes (Origen,Destino,mensaje,fecha) VALUES ('"+mensaje.idSource+"','"+mensaje.idDestination+"','"+cf.cifrarBBDD(pass, msjDescifrado)+"','"+time+"') ");
	            //Cerramos la base de datos
	            db.close();
	            
	            
	            
				intentKey = "es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.MSJREC";
				openIntent = new Intent(intentKey);
				openIntent.putExtra("Mensaje",resultadoPM.substring(2));
				if(firmaCorrecta)
					openIntent.putExtra("FIRMA","SI");
				else
					openIntent.putExtra("FIRMA","NO");
				
				openIntent.setPackage("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141");
				context = getApplicationContext();
				context.sendBroadcast(openIntent); 
				break;
			
			case '5'://MESSAGE
				Log.i("MS recibido de PM", resultadoPM);
				break;
				
			case '6'://ERROR
				Log.i("Error recibido de PM", resultadoPM);
				
//				if(resultadoPM.charAt(1)=='4')
//					startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//				Toast.makeText(this, "Usuario ya registrado, utilize otro nombre", // R.string.local_service_disconnected,
//						Toast.LENGTH_SHORT).show();
				break;
				
			case '7'://KEY-REGISTER
				Log.i("Clave Succesfully Registered recibido de PM", resultadoPM);
				
				//KEY REGISTER SE GUARDA EN LA BBDD USUARIO en el campo kpublica y kprivada
				
				usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				 db = usdbh.getReadableDatabase();
				 db.execSQL("DELETE FROM Usuarios");
				// Log.i("!!!!!!!!delete BBDD: ","borro linea");
				 
				 Log.i("!!!!!!!!insert BBDD UserPrivateKey: ",Spriv);
				 
				 db.execSQL("INSERT INTO Usuarios (telefono,nick,token,kpublica,kprivada,password) VALUES ('"+numeroUsuario+"','"+cf.cifrarBBDD(pass, nickUsuario)+"','"+cf.cifrarBBDD(pass, tokenUsuario)+"','"+cf.cifrarBBDD(pass, Spub)+"','"+cf.cifrarBBDD(pass, Spriv)+"','"+cf.cifrarBBDD(pass, pass)+"') ");
				 
				 db.close();
				 
				contactRequest();
				
				break;
				
			case '9':
				String tel=resultadoPM.substring(1,resultadoPM.length());
				usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				 db = usdbh.getReadableDatabase();

				KeyRequest(tel);
				
				break;
			case '8'://KEY-REQUEST
				Log.i("Clave KR recibido de PM", resultadoPM);
				String telefono = resultadoPM.substring(1, resultadoPM.indexOf("key"));
				String key1 = resultadoPM.substring(resultadoPM.indexOf("key") + 3,resultadoPM.length());
				Log.i("ACTUALIZO CON CLAVE","INIT");
				usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
				 db = usdbh.getReadableDatabase();
				
				
				 ContentValues valores = new ContentValues();
				 int pos=-1;
				 for(int i = 0 ; i < listaTelefonosContactos.length; i++)
				 {
					 Log.i("COMPARO TELEFONOS:",listaTelefonosContactos[i]+" - "+telefono);
					 if(telefono.equalsIgnoreCase(listaTelefonosContactos[i]))
						 pos=i;
				 }
				 Log.i("POSICION",pos+"");
				 db.execSQL("DELETE FROM Contactos WHERE telefono='"+telefono+"' ");
				 db.execSQL("INSERT INTO Contactos (telefono,telfnick,kpublica) VALUES ('"+listaTelefonosContactos[pos]+"','"+cf.cifrarBBDD(pass,listaNicksContactos[pos])+"','"+cf.cifrarBBDD(pass, key1)+"') ");
				 Log.i("ACTUALIZO CON CLAVE","FIN"+telefono+", campos actualizados: ");
				 db.close();
				
			}

			intentKey = "es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CHAT_MESSAGE";
			openIntent = new Intent(intentKey);
			openIntent.putExtra("message", message);//Paso el mensaje al intent
			openIntent.setPackage("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141");
			context = getApplicationContext();
			context.sendBroadcast(openIntent);  
			//System.out.println("onMessage mensaje rec");
		}
		
/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        fin recepcionmensajes
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
		
		@Override
		public void onClose() {
			// Called when the connection is closed
			 
			
		}


		@Override
		public void onError(Integer errorCode, String description) {
			// Called when there is an error in the connection
			
		}
	  
}
