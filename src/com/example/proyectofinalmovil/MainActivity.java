package com.example.proyectofinalmovil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText user;
	private EditText EtUser;
	private EditText EtPass;
	private ImageView TxFacebook;
	private ImageView TxTwitter;
	private ImageView TxGoogle;
	private ImageView btn_login;
	private ImageView btn_registro;
	private String usuario;
	private CheckBox recuerdame;
	boolean logueado = false;

	private static final String TAG_FRAGMENT = "TAG_FRAGMENT";
	protected String TAG = MainActivity.class.getSimpleName();

	
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TxFacebook = (ImageView) findViewById(R.id.TxFacebook);
		user = (EditText) findViewById(R.id.user);
		recuerdame = (CheckBox)findViewById(R.id.recuerdame);
		EtUser = (EditText)findViewById(R.id.EtUser);
		EtPass = (EditText)findViewById(R.id.EtPass);

		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS usuarios(usuario TEXT NOT NULL,contra TEXT NOT NULL);");
		db.execSQL("CREATE TABLE IF NOT EXISTS incidencias(id INTEGER,latitud DOUBLE NOT NULL,"
				+ "longitud DOUBLE NOT NULL,comentario TEXT NOT NULL,direccion TEXT NOT NULL,usuario TEXT NOT NULL,"
				+ "fecha DATE NOT NULL,hora TIME NOT NULL,nivel INTEGER NOT NULL);");

		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_content);
		FragmentTransaction ft = fm.beginTransaction();

		ft.add(R.id.fragment_content, new MainFragment());

		ft.commit();

		setContentView(R.layout.activity_main);

		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
		LoadPreferences();
		
		
	}
	
	
	
	
	private void SavePreferences(String key, String value){
	    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putString(key, value);
	    editor.commit();
	   }
	  
	   private void LoadPreferences(){
		   EtUser = (EditText)findViewById(R.id.EtUser);
			EtPass = (EditText)findViewById(R.id.EtPass);
	    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
	    String strSavedMem1 = sharedPreferences.getString("user", "");
	    String strSavedMem2 = sharedPreferences.getString("pass", "");
	    EtUser.setText(strSavedMem1);
	    EtPass.setText(strSavedMem2);
	   }
    

	public void login(View view) {
		EtUser = (EditText) findViewById(R.id.EtUser);
		EtPass = (EditText) findViewById(R.id.EtPass);
		recuerdame = (CheckBox)findViewById(R.id.recuerdame);
		btn_login = (ImageView) findViewById(R.id.btn_login);
		boolean sal = false;
		logueado = false;
		
		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_PRIVATE, null);

		Cursor d = db.rawQuery("SELECT * FROM usuarios", null);

		// if (d.moveToFirst()) {
		// do {
		// Toast.makeText(this,
		// "" + d.getString(0) + " " + d.getString(1),
		// Toast.LENGTH_LONG).show();
		// } while (d.moveToNext());
		// } else {
		// Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
		// }

		if ((EtUser.getText().toString().length() == 0)
				|| (EtPass.getText().toString().length() == 0)) {

			Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT)
					.show();

		} else {

			if (d.moveToFirst()) {
				do {
					if (sal == false) {
						if (EtUser.getText().toString()
								.equals(d.getString(0).toString())
								&& EtPass.getText().toString()
										.equals(d.getString(1).toString())) {

							Toast.makeText(this, "Ingreso exitoso",
									Toast.LENGTH_LONG).show();

							Intent intent = new Intent(MainActivity.this,
									Finish.class);

							// Creamos la información a pasar entre actividades
							Bundle b = new Bundle();
							b.putString("NOMBRE", EtUser.getText().toString());
							// Añadimos la información al intent
							intent.putExtras(b);

							// Iniciamos la nueva actividad
							startActivity(intent);

							sal = true;
							logueado = true;

							// FragmentManager fm = getFragmentManager();
							// FragmentTransaction ft = fm.beginTransaction();
							// // ft.replace(R.id.fragment_content, new
							// MFragment())
							// // .addToBackStack(TAG_FRAGMENT);
							// ft.commit();

						} else {
							if (EtUser.getText().toString()
									.compareTo(d.getString(0).toString()) != 0
									|| (EtPass
											.getText()
											.toString()
											.compareTo(
													d.getString(1).toString()) != 0)) {

//								Toast.makeText(
//										this,
//										"" + d.getString(0) + " "
//												+ d.getString(1) + " "
//												+ EtUser.getText().toString()
//												+ " "
//												+ EtPass.getText().toString(),
//										Toast.LENGTH_LONG).show();

								if (d.isLast()) {
									
									Toast.makeText(
											this,
											"Su clave o usuario no es correcta",
											Toast.LENGTH_LONG).show();
								}
							}
						}
					}
				} while (d.moveToNext());
			} else {

				Toast.makeText(this, "Usted no esta registrado",
						Toast.LENGTH_LONG).show();
			}
		}

		d.close();
		db.close();
		
		if(recuerdame.isChecked()){
			SavePreferences("user", EtUser.getText().toString());
			SavePreferences("pass", EtPass.getText().toString());
		}

	}

	public void registrarse(View view) {
		EtUser = (EditText) findViewById(R.id.EtUser);
		EtPass = (EditText) findViewById(R.id.EtPass);
		btn_registro = (ImageView) findViewById(R.id.btn_registro);

		db = openOrCreateDatabase("CrasAlertDB", 0, null);

		if ((EtUser.getText().toString().length() == 0)
				|| (EtPass.getText().toString().length() == 0)) {

			Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_LONG)
					.show();
		} else {

			Cursor c = db.rawQuery("SELECT usuario FROM usuarios "
					+ "WHERE usuario = '" + EtUser.getText().toString() + "'",
					null);

			// Toast.makeText(this, "SI HAY ALGO" + c.getString(0),
			// Toast.LENGTH_LONG).show();
			// Toast.makeText(this, "entre", Toast.LENGTH_LONG).show();

			if (c.moveToFirst()) {
				do {

					if (c.getString(0).toString()
							.equals(EtUser.getText().toString())) {
						Toast.makeText(this, "Usted ya esta registrado",
								Toast.LENGTH_LONG).show();

					} else {

						db.execSQL("INSERT INTO usuarios VALUES('"
								+ EtUser.getText() + "','"
								+ EtPass.getText().toString() + "');");

						Toast.makeText(this,
								"Usted ha sido registrado exitosamente",
								Toast.LENGTH_LONG).show();

					}

				} while (c.moveToNext());
			} else {
				db.execSQL("INSERT INTO usuarios VALUES('" + EtUser.getText()
						+ "','" + EtPass.getText().toString() + "');");

				Toast.makeText(this, "Usted ha sido registrado exitosamente",
						Toast.LENGTH_LONG).show();

			}

			c.close();
			db.close();

		}

		EtUser.setText("");
		EtPass.setText("");

	}

	public void loginFacebook(View view) {
		Intent inte = new Intent(this, FaceLogin.class);
		startActivity(inte);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
