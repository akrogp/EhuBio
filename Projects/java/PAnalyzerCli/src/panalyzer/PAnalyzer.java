package panalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import panalyzer.io.DataFile;
import panalyzer.model.Item;
import panalyzer.model.Model;
import panalyzer.model.PeptideType;
import panalyzer.model.ProteinType;

public class PAnalyzer {
	
	public static final String GROUP = "proteinGroup";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if( args.length < 3 || args.length > 4 ) {
			System.out.println("Usage:\n\tPanalyzer <pep2prot.tsv> <pepId> <protId> [<output.tsv>]");
			System.exit(1);
		}
		String input = args[0];
		String pepId = args[1];
		String protId = args[2];
		String output = args.length > 3 ? args[3] : input;
		Model model = DataFile.load(input, pepId, protId);
		run(model);
		DataFile.save(model, output, protId);
	}

	// See steps at: Prieto et al. BMC Bioinformatics 2012, 13:288
	// http://www.biomedcentral.com/1471-2105/13/288
	public static void run(Model model) {
		Collection<Item> peptides = model.getPeptides().values();
		Collection<Item> proteins = model.getProteins().values();
		
		// 1.a) + 1.b): UNIQUE + CONCLUSIVE
		for( Item pep : peptides )
			if( pep.getItems().size() == 1 ) {
				pep.setType(PeptideType.UNIQUE);
				pep.getItems().get(0).setType(ProteinType.CONCLUSIVE);
			} else if( !pep.getItems().isEmpty() )
				pep.setType(PeptideType.DISCRIMINATING);
		
		// 1.c): NON_DISCRIMINATING
		for( Item prot : proteins )
			if( ProteinType.CONCLUSIVE.equals(prot.getType()) )
				for( Item pep : prot.getItems() )
					if( !PeptideType.UNIQUE.equals(pep.getType()) )
						pep.setType(PeptideType.NON_DISCRIMINATING);

		// 2): DISCRIMINATING
		for( Item pep : peptides )
			if( PeptideType.DISCRIMINATING.equals(pep.getType()) ) {
				List<Item> group = pep.getItems();
				for( Item pep2 : group.get(0).getItems() ) {
					if( !PeptideType.DISCRIMINATING.equals(pep2.getType()) || pep2.getItems().size() <= group.size() )
						continue;
					boolean nond = true;
					for( Item prot : group )
						if( !prot.getItems().contains(pep2) ) {
							nond = false;
							break;
						}
					if( nond )
						pep2.setType(PeptideType.NON_DISCRIMINATING);
				}
			}
		
		// 3.a): NON_CONCLUSIVE
		for( Item prot : proteins )
			if( prot.getItems().stream().allMatch(pep -> PeptideType.NON_DISCRIMINATING.equals(pep.getType())) )
				prot.setType(ProteinType.NON_CONCLUSIVE);
		
		// 3.b) + 3.c): AMBIGUOUS + INDISTINGUISHABLE
		int groupId = 1;
		for( Item prot : proteins )
			if( prot.getProps().get(GROUP) == null ) {
				List<Item> group = new ArrayList<>(); 
				populateGroup(group, prot);
				boolean indistinguishable = isIndistinguishable(group);
				for( Item prot2 : group ) {
					prot2.getProps().put(GROUP, groupId+"");
					if( prot2.getType() == null )
						prot2.setType( indistinguishable ? ProteinType.INDISTINGUISHABLE : ProteinType.AMIBIGUOUS);
				}
				groupId++;
			}
	}	

	private static void populateGroup(List<Item> group, Item prot) {
		group.add(prot);
		for( Item pep : prot.getItems() )
			if( PeptideType.DISCRIMINATING.equals(pep.getType()) )
				for( Item prot2 : pep.getItems() )
					if( !group.contains(prot2) )
						populateGroup(group, prot2);
	}
	
	private static boolean isIndistinguishable(List<Item> group) {
		if( group.size() < 2 )
			return false;
		Set<Item> discriminating = group.get(0).getItems().stream()
				.filter(pep -> PeptideType.DISCRIMINATING.equals(pep.getType()))
				.collect(Collectors.toSet());
		for( int i = 1; i < group.size(); i++ ) {
			if( group.get(i).getItems().size() < discriminating.size() )
				return false;
			Set<Item> set = group.get(i).getItems().stream()
				.filter(pep -> PeptideType.DISCRIMINATING.equals(pep.getType()))
				.collect(Collectors.toSet());
			if( set.size() != discriminating.size() || !set.containsAll(discriminating) )
				return false;
		}
		return true;
	}
}
