package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationMessage extends Activity {
       
        public void onCreate(Bundle savedInstanceState) {
                   super.onCreate(savedInstanceState);
                   
                   TextView txt=new TextView(this);
                   
                   txt.setText("Mensaje de SeTIChat! wiii");
                   setContentView(txt);
                 }


}
