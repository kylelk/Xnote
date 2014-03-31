package com.example.xnote;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author kyle
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String baseDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath().toString()
				+ "/xnote";
		File folder = new File(baseDir);

		if (!folder.exists()) {
			folder.mkdirs();
		}


		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null)
		{
			handleSendText(intent); // Handle text being sent
		}

		Button mSaveButton = (Button) findViewById(R.id.save_note);
		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Toast.makeText(getApplicationContext(), "Saved Note", Toast.LENGTH_LONG).show();

				EditText noteTitleFeild = (EditText) findViewById(R.id.note_title);
				String mNoteTitle = noteTitleFeild.getText().toString();

				EditText noteTextFeild = (EditText) findViewById(R.id.note_text);
				String mNoteMessage = noteTextFeild.getText().toString();

				SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
				String mFormatedTimestamp = s.format(new Date());

				try {

					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

					// root elements
					Document doc = docBuilder.newDocument();
					Element rootElement = doc.createElement("note");
					doc.appendChild(rootElement);

					Element timestamp = doc.createElement("timestamp");
					timestamp.appendChild(doc.createTextNode(mFormatedTimestamp));
					rootElement.appendChild(timestamp);

					Element title = doc.createElement("title");
					title.appendChild(doc.createTextNode(mNoteTitle));
					rootElement.appendChild(title);

					Element message = doc.createElement("message");
					message.appendChild(doc.createTextNode(mNoteMessage));
					rootElement.appendChild(message);

					long unixTime = System.currentTimeMillis() / 1000L;
					String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/xnote/";
					String fileNameTimestamp = baseDir+String.valueOf(unixTime)+".xml";

					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(fileNameTimestamp));


					transformer.transform(source, result);

					noteTitleFeild.setText("");
					noteTextFeild.setText("");
					noteTitleFeild.requestFocus();
					//System.out.println("File saved!");

				} catch (ParserConfigurationException pce) {
					pce.printStackTrace();
				} catch (TransformerException tfe) {
					tfe.printStackTrace();
				}

			}
		});

	}
	/**
	 * @param intent
	 */
	public void handleSendText(Intent intent)
	{
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		String subjectText = intent.getStringExtra(Intent.EXTRA_SUBJECT);
		if (sharedText != null)
		{ 
			EditText noteTextFeild = (EditText) findViewById(R.id.note_text);
			noteTextFeild.setText(sharedText);
		} 
	    if (subjectText != null) {
			EditText noteTitleFeild = (EditText) findViewById(R.id.note_title);
			noteTitleFeild.setText(subjectText);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.recent_notes:
            	Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RecentNotes.class);
				startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
