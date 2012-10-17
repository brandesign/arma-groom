package org.armagetronad.groom;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.content.Loader;

public class RuntimeData {
	
	private static final List<Loader<?>> loaders = new ArrayList<Loader<?>>();
	
	public static void addLoader(Loader<?> loader) {
		loaders.add(loader);
	}

	public static List<Loader<?>> getLoaders() {
		return loaders;
	}

}
