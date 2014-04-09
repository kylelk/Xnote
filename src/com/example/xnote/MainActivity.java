package com.example.xnote;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.security.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import android.util.*;


/**
 * @author kyle
 * 
 */
public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String baseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath().toString()
			+ "/xnote";
		File folder = new File(baseDir);

		if (!folder.exists())
		{
			folder.mkdirs();
		}


		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null)
		{
			if ("text/plain".equals(type))
			{
				handleSendText(intent); // Handle text being sent
			}
		}

		Button mSaveButton = (Button) findViewById(R.id.save_note);
		mSaveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub

					Toast.makeText(getApplicationContext(), "Saved Note", Toast.LENGTH_LONG).show();

					EditText noteTitleFeild = (EditText) findViewById(R.id.note_title);
					String mNoteTitle = noteTitleFeild.getText().toString();

					EditText noteTextFeild = (EditText) findViewById(R.id.note_text);
					String mNoteMessage = noteTextFeild.getText().toString();

					//SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
					//String mFormatedTimestamp = s.format(new Date());

					long unixTime = System.currentTimeMillis() / 1000L;
					String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/xnote/";
					String fileNameTimestamp = baseDir + String.valueOf(unixTime) + ".xml";

					try
					{

						DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
						DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

						// root elements
						Document doc = docBuilder.newDocument();
						Element rootElement = doc.createElement("note");
						doc.appendChild(rootElement);

						Element timestamp = doc.createElement("timestamp");
						timestamp.appendChild(doc.createTextNode(String.valueOf(unixTime)));
						rootElement.appendChild(timestamp);

						Element title = doc.createElement("title");
						title.appendChild(doc.createTextNode(mNoteTitle));
						rootElement.appendChild(title);

						Element message = doc.createElement("message");
						message.appendChild(doc.createTextNode(mNoteMessage));
						rootElement.appendChild(message);

						Element message_length = doc.createElement("message_length");
						message_length.appendChild(doc.createTextNode(String.valueOf(mNoteMessage.length())));
						rootElement.appendChild(message_length);

						Element title_length = doc.createElement("title_length");
						title_length.appendChild(doc.createTextNode(String.valueOf(mNoteTitle.length())));
						rootElement.appendChild(title_length);

						Element message_sha256 = doc.createElement("message_sha256");
						message_sha256.appendChild(doc.createTextNode(sha256(mNoteMessage)));
						rootElement.appendChild(message_sha256);

						Element message_other = doc.createElement("received_intent");
						//message_other.appendChild(doc.createTextNode( sha256(mNoteMessage) ));
						//rootElement.appendChild(message_other);

						Intent intent = getIntent();
						String action = intent.getAction();
						String type = intent.getType();
						if (Intent.ACTION_SEND.equals(action) && type != null)
						{
							Bundle bundle = intent.getExtras();
							if (bundle != null)
							{
								Set<String> keys = bundle.keySet();
								Iterator<String> it = keys.iterator();
								while (it.hasNext())
								{
									String key = it.next();
									//Log.e(LOG_TAG,"[" + key + "=" + bundle.get(key)+"]");
									Element other = doc.createElement("intent_field");
									other.setAttribute("name", key);
									other.appendChild(doc.createTextNode(bundle.get(key).toString()));
									message_other.appendChild(other);
								}
							}
							rootElement.appendChild(message_other);
							if (type.startsWith("image/"))
							{
								try
								{
									Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
									File myFile = new File(imageUri.toString());
									String fileName = myFile.getName();
									String imageData = handleSendImage(intent);
									Element encoded_image = doc.createElement("image_base64");
									encoded_image.setAttribute("name", fileName);
									encoded_image.appendChild(doc.createTextNode(imageData));
									rootElement.appendChild(encoded_image);
								}
								catch (IOException e)
								{}
							}
						}
						

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

					}
					catch (ParserConfigurationException pce)
					{
						pce.printStackTrace();
					}
					catch (TransformerException tfe)
					{
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
	    if (subjectText != null)
		{
			EditText noteTitleFeild = (EditText) findViewById(R.id.note_title);
			noteTitleFeild.setText(subjectText);
		}
	}
	public String handleSendImage(Intent intent) throws FileNotFoundException, IOException
	{
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		String imageDataString = null;
		File myFile = new File(imageUri.toString());
		String imageFilePath = myFile.getAbsolutePath();
		FileInputStream imageInFile = new FileInputStream(imageFilePath);
		byte imageData[] = new byte[(int) myFile.length()];
		imageInFile.read(imageData);

		// Converting Image byte array into Base64 String
		imageDataString = encodeImage(imageData);
		return imageDataString;
	}
	public static String encodeImage(byte[] imageByteArray)
	{
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
        // Handle item selection
        switch (item.getItemId())
		{
            case R.id.recent_notes:
            	Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RecentNotes.class);
				startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	public static String sha256(String base)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
