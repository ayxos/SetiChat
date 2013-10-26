package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity;

import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.*;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatService;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SeTIChatServiceBinder;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service.SoundManager;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.R;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main activity and its used to initialize all the SeTIChat features. 
 * It configures the three tabs used in this preliminary version of SeTIChat.
 * It also start the service that connects to the SeTIChat server.
 * 
 * @author Guillermo Suarez de Tangil <guillermo.suarez.tangil@uc3m.es>
 * @author Jorge Blasco Alis <jbalis@inf.uc3m.es>
 */

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	//Manager de sonidos
	int sonido;
	SoundPool sndPool;
	SoundManager snd;
	
	// Service used to access the SeTIChat server
	private SeTIChatService mService;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	// Receivers that wait for notifications from the SeTIChat server
	private BroadcastReceiver openReceiver;
	private BroadcastReceiver chatMessageReceiver;

	private BroadcastReceiver chatMessageReceiver2;
	ContactsFragment fragment;
	
	
	String[] contactos;
	int contadorfragment;
	ContactsFragment fragmentcontactos;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("CREACION","MAINACTIVITY");
		snd = new SoundManager(getApplicationContext());  
		sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);   
		sonido = snd.load(R.raw.sonido);
		
		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText("Contacts")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Convers")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Tools")
				.setTabListener(this));
		Log.i("Activty", "onCreate");
		
		if (mService == null) {
			// Binding the activity to the service to get shared objects

			Log.d("SeTIChatConversationActivity", "Binding activity");
			bindService(new Intent(MainActivity.this,
					SeTIChatService.class), mConnection,
					Context.BIND_AUTO_CREATE);
			
		} 
		Intent intent = getIntent();

		String nick= intent.getStringExtra("nick");
		String telefono= intent.getStringExtra("telefono");
		String pass = intent.getStringExtra("pass");
		
        Log.i("PARAMETRO 1 DEL ACT1" , nick);
        Log.i("PARAMETRO 2 DEL ACT1", telefono);
		
		try{
	        
	        // Make sure the service is started.  It will continue running
	        // until someone calls stopService().  The Intent we use to find
	        // the service explicitly specifies our service component, because
	        // we want it running in our own process and don't want other
	        // applications to replace it.
	        startService(new Intent(MainActivity.this,
	                SeTIChatService.class));
	        
        }catch(Exception e){

    		Log.d("MainActivity", "Unknown Error", e);

	        stopService(new Intent(MainActivity.this,
	                SeTIChatService.class));
        }
		
		
		// Create and register broadcast receivers
		IntentFilter openFilter = new IntentFilter();
		openFilter.addAction("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CHAT_OPEN");

		 openReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	Context context1 = getApplicationContext();
				CharSequence text = "SeTIChatConnected";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context1, text, duration);
				toast.show();
		    }
		  };

		  registerReceiver(openReceiver, openFilter);
		  
		  chatMessageReceiver = new BroadcastReceiver() {
			    @Override
			    public void onReceive(Context context, Intent intent) {//////////////////////////////////////////
			      //do something based on the intent's action
			    	Context context1 = getApplicationContext();
			    	String mensaje =	intent.getStringExtra("message");
			    	CharSequence text = "SeTIChat Message Received "+mensaje;
					int duration = Toast.LENGTH_SHORT;
					
					snd.play(sonido);
					
					Toast toast = Toast.makeText(context1, text, duration);
					toast.show();
			    }
			  };
			  
		IntentFilter chatMessageFilter = new IntentFilter();
		chatMessageFilter.addAction("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CHAT_MESSAGE");
		registerReceiver(chatMessageReceiver, chatMessageFilter);
		
		
		  chatMessageReceiver2 = new BroadcastReceiver() {
			    @Override
			    public void onReceive(Context context, Intent intent) {//////////////////////////////////////////
			      //do something based on the intent's action
			    	Log.i("CREANDO BR service","MA");
			    	Context context1 = getApplicationContext();
			    	String mensaje =	intent.getStringExtra("message");
			    	contactos = intent.getStringArrayExtra("Contactos");
			    	CharSequence text = "Ya tengo los contactos, refresco fragment";
					int duration = Toast.LENGTH_SHORT;
					
					//snd.play(sonido);
					Log.i("CREANDO BR service","Llamo a listcontactos " + contactos.length);
					Log.i("CREANDO BR service",contactos[0]);	
					cambiarfragment(contactos);
					fragmentcontactos=fragment;
					
					
					Toast toast = Toast.makeText(context1, text, duration);
					toast.show();
			    }
			  };
			  
		IntentFilter chatMessageFilter2 = new IntentFilter();
		chatMessageFilter2.addAction("es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.CONTACTREC");
		registerReceiver(chatMessageReceiver2, chatMessageFilter2);
		/*Intent i=new Intent(this, LoginActivity.class);
		startActivity(i);*/
	}
	
	
	@Override
	  public void onDestroy() {
	    super.onDestroy();
        // We stop the service if activity is destroyed
	    stopService(new Intent(MainActivity.this,
                SeTIChatService.class));
	    // We also unregister the receivers to avoid leaks.
        unregisterReceiver(chatMessageReceiver);
        unregisterReceiver(chatMessageReceiver2);
        unregisterReceiver(openReceiver);
	 }
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.v("MainActivity", "onResume: Resuming activity...");
		super.onResume();
	}



	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}
	
	private TextView lblMensaje;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
