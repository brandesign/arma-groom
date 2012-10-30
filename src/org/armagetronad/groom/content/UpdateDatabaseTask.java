package org.armagetronad.groom.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.armagetronad.groom.ArmaUtils;
import org.armagetronad.groom.Constants;
import org.armagetronad.groom.content.ArmaContent.Setting;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.widget.Toast;

public class UpdateDatabaseTask extends AsyncTask<Void, String, Boolean> {

	private final Context displayContext;
	private Date last;

	public UpdateDatabaseTask(Context displayContext) {
		this.displayContext = displayContext;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
			Toast.makeText(displayContext, values[0], Toast.LENGTH_LONG)
					.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if(ArmaUtils.isOnline(displayContext)) {
			publishProgress("Refreshing...");
		} else {
			publishProgress("Cannot connect to the internet");
			cancel(true);
		}
		// TODO find a way to cache the date application wide
		ContentProviderClient client = displayContext.getContentResolver()
				.acquireContentProviderClient(ArmaProvider.URI_SETTINGS);
		Cursor cursor;
		Date lastDate = null;
		try {
			String[] projection = new String[] { Setting.SETTING_PARAM,
					Setting.SETTING_VALUE };
			String selection = Setting.SETTING_PARAM + " = ?";
			String[] selectionArgs = new String[] { Setting.KEY_DATE };
			cursor = client.query(ArmaProvider.URI_SETTINGS, projection,
					selection, selectionArgs, null);
			if (cursor.moveToFirst()) {
				String dateStr = cursor.getString(1);
				lastDate = new SimpleDateFormat(ArmaProvider.DATE_FORMAT)
						.parse(dateStr);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			lastDate = null;
		}
		last = lastDate;
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		new DownloadXmlTask(displayContext, last)
		.execute(Constants.URL_XML_FEEDS);
	}

}
