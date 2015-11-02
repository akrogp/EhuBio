package es.ehubio.tools;

import es.ehubio.dna.DnaUtils;

public class ReverseStrand implements Command.Interface {

	@Override
	public String getUsage() {
		return "<forward_strand>";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public void run(String[] args) throws Exception {
		System.out.println(DnaUtils.getReverseStrand(args[0]));
	}
}
