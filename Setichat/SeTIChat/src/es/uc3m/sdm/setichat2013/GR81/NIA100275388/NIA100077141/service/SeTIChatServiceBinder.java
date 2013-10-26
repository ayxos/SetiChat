package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service;





import android.content.Context;
import android.os.Binder;
import android.util.Log;



public class SeTIChatServiceBinder extends Binder {
	
	static SeTIChatService service;
	
	
	 void onCreate(Context ctxt) {
		 Log.i("Service Binder", "Service Binder onCreate()");
		  service = (SeTIChatService)ctxt;
		  
	 }

	 void onDestroy() {
		service = null;
	  }
	 
	 public static SeTIChatService getService() {
         // Return this instance of LocalService so clients can call public methods
         return service;
     }
	
	

}