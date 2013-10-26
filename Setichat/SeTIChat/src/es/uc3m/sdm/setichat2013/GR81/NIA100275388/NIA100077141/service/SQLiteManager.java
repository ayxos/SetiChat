
package es.uc3m.sdm.setichat2013.GR81.NIA100275388.NIA100077141.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class SQLiteManager extends SQLiteOpenHelper {
 
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE Usuarios (telefono TEXT, nick TEXT, token TEXT, kpublica TEXT, kprivada TEXT, password TEXT)";
    
    //Nose si esto esta bien
    String sqlCreate1 = "CREATE TABLE Contactos (telefono TEXT, telfnick TEXT, kpublica TEXT)";
    
    //Nose si esto esta bien
    String sqlCreate2 = "CREATE TABLE Mensajes (Origen TEXT, Destino TEXT, mensaje TEXT, fecha TEXT)";
 
    public SQLiteManager(Context contexto, String nombre,
                               CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creaci�n de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate1);
        db.execSQL(sqlCreate2);
        Log.i("BBDDSQLite","creacion de tablas");
    }
 
    
    //no se usa.!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aqu� utilizamos directamente la opci�n de
        //      eliminar la tabla anterior y crearla de nuevo vac�a con el nuevo formato.
        //      Sin embargo lo normal ser� que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este m�todo deber�a ser m�s elaborado.
 
        //Se elimina la versi�n anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
 
        //Se crea la nueva versi�n de la tabla
        db.execSQL(sqlCreate);
    }
}