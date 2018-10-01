package es.ehubio.ubase.dl.providers;

import java.util.ArrayList;
import java.util.List;

public class MaxQuantProvider implements Provider {

	@Override
	public String getName() {
		return "MaxQuant";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public List<FileType> getInputFiles() {
		if( types == null ) {
			types = new ArrayList<>();
			types.add(new FileType("peptides.txt"));
			types.add(new FileType("proteinGroups.txt"));
			types.add(new FileType("GlyGly (K)Sites.txt"));
		}
		return types;
	}

	private List<FileType> types;
}
