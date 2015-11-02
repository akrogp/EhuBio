package es.ehubio.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class SplitUniProt implements Command.Interface {

	@Override
	public String getUsage() {		
		return "/path/to/file.fasta[.gz]";
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
		List<Fasta> all = Fasta.readEntries(args[0], SequenceType.PROTEIN);
		List<Fasta> sp = new ArrayList<>();		
		for( Fasta fasta : all )
			if( fasta.getHeader().toLowerCase().startsWith("sp|") )
				sp.add(fasta);
		File file = new File(args[0]);
		Fasta.writeEntries(new File(file.getParent(),"SP_"+file.getName()).getAbsolutePath(), sp);
	}

}
