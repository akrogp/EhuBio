package es.ehubio.dbptm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.PtmItem;

public class ProteinPtms {
	private Map<Integer,Ptm> ptms = new HashMap<>();
	private String protein;
	
	public Map<Integer,Ptm> getPtms() {
		return ptms;
	}
	
	public String getProtein() {
		return protein;
	}
	
	public void setProtein(String id) {
		this.protein = id;
	}
	
	public static Map<String,ProteinPtms> load( List<? extends PtmItem> list ) throws IOException {
		Map<String,ProteinPtms> map = new HashMap<>();
		//List<Entry> list = TxtReader.readFile(path);
		Ptm ptm;
		for( PtmItem entry : list ) {
			ProteinPtms protein = map.get(entry.getAccession());
			if( protein == null ) {
				protein = new ProteinPtms();
				protein.setProtein(entry.getProtein());
				map.put(entry.getAccession(), protein);
			}
			// Filter predicted PTMs
			/*if( entry.getResource().contains("HMM") )
				continue;*/
			ptm = protein.getPtms().get(entry.getPosition());
			if( ptm == null ) {
				ptm = new Ptm();
				ptm.position = entry.getPosition();
				protein.getPtms().put(ptm.position, ptm);
			}
			ptm.types.add(entry.getType());
		}
		return map;
	}
}
