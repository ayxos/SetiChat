package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Time;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity.MainActivity;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.Cifrador;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SQLiteManager;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatService;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatServiceBinder;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * This activity will show the conversation with a given contact. 
 * It will allow also to send him new messages and, of course, will refresh when a new message arrives.
 * 
 * If the user is viewing a different conversation when a message arrive from a third party contact,
 * then a notification should be shown. 
 * 
 * @author Guillermo Suarez de Tangil <guillermo.suarez.tangil@uc3m.es>
 * @author Jorge Blasco Alis <jbalis@inf.uc3m.es>
 */

public class LoginActivity extends Activity {

	////////////TOCAR$ ESTA VARIABLE SI LA BBDD ES ANTIGUA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	boolean BBDDnueva= false;
	////////////TOCAR$ ESTA VARIABLE SI LA BBDD ES ANTIGUA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditText edit;
	private ScrollView scroller;
	private TextView text;
	private EditText etnick, ettelefono,ettpass;

	private boolean DEBUG = false;

	private SeTIChatService mService;
	private boolean existeEnBBDD = false;

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mService = SeTIChatServiceBinder.getService();

			DEBUG = true;

		

		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.

			if (DEBUG)
				Log.d("SeTIChatConversationActivity",
						"onServiceDisconnected: un-bounding the service");

			mService = null;
			Toast.makeText(LoginActivity.this, "Disconnected", // R.string.local_service_disconnected,
					Toast.LENGTH_SHORT).show();
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i("CREACION","LOGINACTIVITY");
		
		if (mService == null) {
			// Binding the activity to the service to get shared objects
			if (DEBUG)
				Log.d("SeTIChatConversationActivity", "Binding activity");
			bindService(new Intent(LoginActivity.this,
					SeTIChatService.class), mConnection,
					Context.BIND_AUTO_CREATE);
			
		} 
		
		setContentView(R.layout.activity_login);
		
		etnick = (EditText) findViewById(R.id.nick);
		ettelefono = (EditText) findViewById(R.id.telefono);
		ettpass = (EditText) findViewById(R.id.password);
		
