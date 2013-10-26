package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.LoginActivity;
import es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Mensaje {
	private SharedPreferences dhj;
	
	String mensaje;
	String cabecera;
	String idSource;
	String idDestination;
	String idMessage;
	int type;
	String encrypted;
	String signed;
	String cuerpo;
	String token;
	/*String textoMensaje;
	public String getTextoMensaje() {
		return textoMensaje;
	}

	public void setTextoMensaje(String textoMensaje) {
		this.textoMensaje = textoMensaje;
	}*/

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Mensaje(String mensaje) {
		
		super();
		Log.i("CREACION","MENSAJE");
		this.mensaje = mensaje;
		this.cabecera = mensaje.substring(0, mensaje.indexOf("</header>")+9);
		
		this.idSource=cabecera.substring(cabecera.indexOf("<idSource>")+10, cabecera.indexOf("</idSource>"));
		this.idDestination=cabecera.substring(cabecera.indexOf("<idDestination>")+15, cabecera.indexOf("</idDestination>"));
		this.idMessage=cabecera.substring(cabecera.indexOf("<idMessage>")+11, cabecera.indexOf("</idMessage>"));
		this.type=Integer.parseInt(cabecera.substring(cabecera.indexOf("<type>")+6, cabecera.indexOf("</type>")));
		this.encrypted=cabecera.substring(cabecera.indexOf("<encrypted>")+11, cabecera.indexOf("</encrypted>"));
		this.signed=cabecera.substring(cabecera.indexOf("<signed>")+8, cabecera.indexOf("</signed>"));
		this.cuerpo = mensaje.substring(mensaje.indexOf("<content>"),mensaje.length()-1);
		//this.textoMensaje = cuerpo.substring(cuerpo.indexOf("<chatMessage>")+1,cuerpo.indexOf("</chatMessage>") );
		
	}

	public Mensaje() {
		super();
	}
	

	
	 public String creaCabecera( String token, String iddest, int tipo, boolean enc, boolean sig){//shared preferences   sqllite
		 //Recordar rellenar los campos correctos
		 Log.e("TOKEN DE ENVIO",token);
		 String aleatorio="";
		 
		 String idDestination = iddest;
		 int type = tipo;
		 Boolean encrypted = enc;
		 Boolean signed = sig;
		 byte [] idmessage = new byte[17];
		 for(int i=0;i<17;i++){
			 idmessage[i] = (byte) (Math.random()*10);
			 //Log.i("aleatorio",idmessage[i]+"");
			 aleatorio+=idmessage[i];
		 }
		 idMessage =  aleatorio;
		 Log.i("NUESTRO", "Numeroaleatoriogenerado= "+ aleatorio + " : " + Arrays.toString(idmessage));
		 
		 String cabecera = "<header><idSource>"+token+"</idSource><idDestination>" + idDestination + "</idDestination><idMessage>" ;
				 for(int i=0;i<17;i++){
					 cabecera+=idmessage[i];
				 }
				 cabecera += "</idMessage>"+"<type>"+type+"</type><encrypted>"+encrypted+"</encrypted><signed>"+signed+"</signed></header>";
		 return cabecera;
	 }

	 public String creaContenido( int cod, String [] parametros){
		 //Recordar rellenar los campos correctos
		 int codContenido []= {1,2,3,4,5,6,7,8,9,10};
		 String stringCodContenido [] = {"","signup","contactrequest","contactresponse","chatmessage","connection", "response", "revocation","keyrequest", "download","upload"};
		 String codigo = stringCodContenido[cod];
		 String contenido = "<content>";
		 
		 if(cod==1){		//signup  			//nick                          mobile					signup
			 contenido +="<"+codigo+"><nick>"+parametros[0]+"</nick><mobile>"+parametros[1]+"</mobile></"+codigo+">";
		 }
		 else if(cod==2){
			 
			 contenido +="<mobileList>";
			 for(int i=0; i<parametros.length; i++)
				 contenido+="<mobile>"+parametros[i]+"</mobile>";
			 contenido +="</mobileList>";
		 }
		 else if (cod==3){//LEER CONTACTOS!!! RECIBIR, NO ENVIAR
			 contenido +="<contactcList>";
			 for(int i=0; i<parametros.length; i+=2){
				 contenido+="<mobile>"+parametros[i]+"</mobile>";
				 
			 }
			 contenido +="</contactcList>";
		 }
		 else if(cod == 4){
			
			 //CARGO TODOS LOS CONTRACTYOS CON CONTACT REQUEST Y SOLICITO CLAVE CON DOWNLOAD (SUBO LA MIA CON UPLOAD) Y LA ALMACENO EN BBDD Y COMPRUEBO Y LA METO EN clavepublicadestinatario
	
			 contenido+="<chatMessage>"+parametros[0]+"</chatMessage>";
		 }
		 else if(cod == 5)
		 {
			 contenido+="<connection></connection>";
		 }
		 else if(cod == 6){//Respuesta del servidor, a lectura!!!!
			 contenido += "<response><responseCode>"+parametros[0]+"";//faltan cosas
		 }
		 else if(cod == 7){
			 contenido += "<revokedMobile>"+parametros[0]+"</revokedMobile>";
			 
		 }
		 else if(cod == 8){
			 contenido+="<keyrequest><type>"+"public" + "</type><mobile>"+parametros[0]+"</mobile></keyrequest>";
		 }
		 else if (cod == 9){
			 //LECTURA para ver clave mod 2
		 }
		 else if(cod == 10){
			 contenido+="<upload><key>"+ parametros[0] + "</key><type>public</type></upload>";					
		 }
		 contenido += "</content>";
		 return contenido;
	 }
	
/*
 * //////////////////////////////////////////////// TRATAMIENTO DE respuestas /////// /////////////////////////////////////////////////////////////////////////
 */
	protected String parsearMensaje(){

		//METER TODAS LAS SALIDAS DE LOS CASES EN STRING RESULTADO
		String resultado="";
		String[] nicks,telefonos;

		
		
		Log.i("MOSTRAR CABECERA","idsource: "+idSource+" idDestination: "+idDestination+" idMessage:" + idMessage+ "type:"+ type+" encrypt:"+ encrypted+" signed: "+signed);
		//ArrayList<String> camposCabecera = new ArrayList<String>();
		Log.i("MOSTRAR CUERPO",cuerpo);
		
		switch(type){
		case 1:			
			break;
		case 2:
			break;
		case 3://ContactResponse
			resultado+="2";
            ArrayList<String> contactos = new ArrayList<String>();
                    String cuerpoAux=cuerpo;
                    String patronApertura = "<contact>";
                    String patronCierre = "</contact>";
               int cantContactos = 0;
		          while (cuerpoAux.indexOf(patronApertura) > -1)
		          {
		                  contactos.add(cuerpoAux.substring(cuerpoAux.indexOf(patronApertura)+patronApertura.length(),cuerpoAux.indexOf(patronCierre)));
		                  cuerpoAux = cuerpoAux.substring(cuerpoAux.indexOf(patronCierre)+patronCierre.length(),cuerpoAux.length());
		                  cantContactos++;                           
		          }
		          int i=0;
		
		          
		          int j=0;
		          
		          cuerpoAux=cuerpo;
		          String patronApMob="<mobile>";
		          String patronCerMob="</mobile>";
		          String patronApnick="<nick>";
		          String patronCernick="</nick>";
		          nicks=new String[cantContactos];
		          telefonos=new String[cantContactos];
		              String aux;
		          while(j<cantContactos){//saco los arrays
		                  aux= contactos.get(j);
		                  nicks[j]=aux.substring(aux.indexOf(patronApMob)+8, aux.indexOf(patronCerMob));
		                  telefonos[j]=aux.substring(aux.indexOf(patronApnick)+6, aux.indexOf(patronCernick));
		                  Log.i("CONTACTOS","Contacto"+j+"--> "+nicks[j]+telefonos[j]);
		                  //resultado
		                  resultado+=nicks[j]+"-"+telefonos[j]+"/";
		                  j++;
		                 
		          }
		          Log.i("muestro resultado antes de enviar", resultado); 
            
            //
            break;
		case 4:
				resultado+="4";
				String cuerpoAux2=cuerpo;
				 String patronApchatMes="<chatMessage>";
		         String patronCerchatMes="</chatMessage>";
		         resultado+=cuerpoAux2.substring(cuerpoAux2.indexOf(patronApchatMes)+patronApchatMes.length(),cuerpoAux2.indexOf(patronCerchatMes));
			break;
			
		case 5:
			Log.i("MENSAJE REC",mensaje);
			break;
			
		case 6:
			
			Log.i("VEEER",cuerpo.substring(cuerpo.indexOf("<responseCode>")+14, cuerpo.indexOf("</responseCode>")));
			int responseCode=Integer.parseInt(cuerpo.substring(cuerpo.indexOf("<responseCode>")+14, cuerpo.indexOf("</responseCode>")));
			String mensaje=cuerpo.substring(cuerpo.indexOf("<responseMessage>")+17, cuerpo.indexOf("</responseMessage>"));
			switch(responseCode){
				case 200://El mensaje se ha enviado correctamente	
					Log.i("SERVER","El mensaje ha llegado al server OK");
					resultado="5";
					resultado+=mensaje;
					break;			
	/////////////////////////////////////TOKEN//////////////////////////////////////////////////////////		
				case 201://Sing up correcto, almacenamos el token		
					Log.i("SIGN UP","Usuario registrado correctamente! OK");
					resultado+="1";
					token=cuerpo.substring(cuerpo.indexOf("<responseMessage>")+17, cuerpo.indexOf("</responseMessage>"));
					Log.e("token",token+"   "+ idDestination);
					resultado+=token;
					break;
					
				case 202://Usuario conectado correctamente, enviando mensajes pendientes
					Log.i("SERVER","Usuario conectado correctamente, recibiendo mensajes pendientes");
					resultado="3";
					resultado+=mensaje;
					break;		
				
				case 203://clave publica correctamente resgistrada
					Log.i("SERVER","clave publica correctamente registrada");
					resultado="7";
					resultado+=mensaje;
					break;
					
				case 204://clave publica correctamente guardada
					Log.i("SERVER","clave publica correctamente guardada");
					resultado="7";
					resultado+=mensaje;
					break;
				//Errores modulo 1
				case 401://Mensaje no conforme a la estructura del setiCHat		
					resultado+="6";
					Log.e("MENSAJE DE ERROR","Mensaje no conforme a la estructura del setiCHat");
					break;
				case 405://Tipo de mensajes no soportado	
					resultado+="6";
					Log.e("MENSAJE DE ERROR","Tipo de mensajes no soportado");
					break;
				case 406://El movil del usuario no se corresponde con el registrado en la aplicaci�n
					resultado+="6";
					resultado+="406El movil del usuario no se corresponde con el registrado en la aplicacion/El numero ya esta registrado en la aplicacion";
					Log.e("MENSAJE DE ERROR",resultado);
					break;
				case 407://Usuario no registrado
					resultado+="6";
					Log.e("MENSAJE DE ERROR","Usuario no registrado");
					break;
				case 408: //El usuario de destino del mensaje NO EXISTE.
					resultado+="6";
					Log.e("MENSAJE DE ERROR","Intento de chat con usuario no existente en setiCHAT");
					break;
				case 410: //El usuario de destino del mensaje NO EXISTE.
					resultado+="6";
					Log.e("MENSAJE DE ERROR","Clave de usuario solicitada no existe");
					break;
				case 413://Error desconocido
					resultado+="6";
					Log.e("MENSAJE DE ERROR","ERROR DESCONOCIDO");
					break;
				/*Modulo 2
				case 203://Nueva clave guardada, enviando revocaciones a los usuarios
					
					break;
				case 204://Nueva clave guardada
					
					break;
				case 410://Clave publica no encontrada
				
					break;
					case 412://La firma del mensaje no coincide
				
					break;
					
					*/
			}
			break;
			
		case 7:
			resultado+="9";
			String cuerpoAux7=cuerpo;
			 String patronRevo="<revokedMobile>";
	         String patronCerRevo="</revokedMobile>";
	         resultado+=cuerpoAux7.substring(cuerpoAux7.indexOf(patronRevo)+patronRevo.length(),cuerpoAux7.indexOf(patronCerRevo));
			break;
			
		case 8: //descarga de claves
			resultado+="8";
			String cuerpoAux3=cuerpo;
			String patronApchatMes2="<key>";
	        String patronCerchatMes2="</key>";
	        
	        String patronApMob2="<mobile>";
	        String patronCerMob2="</mobile>";
	        resultado+=cuerpoAux3.substring(cuerpoAux3.indexOf(patronApMob2)+patronApMob2.length(),cuerpoAux3.indexOf(patronCerMob2));
	        resultado+="key";
	        resultado+=cuerpoAux3.substring(cuerpoAux3.indexOf(patronApchatMes2)+patronApchatMes2.length(),cuerpoAux3.indexOf(patronCerchatMes2));
	        
	        
		break;
		
	}
		/*try{
			

		}
		catch(Exception e){
			Log.i("ERROR!","Hay un problema al parsear el mensaje recibido!");
		}*/
	
		return resultado;
	}

	 
 
	
}
