package es.ehubio.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import es.ehubio.Strings;

public class MergeStrings implements Command.Interface {

	@Override
	public String getUsage() {		
		return "<string1> <string2> ...";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return -1;
	}

	@Override
	public void run(String[] args) throws Exception {
		Set<String> set = new HashSet<>(Arrays.asList(args));
		System.out.println(Strings.merge(set));
	}

}
