package mc.protocol.utils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Table<C, R, V> {

	private final Map<C, Map<R, V>> map = new HashMap<>();

	@Nullable
	public V getColumnAndRow(C column, R row) {
		if (!map.containsKey(column)) {
			return null;
		}

		return map.get(column).get(row);
	}

	public void put(C column, R row, V value) {
		map.computeIfAbsent(column, c -> new HashMap<>()).put(row, value);
	}
}
