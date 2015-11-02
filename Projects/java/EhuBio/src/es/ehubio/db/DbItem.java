package es.ehubio.db;

public interface DbItem {
	String getAccession();
	void setAccession(String accession);

	String getName();
	void setName(String name);

	String getDescription();
	void setDescription(String description);
}
