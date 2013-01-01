package org.armagetronad.groom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

public class ArmaUtils {

	private static HashMap<String, String[]> auths = new HashMap<String, String[]>();

	static {
		auths.put("ct",
				new String[] { "http://crazy-tronners.com/users.php?sort=auth_last&sort_type=DESC&q=", "" });
		auths.put("forums",
				new String[] { "http://forums3.armagetronad.net/memberlist.php?mode=viewprofile&un=", "" });
		auths.put("pru", new String[] {
				"http://trongame.de/forum/memberlist.php?mode=viewprofile&un=", "" });
		auths.put("phoenix-clan.org", new String[] {
				"http://phoenix-clan.org/memberlist.php?mode=viewprofile&un=", "" });
		auths.put("aagid", new String[] { "http://aagid.net/" });
		auths.put("mym", new String[] { "http://www.mymclan.com/memberlist.php?mode=viewprofile&un=", "" });
		auths.put("speedersclan.org", new String[] { "http://speedersclan.org/forums/memberlist.php?mode=viewprofile&un=", "" });
	}

	public static final String uncolourize(String str) {
		return str.replaceAll("0x.{6}", "");
	}

	public static SpannableString colourize(String raw) {
		String uncoloredName = uncolourize(raw);
		SpannableString str = new SpannableString(uncoloredName);
		char c, cc;
		String color = "RESETT";
		int start = 0;
		int end = 0;
		for (int i = 0; i < raw.length() - 1; i++) {
			c = raw.charAt(i);
			cc = raw.charAt(i + 1);
			// if we detect a new color tag
			if (c == '0' && cc == 'x') {
				if (start != end) {
					str.setSpan(new ForegroundColorSpan(getColorInt(color)),
							start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				}
				start = end;
				i += 2;
				color = "";
				for (int k = i; k < i + 6; k++) {
					try {
						color += raw.charAt(k);
					} catch (IndexOutOfBoundsException e) {
						color = "";
						break;
					}
				}
				i += 5;
			} else {
				end++;
			}
		}
		str.setSpan(new ForegroundColorSpan(getColorInt(color)), start,
				uncoloredName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	private static int getColorInt(String color) {
		color = color.toUpperCase();
		// following the game's convention. see
		// http://bazaar.launchpad.net/~armagetronad-dev/armagetronad/trunk-armagetronad-work/view/head:/src/render/rFont.cpp#L775
		color = (color == "RESETT") ? "F8F8F8" : color;
		// we make sure the value is valid. (maybe unexpected color for people not using right codes)
		color = color.replaceAll("[^0-9A-F]", "F");
		return Color.parseColor("#" + color);
	}

	public static HttpURLConnection getConnection(String urlString,
			int... acceptedResponseCodes) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		boolean valid = false;
		int response = conn.getResponseCode();
		for (int code : acceptedResponseCodes) {
			valid = (code == response) ? true : valid;
		}
		if (!valid) {
			throw new IOException("Response code not satisfactory");
		}
		Log.i(Constants.TAG, "got response " + conn.getResponseCode()
				+ " from server of type " + conn.getContentType());
		return conn;
	}

	public static SpannableString linkifyGid(String gid) {
		if (gid == null) {
			return null;
		}
		SpannableString ret = new SpannableString(gid);
		String name = gid.substring(0, gid.lastIndexOf("@"));
		String authority = gid.substring(gid.lastIndexOf("@") + 1);
		String mainAuth = authority.contains("/") ? authority.substring(0,
				authority.indexOf("/")) : authority;

		if (auths.containsKey(mainAuth)) {

			String link;
			try {
				if (auths.get(mainAuth).length == 1) {
					link = auths.get(mainAuth)[0];
				} else if (auths.get(mainAuth).length == 2) {
					link = auths.get(mainAuth)[0]
							+ URLEncoder.encode(name, "UTF-8")
							+ auths.get(mainAuth)[1];
				} else {
					return ret;
				}
			} catch (UnsupportedEncodingException e) {
				return ret;
			}

			ret = new SpannableString(Html.fromHtml("<a href=\"" + link + "\">"
					+ gid + "</a>"));
			
		} else if (mainAuth.matches(".*\\..*")) { // if it contains a dot
			
			// we try a link to the supposed webpage of this auth
			ret = new SpannableString(Html.fromHtml("<a href=\"http://" + mainAuth + "\">"
					+ gid + "</a>"));
			
		}

		return ret;
	}

	public static CharSequence displayPlayer(String playerName,
			String playerGID, boolean linkGID) {
		SpannableString name = colourize(playerName);
		if (playerGID == null) {
			return name;
		} else {
			SpannableString gid = linkifyGid(playerGID);
			return TextUtils.concat(name, " (", gid, ")");
		}
	}

	public static void displayMessage(Context context, String message,
			int length) {
		try {
			Toast.makeText(context, message, length).show();
		} catch (Exception e) {
		}
	}
	
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
