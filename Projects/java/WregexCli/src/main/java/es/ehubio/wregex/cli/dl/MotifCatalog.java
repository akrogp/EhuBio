package es.ehubio.wregex.cli.dl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import es.ehubio.wregex.Pssm;
import es.ehubio.wregex.PssmBuilder.PssmBuilderException;

/**
 * CLI-specific data access: loads {@link MotifConfiguration} (the webapp's
 * motif schema) from the built-in catalog bundled as a classpath resource
 * (trimmed copy of the webapp's motifs.xml, NES/NLS only) and its PSSM files.
 */
public class MotifCatalog {
	private static final String RESOURCE_PATH = "/motifs/motifs.xml";

	private final List<MotifInformation> motifs;

	private MotifCatalog(List<MotifInformation> motifs) {
		this.motifs = motifs;
	}

	public static MotifCatalog load() throws IOException {
		try (Reader reader = new InputStreamReader(open(RESOURCE_PATH))) {
			return new MotifCatalog(MotifConfiguration.load(reader).getMotifs());
		}
	}

	public List<MotifInformation> getMotifs() {
		return motifs;
	}

	public MotifInformation findMotif(String name) {
		return motifs.stream()
				.filter(m -> m.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}

	public Pssm loadPssm(String fileName) throws IOException, PssmBuilderException {
		try (Reader reader = new InputStreamReader(open("/motifs/" + fileName))) {
			return Pssm.load(reader, true);
		}
	}

	private static InputStream open(String resource) throws IOException {
		InputStream stream = MotifCatalog.class.getResourceAsStream(resource);
		if (stream == null)
			throw new IOException("Missing bundled resource: " + resource);
		return stream;
	}
}
