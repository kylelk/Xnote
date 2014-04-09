package com.example.xnote;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.*;

public class RecentNotes extends Activity {
	private ListView mainListView ;  
	 private ArrayAdapter<String> listAdapter ;  
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_notes);

	    mainListView = (ListView) findViewById( R.id.recent_notes_list );  
	    
	    // Create and populate a List of planet names.  
	    String[] planets = new String[] {};    
	    ArrayList<String> planetList = new ArrayList<String>();  
	    planetList.addAll( Arrays.asList(planets) );  
	      
	    // Create ArrayAdapter using the planet list.  
	    listAdapter = new ArrayAdapter<String>(this, R.layout.list_view_row, planetList); 
		
		final String path = Environment.getExternalStorageDirectory().toString()+"/xnote/";
		File f = new File(path);        
		final File [] files = f.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
			});
		
		final ArrayList<String> mNoteTitle = new ArrayList<String>();
		final ArrayList<String> mNoteContent = new ArrayList<String>();
		for (int i=0; i < files.length; i++)
		{
			//Log.d("Files", "FileName:" + file[i].getName());

			try {
				File fXmlFile = new File(path + files[i].getName());
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();

				listAdapter.add(doc.getElementsByTagName("title").item(0).getTextContent());
				mNoteTitle.add(doc.getElementsByTagName("title").item(0).getTextContent());
				mNoteContent.add(doc.getElementsByTagName("message").item(0).getTextContent());

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		mainListView.setAdapter( listAdapter ); 
		mainListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {                
  
                AlertDialog.Builder builder = new AlertDialog.Builder(RecentNotes.this);
                builder.setTitle(mNoteTitle.get(position));
                builder.setMessage(mNoteContent.get(position));

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do something
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.show();
            }                   
			
        });
		
		mainListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    final int position, long id) {                
  
                AlertDialog.Builder builder = new AlertDialog.Builder(RecentNotes.this);
                builder.setTitle("Delete Note?");
                builder.setMessage(mNoteTitle.get(position));

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do something                    
                    	File file = new File(path + files[position].getName());
                    	file.delete();
                        listAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.show();
                return true;
            }
		});

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_notes_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_note_option:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
