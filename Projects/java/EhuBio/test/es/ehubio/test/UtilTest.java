package es.ehubio.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import es.ehubio.Util;

public class UtilTest {

	@Test
	public void testIsEmpty() {
		assertTrue(Util.isEmpty(null));
		assertTrue(Util.isEmpty(0));
		assertTrue(Util.isEmpty(0.0));
		assertTrue(Util.isEmpty(0.0F));
		assertTrue(Util.isEmpty(new int[0]));
		assertTrue(Util.isEmpty(new String[0]));
		assertTrue(Util.isEmpty(""));
		assertTrue(Util.isEmpty(new ArrayList<>()));
		assertTrue(Util.isEmpty(new HashMap<>()));
		
		assertFalse(Util.isEmpty(new UtilTest()));
		assertFalse(Util.isEmpty(1));
		assertFalse(Util.isEmpty(1.0));
		assertFalse(Util.isEmpty(1.0F));
		assertFalse(Util.isEmpty(new int[1]));
		assertFalse(Util.isEmpty(new String[1]));
		assertFalse(Util.isEmpty("test"));
		List<String> list = new ArrayList<String>();
		list.add("test");
		assertFalse(Util.isEmpty(list));
		Map<String, String> map = new HashMap<>();
		map.put("test", "not empty");
		assertFalse(Util.isEmpty(map));
	}

}