//	    menu.add(Menu.NONE, MNU_OPC1, Menu.NONE, "Opcion1")
//        .setIcon(android.R.drawable.ic_menu_preferences);
//		menu.add(Menu.NONE, MNU_OPC2, Menu.NONE, "Opcion2")
//		        .setIcon(android.R.drawable.ic_menu_compass);
//		menu.add(Menu.NONE, MNU_OPC3, Menu.NONE, "Opcion3")
//		        .setIcon(android.R.drawable.ic_menu_agenda);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.MnuOpc1:
	          
	            return true;
	        case R.id.MnuOpc2:
	        	
	            return true;
	            
	        case R.id.SubMnuOpc1:
	       
	           getService().setCifrar(true);
	        	Log.i("activo cifrado","activo cifrado");
	            return true;
	            
	        case R.id.SubMnuOpc2:
	        	
	        	getService().setCifrar(false);
	        	Log.i("desactivo cifrado","desactivo cifrado");
	            return true;
	            
	        case R.id.SubMnuOpc3:
	        
	        	getService().setFirmar(true);
	        	Log.i("activar firma","activo firma");
	            return true;
	            
	        case R.id.SubMnuOpc4:   
	        
	        	getService().setFirmar(false);
	        	Log.i("desactivo firma","desactivo firma");
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		
		
		if(tab.getText().equals("Contacts")){
			Log.i("#############################################","###################################mjpmpompom");
			fragment = new ContactsFragment();
			Log.i("contadorfragment",""+contadorfragment);
			if(contadorfragment==0){
				contadorfragment++;
			}else{
				Log.i("cambiando fondo","contactos");
				fragment=fragmentcontactos;
				Log.i("contactos: ",contactos[0] + contactos[1] + contactos.length);
				fragment.setfondo(contactos);
			}
		}
		if(tab.getText().equals("Convers")){
			Log.i("----------------------------------------------","----------------------------------##mjpmpompom");
			
		}
		if(tab.getText().equals("Tools")){
			Log.i("``````````````````````````````````````````````","``````````````````````````````````mjpmpompom");
		}
		
		
		//prueba para pasar lista de contactos
		//do{
		//fragment.setNicks(mService.getListnick());
		//fragment.setTelefonos(mService.getListtel());
		//}while(fragment.getTelefonos()==null|| fragment.getNicks()==null);
		//fin prueba
		
		
		
		getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		//if(mService.getListaTelefonosContactos()!=null)
		//	fragment.setfondo(mService.getListaTelefonosContactos());
	}
	
	
	public void cambiarfragment(String[] contactos){
		Log.i("ENTRO EN CF","CAMBIARFRAGMENT()");
		fragment.setfondo(contactos);
		
		//getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public void update(){
		
	}
	
	  public void showException(Throwable t) {
		    AlertDialog.Builder builder=new AlertDialog.Builder(this);

		    builder
		      .setTitle("Exception!")
		      .setMessage(t.toString())
		      .setPositiveButton("OK", null)
		      .show();
	  }
	  
	  /** Defines callbacks for service binding, passed to bindService() */
	    private ServiceConnection mConnection = new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	        	Log.i("Service Connection", "Estamos en onServiceConnected");
	            SeTIChatServiceBinder binder = (SeTIChatServiceBinder) service;
	            mService = binder.getService();
	            
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	           
	        }
	    };



	public SeTIChatService getService() {
		// TODO Auto-generated method stub
		return mService;
	}
	
	//SeTIChatServiceDelegate Methods
	
	public void showNotification(String message){
		Context context = getApplicationContext();
		CharSequence text = message;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	

}
