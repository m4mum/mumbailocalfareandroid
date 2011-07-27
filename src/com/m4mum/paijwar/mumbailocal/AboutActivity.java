/***
 * @author Pradeep Kumar Paijwar
 * @email  paijwar@gmail.com
 */

package com.m4mum.paijwar.mumbailocal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	final static String file_name = "about.txt";
	String content = "";
	private void loadText() {
		String inputLine = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getAssets().open(file_name)));
			while ((inputLine = br.readLine()) != null) {
				content += inputLine;
				content += "\n";
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Unable to load content", Toast.LENGTH_LONG);
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		loadText();
		
		TextView tv = (TextView)findViewById(R.id.aboutContent);
		tv.setText(content);
	}
}
