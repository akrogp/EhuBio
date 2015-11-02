package es.ehubio.tools;

import es.ehubio.io.CsvUtils;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.pipeline.DecoyDb;

public class DecoyCreator implements Command.Interface {

	@Override
	public String getUsage() {
		return String.format("<target.fasta> <decoy.fasta> [%s [%s [<prefix>]]]",
			CsvUtils.getCsv('|',(Object[])DecoyDb.Strategy.values()),
			CsvUtils.getCsv('|',(Object[])Enzyme.values())
			);
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 5;
	}

	@Override
	public void run(String[] args) throws Exception {
		String target = args[0];
		String decoy = args[1];
		DecoyDb.Strategy strategy = args.length >= 3 ? DecoyDb.Strategy.valueOf(args[2]) : DecoyDb.Strategy.PSEUDO_REVERSE;
		Enzyme enzyme = args.length >= 4 ? Enzyme.valueOf(args[3]) : Enzyme.TRYPSIN;
		String prefix = args.length >= 5 ? args[4] : "decoy-";
		DecoyDb.create(target, decoy, strategy, enzyme, prefix);		
	}

}
