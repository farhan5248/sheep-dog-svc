package org.farhan.common;

import java.util.Map;

/**
 * Shared property-Map readers for the per-command fakes.
 */
final class FakeProperties {

	private FakeProperties() {
	}

	static String string(Map<String, Object> properties, String key) {
		Object value = properties.get(key);
		return value instanceof String ? (String) value : null;
	}

	static int intOrZero(Map<String, Object> properties, String key) {
		Object value = properties.get(key);
		if (value instanceof String s && !s.isEmpty()) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}
}
