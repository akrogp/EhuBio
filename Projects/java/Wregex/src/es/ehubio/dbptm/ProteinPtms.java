package es.ehubio.dbptm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.dbptm.Entry;

public class ProteinPtms {
	private Map<Integer,Ptm> ptms = new HashMap<>();
	private String id;
	
	public Map<Integer,Ptm> getPtms() {
		return ptms;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public static Map<String,ProteinPtms> load( List<Entry> list ) throws IOException {
		Map<String,ProteinPtms> map = new HashMap<>();
		//List<Entry> list = TxtReader.readFile(path);
		Ptm ptm;
		for( Entry entry : list ) {
			ProteinPtms protein = map.get(entry.getAccession());
			if( protein == null ) {
				protein = new ProteinPtms();
				protein.setId(entry.getId());
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
