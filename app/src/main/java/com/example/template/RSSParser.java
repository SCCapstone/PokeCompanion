/*  JD Edwards  2021 April 4    CSCE 492

    This file parses through the data from the RSS xml file
    It gets the individual article links and miscellaneous information
    This class is instantiated in RSS_view

    Created using Anupam Chugh's tutorial from:
    https://www.journaldev.com/20126/android-rss-feed-app
 */
package com.example.template;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSParser {

    // Create strings for setting RSS data
    private static String TAG_CHANNEL = "channel";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_ITEM = "item";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_GUID = "guid";

    // Constructor
    public RSSParser() {

    }

    // Create a list of all the RSS items
    public List<RSSItem> getRSSFeedItems(String rss_url) {
        List<RSSItem> itemsList = new ArrayList<RSSItem>();
        String rss_feed_xml;

        // Retrieve the xml file from the rss_url
        rss_feed_xml = this.getXmlFromUrl(rss_url);
        // Condition if rss_url  has xml file
        if (rss_feed_xml != null) {
            try {
                // Get document elements
                Document doc = this.getDomElement(rss_feed_xml);
                NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                Element e = (Element) nodeList.item(0);

                NodeList items = e.getElementsByTagName(TAG_ITEM);
                // Loop through elements and set correct values
                for (int i = 0; i < items.getLength(); i++) {
                    Element e1 = (Element) items.item(i);

                    String title = this.getValue(e1, TAG_TITLE);
                    String link = this.getValue(e1, TAG_LINK);
                    String description = this.getValue(e1, TAG_DESRIPTION);
                    String pubdate = this.getValue(e1, TAG_PUB_DATE);
                    String guid = this.getValue(e1, TAG_GUID);

                    RSSItem rssItem = new RSSItem(title, link, description, pubdate, guid);

                    // Add item to the list
                    itemsList.add(rssItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO add rss_feed_xml == null condition

        // Return itemsList
        return itemsList;
    }

    // Class for getting the xml file from the RSS feed url
    public String getXmlFromUrl(String url) {
        String xml = null;

        // Try to get response from URL
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            // Set contents of URL to xml variable
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the xml file
        return xml;
    }

    // Class for building the document from the xml file
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Try to parse through the xml file
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    // Class for getting the value of an RSS element's node
    public final String getElementValue(Node elem) {
        // Create child
        Node child;
        // Check if element exists and has child nodes
        if (elem != null) {
            if (elem.hasChildNodes()) {
                // Parse through to the appropriate node
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        // Get the value at that node
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    // Class for getting the value of an RSS element
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}