		findViewById(R.id.buttonLogin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							ejecutar(view);
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
						}
						//attemptLogin();
					}
				});
		
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("ONSTOP","login ACTIVITY");
		
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("ONDESTROY","MAIN ACTIVITY");
		unbindService(mConnection);
	}
	

	
	public void ejecutar(View view) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
	 SQLiteManager usdbh =new SQLiteManager(this, "DBUsuarios", null, 1);
	 String telD=null;
	 String nickD=null;
	 String tokD=null;
	 String pass = null;
	 SQLiteDatabase db = usdbh.getReadableDatabase();
	// db.execSQL("DROP TABLE IF EXISTS Usuarios");
	// db.execSQL("CREATE TABLE Usuarios (telefono TEXT, nick TEXT, token TEXT, kpublica TEXT, kprivada TEXT, password TEXT)");

	 if(!ettelefono.getText().toString().equals("")&&!etnick.getText().toString().equals("")&&!ettpass.getText().toString().equals("")){
		 db.execSQL("DROP TABLE IF EXISTS Usuarios");
		 db.execSQL("CREATE TABLE Usuarios (telefono TEXT, nick TEXT, token TEXT, kpublica TEXT, kprivada TEXT, password TEXT)");
	 }
	Log.i("CAMPOS INTRODUCIDOS",ettelefono.getText().toString()+etnick.getText().toString()+ettpass.getText().toString());
	 
	 /* if(BBDDnueva == true){
		db.execSQL("DROP TABLE IF EXISTS Usuarios");
		db.execSQL("CREATE TABLE Usuarios (telefono TEXT, nick TEXT, token TEXT, kpublica TEXT, kprivada TEXT)");
		db.execSQL("INSERT INTO Usuarios (telefono,nick,token) VALUES ('100275388.100077141','calobro','5A1CA40A4B316915F300FF217A927DAC') ");
		db.execSQL("INSERT INTO Usuarios (telefono,nick,token) VALUES ('177141.1275388','lala','8ACE5D73E2A86DF68403A24663CA3221') ");
		db.execSQL("INSERT INTO Usuarios (telefono,nick,token) VALUES ('77.22','o','D17E5A3CCE253C26BC29751DAD751065') ");
		db.execSQL("INSERT INTO Usuarios (telefono,nick,token) VALUES ('14177.885327','atacabro','0FF4318B4047C44635F49B9AE610D434') ");
	 }
	 
	 else{
	*/	 
	Cifrador cf = new Cifrador();
	
	//db.execSQL("DROP TABLE IF EXISTS Mensajes");
               Cursor  c = db.rawQuery("SELECT * FROM Usuarios",null);
              //AQUI SIEMPRE ENTRA
               c.moveToFirst();
               boolean PASSCORRECTA = false;
               if (  c.getCount()!=0) {
            	   		Log.i("BBDDLogIN","Existe en BBDD");
                         try {
                        	 PASSCORRECTA =cf.comprobarPASS (ettpass.getText().toString().trim(), c.getString(5));
							 if(PASSCORRECTA){
	                        	 telD = c.getString(0);
		                         nickD = cf.descifrarBBDD(ettpass.getText().toString().trim(),c.getString(1));
		                         tokD = cf.descifrarBBDD(ettpass.getText().toString().trim(),c.getString(2));
		                         pass = cf.descifrarBBDD(ettpass.getText().toString().trim(),c.getString(5));
							 }
						} catch (InvalidAlgorithmParameterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							db.close();
						}

                         Log.i("DATOS USER"," telD: "+telD+" nickD: "+nickD+" tokD: "+tokD+" pass: "+pass);
                         existeEnBBDD=true;
               }
               
               
            //Cerramos la base de datos
            db.close();
       // }
       
       Intent i=new Intent(this, MainActivity.class);
	if(existeEnBBDD==true){
        /*
         * 
         * COMPROBAR PASS
         * 
         */

		//Cifrar la pass que se ha introducido
		
		
		
		
		
		//String passIntroducidaCifrada = cf.cifrarBBDD(ettpass.getText().toString(), ettpass.getText().toString());
		Log.i("PASSWORDS",ettpass.getText().toString() +" - "+ pass);
		if(PASSCORRECTA)
		{
	        i.putExtra("nick", nickD);
	        i.putExtra("telefono", telD);
	        i.putExtra("token", tokD );
	        i.putExtra("QueHacer", "SU");
	        mService.setTokenUsuario(tokD);
	        mService.setNumeroUsuario(telD);
	        mService.setNickUsuario(nickD);
	        mService.conectar(telD);
	        Log.e("TOKEN CONEXION",mService.getTokenUsuario()+" "+telD);
	        //toast muestra en pantalla como alert
	        Toast.makeText(this, "token: " + tokD, Toast.LENGTH_SHORT).show();
	        //contactrequest inicia el proceso si ya esta registrado
	        Log.i("Hago LogIn", "Usuario ya existente");
	        mService.LogIn("", null, null,ettpass.getText().toString());
	        //mService.getListaContactos();
	        Log.i("listaContactos", "obtengo lista de contactos");
	        Toast.makeText(this, "Contraseña CORRECTA", Toast.LENGTH_SHORT).show();
	        startActivity(i);

			
		}
		else{
			Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
		}
		
		

	}else{
		Log.i("EL USUARIO NO EXISTE","NO EXISTE");
		//PutExtra para pasar entre activitys
        i.putExtra("nick", etnick.getText().toString());
        i.putExtra("telefono", ettelefono.getText().toString());
        i.putExtra("password", ettpass.getText().toString());
        //mService para Actividad-Proceso
        String tel = ettelefono.getText().toString();
        mService.setNumeroUsuario(tel);
        mService.setNickUsuario(etnick.getText().toString());
        
        mService.conectar(ettelefono.getText().toString());
        Log.i("NUEVO USUARIOs",ettelefono.getText().toString()+ etnick.getText().toString());
        //sendmessagesignup inicia el proceso para los no registrados
        mService.LogIn("", ettelefono.getText().toString(), etnick.getText().toString(),ettpass.getText().toString());
        String token = mService.getTokenUsuario();
        Log.i("TOKEN CREADO",token);
        Toast.makeText(this, "REGISTRANDO USUARIO", Toast.LENGTH_SHORT).show();
        startActivity(i);
        SQLiteDatabase db2 = usdbh.getReadableDatabase();
        db2.execSQL("DROP TABLE IF EXISTS Mensajes");
        db2.close();
	}
    
}



}
