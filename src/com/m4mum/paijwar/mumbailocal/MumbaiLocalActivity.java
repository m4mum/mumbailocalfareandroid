/***
 * @author Pradeep Kumar Paijwar
 * @email  paijwar@gmail.com
 */

package com.m4mum.paijwar.mumbailocal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MumbaiLocalActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	final static String STATION_FILE = "stations.txt";
	final static String FARE_FILE = "fare.txt";
	final static String defaultTitle = "Choose start staion";
	ArrayList<String> stations = new ArrayList<String>();
	int[][] fareTable = new int[35][35];

	private ArrayAdapter<String> listAdapter = null;
	private ListView listView = null;
	private TextView fareView = null;

	ArrayList<String> fare = new ArrayList<String>();

	public static Boolean firstLaunch = true;
	SharedPreferences mPrefSettings = null;
	static final String PREFNAME = "PMUMBAILOCAL";
	static String registrationMobileNumber = null;
	private EditText et = null;
	private ArrayList<String> array_sort = new ArrayList<String>();
	int textlength = 0;
	Boolean srcSelected = false;
	Boolean dstSelected = false;
	String startStation = null;
	String stopStation = null;

	void loadFare() {
		BufferedReader br;
		String inputLine = "";
		int x = 0;
		int y = 0;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(
					FARE_FILE)));
			while ((inputLine = br.readLine()) != null) {
				String[] values = inputLine.split("\t");

				for (String value : values) {
					int c = 0;
					try {
						c = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					fareTable[x][y] = c;
					y++;
				}
				x++;
				y = 0;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void loadStations() {
		stations.clear();
		BufferedReader br;
		String inputLine = "";
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open(
					STATION_FILE)));
			while ((inputLine = br.readLine()) != null) {
				stations.add(inputLine);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void loadUI() {
		fareView = (TextView) findViewById(R.id.fare);
		fareView.setEnabled(false);

		listView = (ListView) findViewById(R.id.listView);
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, stations);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		et = (EditText) findViewById(R.id.searchBox);
		et.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlength = et.getText().length();
				array_sort.clear();
				for (int i = 0; i < stations.size(); i++) {
					if (textlength <= stations.get(i).length()) {
						if (et.getText()
								.toString()
								.equalsIgnoreCase(
										(String) stations.get(i).subSequence(0,
												textlength))) {
							array_sort.add(stations.get(i));
						}
					}
				}
				listView.setAdapter(new ArrayAdapter<String>(
						MumbaiLocalActivity.this,
						android.R.layout.simple_list_item_1, array_sort));
				listView.setOnItemClickListener(MumbaiLocalActivity.this);
			}
		});

		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (event != null
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					return true;
				}
				return false;
			}
		});
		Button clear = (Button) findViewById(R.id.btnClear);
		clear.setOnClickListener(this);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_listview);
		registrationMobileNumber = getResources().getString(
				R.string.registration_number);
		mPrefSettings = getSharedPreferences(PREFNAME, 0);
		int registered = mPrefSettings.getInt("registered", 0);

		for (int i = 0; i < 25; i++) {
			fare.add(i, "" + i);
		}
		loadStations();
		loadFare();

		if (registered == 0) {
			firstLaunch = true;
			firstLaunch(registrationMobileNumber);
		} else {
			firstLaunch = false;
			loadUI();
		}

		if (!srcSelected && !dstSelected) {
			setTitle(defaultTitle);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_about, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.about) {
			showAboutScreen();
		}
		return true;
	}

	public void showAboutScreen() {
		Intent intent = new Intent();
		intent.setClassName("com.paijwar.mumbailocal",
				"com.paijwar.mumbailocal.AboutActivity");
		try {
			startActivity(intent);// (intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMessage(CharSequence str,
			DialogInterface.OnClickListener listener) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Message");
		alert.setMessage(str);
		alert.setButton("OK", listener);
		// alert.setButton("Cancel", listener);
		alert.setIcon(R.drawable.icon);
		alert.show();
	}

	private void firstLaunch(String number) {
		showMessage(
				"For registration this application will send an SMS, This will happen only one time",
				null);
		sendSMS(number,
				getResources().getString(R.string.application_registration_msg));
	}

	private void sendSMS(String phoneNumber, String message) {
		if (firstLaunch == true) {
			if (!phoneNumber.equalsIgnoreCase(registrationMobileNumber)) {
				Toast.makeText(this, "Application is not registered",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();

					if (firstLaunch == true) {
						firstLaunch = false;
						mPrefSettings = getSharedPreferences(PREFNAME, 0);
						SharedPreferences.Editor prefEdit = mPrefSettings.edit();
						prefEdit.putInt("registered", 1);
						prefEdit.commit();

						// showMessage("Please restart application to use it",
						// null);
						loadUI();
					}
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		String title = defaultTitle;
		if (!srcSelected && !dstSelected) {
			startStation = (String) arg0.getAdapter().getItem(arg2);
			title = "From " + startStation + "- choose stop station";
			srcSelected = true;
			et.setText("");
		} else if (srcSelected && !dstSelected) {
			dstSelected = true;
			stopStation = (String) arg0.getAdapter().getItem(arg2);
			title = "From " + startStation + " to " + stopStation;
			
			int srcIndex = 0;
			int dstIndex = 0;
			int countStart = 0;
			int countStop = 0;
			for(String station: stations) {
				if(station.equalsIgnoreCase(startStation)) {
					srcIndex = countStart;
					break;
				}
				countStart++;
			}
			for(String station: stations) {
				if(station.equalsIgnoreCase(stopStation)) {
					dstIndex = countStop;
					break;
				}
				countStop++;
			}
			int fare = fareTable[srcIndex][dstIndex];
			fareView.setText("INR "+fare+"/-");
			title += " INR "+fare+" /-";
			TextView tv = (TextView)findViewById(R.id.fare);
			tv.setVisibility(View.VISIBLE);
		}
		else {
			srcSelected = dstSelected = false;
			if (!srcSelected && !dstSelected) {
				et.setText("");
				TextView tv = (TextView)findViewById(R.id.fare);
				tv.setVisibility(View.GONE);
			}
		}
		setTitle(title);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnClear) {
			srcSelected = dstSelected = false;
			if (!srcSelected && !dstSelected) {
				setTitle(defaultTitle);
				et.setText("");
				TextView tv = (TextView)findViewById(R.id.fare);
				tv.setVisibility(View.GONE);
			}
		}
	}

}