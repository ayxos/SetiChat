package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.Cifrador;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SQLiteManager;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatService;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatServiceBinder;

import android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SeTIChatConversationActivity extends Activity {
	private BroadcastReceiver openReceiver;
	
	private String telefonoDestino;
	private String nickDestino;
	String mensajedes;
	private EditText edit;
	private ScrollView scroller;
	private TextView text;
	
	private boolean DEBUG = false;

	private static final int NOTIFY_ME_ID=1987;
    private int count=0;
    private NotificationManager mgr=null;
	
	private SeTIChatService mService;
	Intent intentchat;
	

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mService = SeTIChatServiceBinder.getService();

			DEBUG = true;

			render();

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
			Toast.makeText(SeTIChatConversationActivity.this, "Disconnected", // R.string.local_service_disconnected,
					Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("CREACION","SETICHATCONVERSATIONACTIVITY");
		if (mService == null) {
			// Binding the activity to the service to get shared objects
			if (DEBUG)
				Log.d("SeTIChatConversationActivity", "Binding activity");
			intentchat = new Intent(SeTIChatConversationActivity.this,SeTIChatService.class);
			bindService(intentchat, mConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			render();
		}
		
		Intent intent = getIntent();
		telefonoDestino = intent.getStringExtra("Telefono").split("-")[0];
		nickDestino = intent.getStringExtra("Telefono").split("-")[1];
		


		

		
		
		IntentFilter openFilter = new IntentFilter();
		openFilter.addAction("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.MSJREC");

		 openReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	Log.i("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2");
		    	Context context1 = getApplicationContext();
		    	mostrarConversacion();
		    	//createNotification(conversationView());
				String firmadoOK = intent.getStringExtra("FIRMA");
				Log.i("EL FIRMADO ES",firmadoOK+"");
				if(firmadoOK.equals("SI"))
				{
				
				Toast.makeText(SeTIChatConversationActivity.this, "FIRMA DEL MENSAJE CORRECTA", // R.string.local_service_disconnected,
						Toast.LENGTH_SHORT).show();
				}
			else 
				{
				Toast.makeText(SeTIChatConversationActivity.this, "FIRMA DEL MENSAJE INCORRECTA", // R.string.local_service_disconnected,
						Toast.LENGTH_SHORT).show();
				}
			
			Log.i("EN CONVERSACIon","Telefono destino:"+telefonoDestino+" nick: "+nickDestino);
		    }
		  };
		  registerReceiver(openReceiver, openFilter);
	}
	
	public void notifyMe(View v) {
        Notification note=new Notification(R.drawable.stat_notify_chat,"SeTIChat message!! wii :)",System.currentTimeMillis());
        PendingIntent i=PendingIntent.getActivity(this, 0,new Intent(this, NotificationMessage.class),0);
        note.setLatestEventInfo(this, "SeTIChat: "+nickDestino,mensajedes, i);
        note.number=++count;
        note.vibrate=new long[] {500L, 200L, 200L, 500L};
        note.flags|=Notification.FLAG_AUTO_CANCEL;            
        
        mgr.notify(NOTIFY_ME_ID, note);
      }
	
      
	public void clearNotification(View v) {
		mgr.cancel(NOTIFY_ME_ID);
	}
	
	public void createNotification(View view) {
		// Build notification
		// Actions are just fake
		NotificationManager notificationManager 
		  = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		  
		  PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intentchat, 0);
		  
		Notification n = new Notification.Builder(this)
				.setContentTitle("SeTIChat: "+nickDestino)
				.setContentText(mensajedes)
				.setSmallIcon(R.drawable.stat_notify_chat)
				.setContentInfo("ContentInfo")
				  .setTicker("SeTIChat message")
				  .setLights(0xFFFF0000, 500, 500) //setLights (int argb, int onMs, int offMs)
				  .setContentIntent(pendingIntent)
				  .setAutoCancel(true)
				.getNotification();
		// Hide the notification after its selected

		notificationManager.notify(0, n);

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("ONSTOP","conver ACTIVITY");
		Log.i("SeTIChatConversationActivity", "Unbinding activity");
		unbindService(mConnection);
		unregisterReceiver(openReceiver);
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

	private void render() {
		// Tell the user about the service.
		Toast.makeText(SeTIChatConversationActivity.this, "Connected", // R.string.local_service_connected,
				Toast.LENGTH_SHORT).show();

	//	int index = getIntent().getIntExtra("index", -1);
		if (DEBUG)
			Log.d("SeTIChatConversationActivity",
					"onServiceConnected: Rendering conversation based on extra information provided by previous activity intention: "
							);
		setContentView(conversationView());
	}

	public View conversationView() {

		// ***************************************************************** //
		// *********************** Layouts and Views *********************** //
		// ***************************************************************** //

		int padding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());

		// Creating a general layout
		LinearLayout background = new LinearLayout(this);
		background.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		background.setOrientation(LinearLayout.VERTICAL);
		background.setPadding(0, 0, 0, padding);

		// Creating a layout for the edit text and the bottom to be in the
		// button
		LinearLayout background_edit = new LinearLayout(this);
		background_edit.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		background_edit.setOrientation(LinearLayout.HORIZONTAL);

		// Creating the view to show the conversations
		text = new TextView(this);
		text.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		text.setPadding(padding, padding, padding, 0);
		//text.setId(R.id.conversation);
		// Adding some scroll
		scroller = new ScrollView(this);
		scroller.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
		scroller.post(new Runnable() {
			public void run() {
				scroller.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		// Creating the edit text to add new chats
		edit = new EditText(this);
		edit.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f));
		edit.requestFocus();

		// Of course a send button
		Button send = new Button(this);
		send.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 3f));
		send.setText("Send");

		
		
		
		// Setting the conversations
		
		//text.setText("****This is a very easy way to add text into a Text View. This has been done programatically, but could've been done using layouts."); // TODO Use a more fancy layout
		mostrarConversacion();
		
		// Sending messages
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String pass = mService.getPass().trim();
				Cifrador cf = new Cifrador();
				String sms="";
				
				Time time = new Time(System.currentTimeMillis());
				mService.sendMessage(edit.getText().toString(),telefonoDestino);
				//Log.i("STCAct send",edit.getText().toString());
				
				SQLiteManager usdbh =new SQLiteManager(mService.getApplicationContext(), "DBUsuarios", null, 1);
				SQLiteDatabase db = usdbh.getReadableDatabase();
				//db.execSQL("CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT)");
				//Log.i("LA PASS ES:",pass);
				//muestro nueva BBDD
	                Cursor c3 =db.rawQuery("SELECT mensaje,fecha,Origen,Destino FROM Mensajes WHERE Origen='"+telefonoDestino+"' or Destino='"+telefonoDestino+"' ",null);
	                if (c3.moveToFirst()) {
	                    //Recorremos el cursor hasta que no haya m�s registros
	                    do {
	                    	//Log.i("MI NUMERO - NUMERO ORIGEN MENSAJE",mService.getNumeroUsuario()+"asdf"+c3.getString(2));
	            	    	  if(c3.getString(2).equalsIgnoreCase(mService.getNumeroUsuario().trim()))
	        					try {
	        						sms+=mService.getNickUsuario().trim()+": "+cf.descifrarBBDD(pass,c3.getString(0))+" at "+c3.getString(1)+"\n";
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
	        				else
	        					try {
	        						Log.i("MENSAJE DE OTRO:",c3.getString(0)+"  "+cf.descifrarBBDD(pass, c3.getString(0)));
	        						sms+=nickDestino.trim()+": "+cf.descifrarBBDD(pass, c3.getString(0))+" at "+c3.getString(1)+"\n";
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
	          	    	
	                    } while(c3.moveToNext());
	                }
	            //Cerramos la base de datos
	            db.close();
				
				// Refresh textview
	            sms+=mService.getNickUsuario().trim()+": "+edit.getText().toString() + " at " + time +"\n";
				text.setText(sms);
				edit.setText("");
			}
		});
		

		
		// ***************************************************************** //
		// ******** Configuring the Views and returning the layout ******** //
		// ***************************************************************** //

		scroller.addView(text);
		background.setBackgroundColor(102);
		background.addView(scroller);
		background_edit.addView(edit);
		background_edit.addView(send);
		background.addView(background_edit);

		return background;
	}
	public void mostrarConversacion(){
		Time time = new Time(System.currentTimeMillis());
		String sms="";
		SQLiteManager usdbh =new SQLiteManager(mService.getApplicationContext(), "DBUsuarios", null, 1);
		SQLiteDatabase db = usdbh.getReadableDatabase();

		Cifrador cf = new Cifrador();
		String pass = mService.getPass();
		Log.i("Comprobamos mensajes con telefonoDestino:",telefonoDestino);
        Cursor c3 =db.rawQuery("SELECT mensaje,fecha,Origen,Destino FROM Mensajes WHERE Origen='"+ telefonoDestino+"' or Destino='"+telefonoDestino+"' ",null);
        if (  c3.getCount()!=0) {
        if (c3.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
            	//Log.i("MI NUMERO - NUMERO ORIGEN MENSAJE",mService.getNumeroUsuario()+"asdf"+c3.getString(2));
    	    	  if(c3.getString(2).equalsIgnoreCase(mService.getNumeroUsuario().trim()))
					try {
						mensajedes=cf.descifrarBBDD(pass, c3.getString(0));
						sms+=mService.getNickUsuario().trim()+": "+cf.descifrarBBDD(pass,c3.getString(0))+" at "+c3.getString(1)+"\n";
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
				else
					try {
						mensajedes=cf.descifrarBBDD(pass, c3.getString(0));
						Log.i("MENSAJE DE OTRO:",c3.getString(0)+"  "+ mensajedes);
						sms+=nickDestino.trim()+": "+cf.descifrarBBDD(pass, c3.getString(0))+" at "+c3.getString(1)+"\n";
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
  	    	
            } while(c3.moveToNext());
        }
	}
    //Cerramos la base de datos
    db.close();
	
	// Refresh textview
    //sms+=mService.getNumeroUsuario()+": "+edit.getText().toString() + " at " + time +"\n";
		
		// Refresh textview
        //sms+=edit.getText().toString() + " at " + time +"\n";
		text.setText(sms);
		edit.setText("");
	}
	

}
