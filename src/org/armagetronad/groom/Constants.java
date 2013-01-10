package org.armagetronad.groom;

public interface Constants {

	/**
	 * List of URLs where we can find the serverlist
	 */
	public final String[] URL_XML_FEEDS = new String[] {
			"http://wrtlprnft.ath.cx/serverlist/serverxml.php",
			"http://crazy-tronners.com/grid/serverxml.php"
			};
	
	/**
	 * Tag for android debugging 
	 */
	public final String TAG = "ArmaGroom";
	
	/**
	 * Delay between two automatic updates of data in milliseconds
	 */
	public final long AUTO_UPDATE_DELAY = 70000L;
}
