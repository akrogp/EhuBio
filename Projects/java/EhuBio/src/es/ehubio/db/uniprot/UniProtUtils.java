package es.ehubio.db.uniprot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ehubio.db.uniprot.xml.FeatureType;
import es.ehubio.model.ProteinModification;
import es.ehubio.model.ProteinModificationType;

public class UniProtUtils {
	public static boolean validAccession( String acc ) {
		Matcher matcher = accPattern.matcher(acc);
		return matcher.matches();
	}
	
	public static String canonicalAccesion( String acc ) {
		return acc.replaceAll("NX_","").replaceAll("-.*", "");
	}
	
	public static String reducedAccession( String acc ) {
		return acc.replaceAll("NX_","").replaceAll("-1$", "");
	}
	
	public static ProteinModification featureToModification( FeatureType feature ) {
		if( feature.getDescription() == null || feature.getLocation() == null || feature.getLocation().getPosition() == null )
			return null;
		ProteinModification mod = new ProteinModification();
		mod.setName(feature.getDescription());
		if( feature.getDescription().startsWith("Phospho") )
			mod.setType(ProteinModificationType.PHOSPHORYLATION);
		mod.setPosition(feature.getLocation().getPosition().getPosition().intValue());
		return mod;
	}
	
	protected static Pattern accPattern = Pattern.compile(
		// "([A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9])|([OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9])");
		"[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");
}
