package com.example.proyectofinalmovil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.view.KeyEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Finish extends Activity implements OnMapLongClickListener,
		OnMapClickListener, OnMyLocationChangeListener {
	private GoogleMap googleMap;
	SQLiteDatabase db;
	protected String TAG = Finish.class.getSimpleName();
	protected JSONArray mData;

	String lat2;
	String lon2;
	boolean on = false;
	static LocationManager lm;
	static MiLocationListener mlistener;

	public static String lat, lon, loc;
	public View prompt;
	private TextView user;
	private EditText comentariof;
	Editable com;
	private EditText nivelG;

	public ImageView btn_reportar;
	public ImageView btn_logout;
	public ImageView btn_reportes_anu;
	public ImageView btn_reportes_men;
	public ImageView btn_reportes_sem;

	String lat3;
	String lon3;

	TextView tvhora;
	int hora = 0, minuto = 0, segundo = 0;

	Thread iniReloj = null;
	Runnable r;
	boolean isUpdate = false;
	String sec, min, hor;
	String curTime;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			setContentView(R.layout.fragment_mapa);

			user = (TextView) findViewById(R.id.user);
			comentariof = (EditText) findViewById(R.id.comentariof);
			btn_reportar = (ImageView) findViewById(R.id.btn_reportar);
			btn_logout = (ImageView) findViewById(R.id.btn_logout);

			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mlistener = new MiLocationListener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5,
					mlistener);

			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			googleMap.setMyLocationEnabled(true);

			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.getUiSettings().setAllGesturesEnabled(true);
			googleMap.setTrafficEnabled(true);
			googleMap.setBuildingsEnabled(false);

			tvhora = (TextView) findViewById(R.id.tvhora);
			r = new RefreshClock();
			iniReloj = new Thread(r);
			iniReloj.start();

		}

		Bundle bundle = getIntent().getExtras();

		user.setText("" + bundle.getString("NOMBRE"));

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();

	}

	// RELOJ
	private void initClock() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {

					if (isUpdate) {
						settingNewClock();
					} else {
						updateTime();
					}

					curTime = hor + hora + min + minuto + sec + segundo;
					tvhora.setText(curTime);

				} catch (Exception e) {
				}
			}
		});
	}

	class RefreshClock implements Runnable {
		// @Override
		@SuppressWarnings("unused")
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					initClock();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}
	}

	private void updateTime() {

		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.HOUR_OF_DAY) > 12) {
			hora = c.get(Calendar.HOUR_OF_DAY) - 12;
		} else {
			hora = c.get(Calendar.HOUR_OF_DAY);
		}

		minuto = c.get(Calendar.MINUTE);
		segundo = c.get(Calendar.SECOND);
		setZeroClock();

	}

	private void setZeroClock() {
		if (hora >= 0 & hora <= 9) {
			hor = "0";
		} else {
			hor = "";
		}

		if (minuto >= 0 & minuto <= 9) {
			min = ":0";
		} else {
			min = ":";
		}

		if (segundo >= 0 & segundo <= 9) {
			sec = ":0";

		} else {
			sec = ":";
		}
	}

	private void settingNewClock() {
		segundo += 1;

		setZeroClock();

		if (segundo >= 0 & segundo <= 59) {

		} else {
			segundo = 0;
			minuto += 1;
		}
		if (minuto >= 0 & minuto <= 59) {

		} else {
			minuto = 0;
			hora += 1;
		}
		if (hora >= 0 & hora <= 24) {

		} else {
			hora = 0;
		}

	}

	// REPORTAR Y VER INCIDENCIAS
	@SuppressLint("SimpleDateFormat")
	public void Reportar_Incidencia(View view) {
		comentariof = (EditText) findViewById(R.id.comentariof);
		nivelG = (EditText) findViewById(R.id.nivelG);
		btn_reportar = (ImageView) findViewById(R.id.btn_reportar);
		tvhora = (TextView) findViewById(R.id.tvhora);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());

		Toast.makeText(
				this,
				"" + lat2 + " " + lon2 + " " + comentariof.getText().toString()
						+ " " + null + " " + user.getText() + " "
						+ formattedDate + " " + tvhora.getText() + " "
						+ nivelG.getText(), Toast.LENGTH_LONG).show();

	}

	public void repsem(View view) {
		obtenerDatosJSON();
		googleMap.clear();
		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_PRIVATE, null);

		Cursor c = db.rawQuery("SELECT * FROM incidencias ", null);

		if (c.moveToFirst()) {
			do {
				// Toast.makeText(
				// this,
				// "" + c.getString(1) + " " + c.getString(2) + " "
				// + c.getString(3) + " " + c.getString(4) + " "
				// + c.getString(5) + " " + c.getString(6) + " "
				// + c.getString(7) + " " + c.getString(8),
				// Toast.LENGTH_LONG).show();

				final LatLng loc = new LatLng(Double.parseDouble(c.getString(1)
						.toString()), Double.parseDouble(c.getString(2)
						.toString()));

				LatLng pos = new LatLng(10.967735, -74.800468);

				CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(pos, 11);

				googleMap.moveCamera(cam);

				googleMap.addMarker(new MarkerOptions().position(loc)
						.title(c.getString(3)).alpha(0.7f));// .showInfoWindow();

				CircleOptions opcionesCirculo = new CircleOptions().center(
						new LatLng(Double
								.parseDouble(c.getString(1).toString()), Double
								.parseDouble(c.getString(2).toString())))
						.radius(50);

				Circle circulo = googleMap.addCircle(opcionesCirculo);

				circulo.setStrokeColor(Color.RED);
				circulo.setStrokeWidth(5f);
			} while (c.moveToNext());
		} else {
			Toast.makeText(this, "No exite informacion de incidencias",
					Toast.LENGTH_LONG).show();
		}
		
		c.close();
		db.execSQL("DELETE FROM incidencias;");
		db.close();

	}

	public void repmen(View view) {
		obtenerDatosJSON();
		googleMap.clear();
		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_PRIVATE, null);

		Cursor c = db.rawQuery("SELECT * FROM incidencias ", null);

		if (c.moveToFirst()) {
			do {
				// Toast.makeText(
				// this,
				// "" + c.getString(1) + " " + c.getString(2) + " "
				// + c.getString(3) + " " + c.getString(4) + " "
				// + c.getString(5) + " " + c.getString(6) + " "
				// + c.getString(7) + " " + c.getString(8),
				// Toast.LENGTH_LONG).show();

				final LatLng loc = new LatLng(Double.parseDouble(c.getString(1)
						.toString()), Double.parseDouble(c.getString(2)
						.toString()));

				LatLng pos = new LatLng(10.967735, -74.800468);

				CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(pos, 11);

				googleMap.moveCamera(cam);

				googleMap.addMarker(new MarkerOptions().position(loc)
						.title(c.getString(3)).alpha(0.7f));// .showInfoWindow();

				CircleOptions opcionesCirculo = new CircleOptions().center(
						new LatLng(Double
								.parseDouble(c.getString(1).toString()), Double
								.parseDouble(c.getString(2).toString())))
						.radius(50);

				Circle circulo = googleMap.addCircle(opcionesCirculo);

				circulo.setStrokeColor(Color.RED);
				circulo.setStrokeWidth(5f);
			} while (c.moveToNext());
		} else {
			Toast.makeText(this, "No exite informacion de incidencias",
					Toast.LENGTH_LONG).show();
		}
		

		c.close();
		db.execSQL("DELETE FROM incidencias;");
		db.close();
	}

	public void repanu(View view) {
		googleMap.clear();
		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_PRIVATE, null);

		Cursor c = db.rawQuery("SELECT * FROM incidencias ", null);

		if (c.moveToFirst()) {
			do {
				// Toast.makeText(
				// this,
				// "" + c.getString(1) + " " + c.getString(2) + " "
				// + c.getString(3) + " " + c.getString(4) + " "
				// + c.getString(5) + " " + c.getString(6) + " "
				// + c.getString(7) + " " + c.getString(8),
				// Toast.LENGTH_LONG).show();

				final LatLng loc = new LatLng(Double.parseDouble(c.getString(1)
						.toString()), Double.parseDouble(c.getString(2)
						.toString()));

				LatLng pos = new LatLng(10.967735, -74.800468);

				CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(pos, 11);

				googleMap.moveCamera(cam);

				googleMap.addMarker(new MarkerOptions().position(loc)
						.title(c.getString(3)).alpha(0.7f));// .showInfoWindow();

				CircleOptions opcionesCirculo = new CircleOptions().center(
						new LatLng(Double
								.parseDouble(c.getString(1).toString()), Double
								.parseDouble(c.getString(2).toString())))
						.radius(50);

				Circle circulo = googleMap.addCircle(opcionesCirculo);

				circulo.setStrokeColor(Color.RED);
				circulo.setStrokeWidth(5f);
			} while (c.moveToNext());
		} else {
			Toast.makeText(this, "No exite informacion de incidencias",
					Toast.LENGTH_LONG).show();
		}
		

		c.close();
		db.execSQL("DELETE FROM incidencias;");
		db.close();
	}

	// JSON
	public void obtenerDatosJSON() {

		if (isNetworkAvailable()) {
			GetDataTask getDataTask = new GetDataTask();

			getDataTask.execute();
		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isNetworkAvaible = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isNetworkAvaible = true;
			Toast.makeText(this, "La red está disponible ", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "La red no está disponible ",
					Toast.LENGTH_LONG).show();
		}
		return isNetworkAvaible;
	}

	public class GetDataTask extends AsyncTask<Object, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Object... params) {
			btn_reportes_anu = (ImageView)findViewById(R.id.btn_reportes_anu);
			btn_reportes_men = (ImageView)findViewById(R.id.btn_reportes_men);
			btn_reportes_sem = (ImageView)findViewById(R.id.btn_reportes_sem);
			int responseCode = -1;
			JSONArray jsonResponse = null;

			if (btn_reportes_sem.isPressed()) {
				try {

					URL blogFeedUsr = new URL(
							"https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias.json");

					HttpURLConnection connection = (HttpURLConnection) blogFeedUsr
							.openConnection();
					connection.connect();

					responseCode = connection.getResponseCode();

					if (responseCode == HttpURLConnection.HTTP_OK) {
						String jsonsem = "https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias.json";

						try {
							jsonResponse = new JSONArray(readUrl(jsonsem));
							Log.v(TAG, jsonResponse.toString());

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						Log.i(TAG,
								"Response code unsuccesful "
										+ String.valueOf(responseCode));
					}

				} catch (MalformedURLException e) {
					Log.e(TAG, "Exception1", e);
				} catch (IOException e) {
					Log.e(TAG, "Exception2", e);
				} catch (Exception e) {
					Log.e(TAG, "Exception3", e);
				}
			} else {
				if (btn_reportes_men.isPressed()) {
					try {

						URL blogFeedUsr = new URL(
								"https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias_mes.json?utf8=%E2%9C%93&date%5Byear%5D=2014&date%5Bmonth%5D=11&commit=Generar+lista");

						HttpURLConnection connection = (HttpURLConnection) blogFeedUsr
								.openConnection();
						connection.connect();

						responseCode = connection.getResponseCode();

						if (responseCode == HttpURLConnection.HTTP_OK) {
							String jsonmes = "https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias_mes.json?utf8=%E2%9C%93&date%5Byear%5D=2014&date%5Bmonth%5D=11&commit=Generar+lista";

							try {
								jsonResponse = new JSONArray(readUrl(jsonmes));
								Log.v(TAG, jsonResponse.toString());

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							Log.i(TAG,
									"Response code unsuccesful "
											+ String.valueOf(responseCode));
						}

					} catch (MalformedURLException e) {
						Log.e(TAG, "Exception1", e);
					} catch (IOException e) {
						Log.e(TAG, "Exception2", e);
					} catch (Exception e) {
						Log.e(TAG, "Exception3", e);
					}
				}else{
					if(btn_reportes_anu.isPressed()){
						try {

							URL blogFeedUsr = new URL(
									"https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias_anho.json?utf8=%E2%9C%93&date%5Byear%5D=2014&commit=Generar+lista");

							HttpURLConnection connection = (HttpURLConnection) blogFeedUsr
									.openConnection();
							connection.connect();

							responseCode = connection.getResponseCode();

							if (responseCode == HttpURLConnection.HTTP_OK) {
								String jsonaño = "https://accidentestransitobarranquilla-leandroniebles.c9.io/incidencias_anho.json?utf8=%E2%9C%93&date%5Byear%5D=2014&commit=Generar+lista";

								try {
									jsonResponse = new JSONArray(readUrl(jsonaño));
									Log.v(TAG, jsonResponse.toString());

								} catch (JSONException e) {
									e.printStackTrace();
								}

							} else {
								Log.i(TAG,
										"Response code unsuccesful "
												+ String.valueOf(responseCode));
							}

						} catch (MalformedURLException e) {
							Log.e(TAG, "Exception1", e);
						} catch (IOException e) {
							Log.e(TAG, "Exception2", e);
						} catch (Exception e) {
							Log.e(TAG, "Exception3", e);
						}
					}
				}
			}
			
			return jsonResponse;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			mData = result;
			handleBlogResponse();
		}

	}

	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public void handleBlogResponse() {
		if (mData == null) {

			// updateDisplayForError();
			Toast.makeText(this, "No existe información", Toast.LENGTH_LONG)
					.show();
		} else {
			// Introducir información JSON en la BD
			try {

				JSONArray jsonarray = mData;

				for (int i = 0; i < jsonarray.length(); i++) {

					JSONObject jsonobj = jsonarray.getJSONObject(i);

					String id = jsonobj.getString("id");
					String latitud = jsonobj.getString("latitud");
					String longitud = jsonobj.getString("longitud");
					String comentario = jsonobj.getString("comentario");
					String direccion = jsonobj.getString("ubicacion");
					String fecha = jsonobj.getString("fecha");
					String hora = jsonobj.getString("hora");
					String nivel = jsonobj.getString("gravedad");

					db = openOrCreateDatabase("CrasAlertDB",
							Context.MODE_PRIVATE, null);
					db.execSQL("INSERT INTO incidencias VALUES(" + id + ",'"
							+ latitud + "','" + longitud + "','" + comentario
							+ "','" + direccion + "','"
							+ user.getText().toString() + "','" + fecha + "','"
							+ hora + "'," + Integer.parseInt(nivel) + ");");

				}

				db.close();

			} catch (JSONException e) {
				Log.e(TAG, "Exception caught!", e);
			}
		}
	}

	// GENERAR RUTA
	public void genruta(View view) {

		connectAsyncTask _connectAsyncTask = new connectAsyncTask();
		_connectAsyncTask.execute();

	}

	private class connectAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(getApplicationContext());
			progressDialog.setMessage("analizando ruta, espere un momento");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			fetchData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (doc != null) {
				NodeList _nodelist = doc.getElementsByTagName("status");
				Node node1 = _nodelist.item(0);
				String _status1 = node1.getChildNodes().item(0).getNodeValue();
				if (_status1.equalsIgnoreCase("OK")) {
					NodeList _nodelist_path = doc
							.getElementsByTagName("overview_polyline");
					Node node_path = _nodelist_path.item(0);
					Element _status_path = (Element) node_path;
					NodeList _nodelist_destination_path = _status_path
							.getElementsByTagName("points");
					Node _nodelist_dest = _nodelist_destination_path.item(0);
					String _path = _nodelist_dest.getChildNodes().item(0)
							.getNodeValue();
					List<LatLng> directionPoint = decodePoly(_path);

					PolylineOptions rectLine = new PolylineOptions().width(10)
							.color(Color.RED);
					for (int i = 0; i < directionPoint.size(); i++) {
						rectLine.add(directionPoint.get(i));
					}
					// Adding route on the map
					googleMap.addPolyline(rectLine);
				}
			}
			progressDialog.dismiss();
		}
	}

	Document doc = null;

	private void fetchData() {
		StringBuilder urlString = new StringBuilder();
		urlString
				.append("http://maps.google.com/maps/api/directions/xml?origin=");
		urlString.append(lat);
		urlString.append(",");
		urlString.append(lon);
		urlString.append("&destination=");// to
		urlString.append(lat2);
		urlString.append(",");
		urlString.append(lon2);
		urlString.append("&sensor=true&mode=driving");
		Log.d("url", "::" + urlString.toString());
		HttpURLConnection urlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = (Document) db.parse(urlConnection.getInputStream());// Util.XMLfromString(response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

	// LOGOUT
	public void exit(View view) {
		btn_logout = (ImageView) findViewById(R.id.btn_logout);

		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);

	}

	// POSICION ACTUAL
	public class MiLocationListener implements LocationListener {

		public MiLocationListener() {
			// TODO Auto-generated constructor stub
		}

		public synchronized void onLocationChanged(Location location) {

			DecimalFormat f = new DecimalFormat("###.#####");

			lat = f.format(location.getLatitude()).replace(",", ".");
			lon = f.format(location.getLongitude()).replace(",", ".");
			LatLng pos = new LatLng(Double.parseDouble(lat),
					Double.parseDouble(lon));
			// CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(pos,25);
			//
			// googleMap.moveCamera(cam);

		}

		public synchronized void onProviderDisabled(String provider) {

		}

		public synchronized void onProviderEnabled(String provider) {

		}

		public synchronized void onStatusChanged(String provider, int status,
				Bundle extras) {

		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return on;
		// Inflate the menu; this adds items to the action bar if it is present.

	}

	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));

	}

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub

	}

	public void onMapLongClick(final LatLng point) {

		btn_reportar = (ImageView) findViewById(R.id.btn_reportar);
		comentariof = (EditText) findViewById(R.id.comentariof);
		nivelG = (EditText) findViewById(R.id.nivelG);

		db = openOrCreateDatabase("CrasAlertDB", Context.MODE_APPEND, null);

		lat2 = String.valueOf(point.latitude);
		lon2 = String.valueOf(point.longitude);

		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.fragment_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText comentario = (EditText) promptsView
				.findViewById(R.id.comentario);

		final EditText nivel = (EditText) promptsView.findViewById(R.id.nivel);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Comentar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// get user input and set it to result
								// edit text
								comentariof.setText(comentario.getText());
								nivelG.setText(nivel.getText());
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		final LatLng loc = new LatLng(Double.parseDouble(lat2.toString()),
				Double.parseDouble(lon2.toString()));

		googleMap.addMarker(new MarkerOptions().position(loc)
				.title(loc.toString()).alpha(0.7f));// .showInfoWindow();

		CircleOptions opcionesCirculo = new CircleOptions().center(
				new LatLng(Double.parseDouble(lat2.toString()), Double
						.parseDouble(lon2.toString()))).radius(50);

		Circle circulo = googleMap.addCircle(opcionesCirculo);

		circulo.setStrokeColor(Color.RED);
		circulo.setStrokeWidth(5f);

	}

}
