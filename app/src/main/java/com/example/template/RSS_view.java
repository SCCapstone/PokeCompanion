/*  JD Edwards  2021 April 4    CSCE 492

    This file initiates a page for viewing an RSS feed
    It retrieves an RSS xml file from pokemondb.net and displays clickable article links
    This is so the user has an up to date news feed dedicated to the Pokemon world

    Created using Anupam Chugh's tutorial from:
    https://www.journaldev.com/20126/android-rss-feed-app
 */
package com.example.template;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RSS_view extends ListActivity {


    // Create a loading bar
    private ProgressBar pDialog;
    // Array list for displaying articles
    ArrayList<HashMap<String, String>> rssItemList = new ArrayList<>();

    RSSParser rssParser = new RSSParser();
    Toolbar toolbar;

    List<RSSItem> rssItems = new ArrayList<>();
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_PUB_DATE = "pubDate";

    // Start the activity and set the activity_rss_view.xml
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link to activity_rss_view.xml
        setContentView(R.layout.activity_rss_view);

        String rss_link = getIntent().getStringExtra("rssLink");
        new LoadRSSFeedItems().execute(rss_link);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // View article with RSSActivityBrowser.java
                Intent in = new Intent(getApplicationContext(), RSSActivityBrowser.class);
                // Retrieve article's url
                String page_url = ((TextView) view.findViewById(R.id.page_url)).getText().toString().trim();
                in.putExtra("url", page_url);
                startActivity(in);
            }
        });
    }

    // Bottom menu button links
    // Go to Add Pokemon View
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    // Go to Pokedex View
    public void gotoDexView(View view) {
        Intent intent = new Intent(this, PokedexView.class);
        startActivity(intent);
    }

    // Go to News View
    public void gotoNewsView(View view) {
        // Commented out because we are currently in News View
        //Intent intent = new Intent(this, RSS_view.class);
        //startActivity(intent);
    }

    // Go to Team View
    public void gotoTeamView(View view) {
        Intent intent = new Intent(this, PersonalDex.class);
        startActivity(intent);
    }

    // Go to Settings View
    public void gotoSettingsView(View view) {
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }

    // Class for loading and executing all of the articles
    // AsyncTask optimizes loading times
    public class LoadRSSFeedItems extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressBar(RSS_view.this, null, android.R.attr.progressBarStyleLarge);


            //Can't get the loading bar to work right now
            /*RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            pDialog.setLayoutParams(lp);
            pDialog.setVisibility(View.VISIBLE);
            relativeLayout.addView(pDialog);*/

            // Set parameters for individual article link screen space
            ConstraintLayout constraintLayout = findViewById(R.id.layout);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );

            //lp.addRule(ConstraintLayout.CENTER_IN_PARENT);
            // rightMargin probably needs tweaking to display better
            lp.rightMargin = 2;
            pDialog.setLayoutParams(lp);
            pDialog.setVisibility(View.VISIBLE);
            //constraintLayout.addView(pDialog);
        }

        @Override
        protected String doInBackground(String... args) {
            // Set the actual RSS url string from pokemondb.net
            String rss_url = "https://pokemondb.net/news/feed";

            // Set a list of all the RSS items
            rssItems = rssParser.getRSSFeedItems(rss_url);

            // Loop to set each item
            for (RSSItem item : rssItems) {
                // Create a hashmap of individual strings
                if (item.link.toString().equals(""))
                    break;
                HashMap<String, String> map = new HashMap<String, String>();

                // Get the date of each article
                String givenDateString = item.pubdate.trim();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                try {
                    // Parse through dates and try to read them correctly
                    Date mDate = sdf.parse(givenDateString);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.US);
                    item.pubdate = sdf2.format(mDate);

                } catch (ParseException e) {
                    e.printStackTrace();

                }


                // Set values for article title, link, and publication date
                map.put(TAG_TITLE, item.title);
                map.put(TAG_LINK, item.link);
                map.put(TAG_PUB_DATE, item.pubdate);

                // Add the hashlist to the rssItemList
                rssItemList.add(map);
            }

            // Update the UI
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            RSS_view.this,
                            rssItemList, R.layout.rss_list_items,
                            new String[]{TAG_LINK, TAG_TITLE, TAG_PUB_DATE},
                            new int[]{R.id.page_url, R.id.title, R.id.pub_date});

                    // Update the listview
                    setListAdapter(adapter);
                }
            });
            return null;
        }

        // Make the loading bar disappear
        protected void onPostExecute(String args) {
            pDialog.setVisibility(View.GONE);
        }
    }
}