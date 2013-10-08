package dk.itu.bigm.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import dk.itu.bigm.application.plugin.BigMPlugin;

public class BigMPreferences extends AbstractPreferenceInitializer {
	public static final String PREFERENCE_EXTERNAL_TOOLS =
			"dk.itu.bigm.preferences.externalTools";

	@Override
	public void initializeDefaultPreferences() {
	}
	
	protected static IPreferenceStore getStore() {
		return BigMPlugin.getInstance().getPreferenceStore();
	}
	
	protected static String getString(String id) {
		return getStore().getString(id);
	}

	public static String[] getExternalTools() {
		return splitString(
				getString(BigMPreferences.PREFERENCE_EXTERNAL_TOOLS));
	}
	
	static String[] splitString(String s) {
		if (s == null || s.length() == 0)
			return new String[0];
		return s.split(":");
	}
	
	static String joinString(String[] items) {
		String result = "";
		int i = 0;
		while (i < items.length) {
			result += items[i++];
			if (i < items.length)
				result += ":";
		}
		return result;
	}
}
