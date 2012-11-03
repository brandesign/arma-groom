package org.armagetronad.groom.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.armagetronad.groom.Constants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class ArmaserversXmlParser {
	
    private final Date dateRef;
    
public ArmaserversXmlParser(Date date) {
	this.dateRef = date;
}

	public ServersFeed parse(InputStream in) throws XmlPullParserException, IOException{

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "ISO-8859-1");
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

	private ServersFeed readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<ServerEntity> servers = new ArrayList<ServerEntity>();

		Date dateTranslate = new Date();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // TODO make sure we DO get the date, else fail or prepare a failover plan
	        if(name.equals("Servers")) {
	        	String age = parser.getAttributeValue(null, "age");
	        	cal.add(Calendar.SECOND, Integer.parseInt("-"+age));
	        	date = cal.getTime();
	        	cal.add(Calendar.SECOND, -10);
	        	dateTranslate = cal.getTime();
	        	if(dateRef != null && dateTranslate.before(dateRef)) {
	        		Log.i(Constants.TAG,"Data too old, skipping this server");
	        		return null;
	        	} else if(dateRef != null) {
	        		Log.i(Constants.TAG,"Data newer ("+date+") than what we've got("+dateRef+"), let's take it!");
	        	}
	        } else if (name.equals("Server")) {
	        	servers.add(readServerTag(parser));
	        } else {
	            skip(parser);
	        }
	    }
		ServersFeed serversFeed = new ServersFeed(date, servers);
	    return serversFeed;
	}

	private ServerEntity readServerTag(XmlPullParser parser) throws XmlPullParserException, IOException {
		 parser.require(XmlPullParser.START_TAG, null, "Server");
			String name = parser.getAttributeValue(null, "name");
			String ip = parser.getAttributeValue(null, "ip");
			String port = parser.getAttributeValue(null, "port");
			String version = parser.getAttributeValue(null, "version");
			String version_min = parser.getAttributeValue(null, "version_min");
			String version_max = parser.getAttributeValue(null, "version_max");
			String description = parser.getAttributeValue(null, "description");
			String numplayers = parser.getAttributeValue(null, "numplayers");
			String maxplayers = parser.getAttributeValue(null, "maxplayers");
			String url = parser.getAttributeValue(null, "url");
		List<PlayerEntity> players = new ArrayList<PlayerEntity>();
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			if(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("Server")) {
				break;
			}
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("Player")) {
				players.add(
					new PlayerEntity(
						parser.getAttributeValue(null, "name"),
						parser.getAttributeValue(null, "global_id")
					)
				);
				
			}
		}
		return new ServerEntity(name, ip, Integer.decode(port), version, version_min, version_max, description, Integer.decode(numplayers), Integer.decode(maxplayers), url, players);
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	}
	
	
}
