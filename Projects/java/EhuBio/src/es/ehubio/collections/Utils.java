package es.ehubio.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	public static <T> Map<String, T> getMap(Collection<? extends T> list, Identificator<T> id) {
		Map<String, T> map = new HashMap<String, T>();
		for( T object : list )
			map.put(id.getId(object), object);
		return map;
	}
}
