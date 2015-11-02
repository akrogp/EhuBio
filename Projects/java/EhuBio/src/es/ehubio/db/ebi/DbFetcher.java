package es.ehubio.db.ebi;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import es.ehubio.db.uniprot.xml.Entry;
import es.ehubio.db.uniprot.xml.Uniprot;

public class DbFetcher {
	// http://www.ebi.ac.uk/Tools/dbfetch/dbfetch/dbfetch.databases
	public enum DataBase {
		UNIPROT("uniprotkb"),
		REFSEQ_NUCLEOTIDE("refseqn"),
		REFSEQ_PROTEIN("refseqp");
		
		private final String name;		
		private DataBase(String name) {
			this.name = name;
		}		
		public String getName() {
			return name;
		}
	}
	
	// http://www.ebi.ac.uk/Tools/dbfetch/dbfetch/dbfetch.databases
	public enum Format {
		DEFAULT("default"),
		FASTA("fasta"),
		UNIPROT("uniprot"),
		UNIPROT_XML("uniprotxml");
		
		private final String name;		
		private Format(String name) {
			this.name = name;
		}		
		public String getName() {
			return name;
		}
	}
	
	private final WebTarget target;
	
	public DbFetcher() {
		target = ClientBuilder.newClient().target("http://www.ebi.ac.uk/Tools/dbfetch/dbfetch/{db}/{id}/{format}?style=raw");
	}
	
	public String fetch( DataBase db, String id, Format format ) {
		return target
			.resolveTemplate("db", db.getName())
			.resolveTemplate("id", id)
			.resolveTemplate("format", format.getName())
			.request(MediaType.TEXT_PLAIN)
			.get(String.class);
	}
	
	public Entry fetchUniProt( String accession ) {
		return target
			.resolveTemplate("db", DataBase.UNIPROT.getName())
			.resolveTemplate("id", accession)
			.resolveTemplate("format", Format.UNIPROT_XML.getName())
			.request(MediaType.TEXT_XML)
			.get(Uniprot.class).getEntry().iterator().next();
	}
}