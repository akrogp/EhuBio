package es.ehubio.db.test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import es.ehubio.db.uniprot.xml.UniprotStream;

public class UniprotStreamTest {
	@Test
	public void testFeatures() throws Exception {
		try(InputStream is = new GZIPInputStream(new FileInputStream("/home/gorka/Descargas/Temp/UP000005640_9606.xml.gz"))) {
			long count = UniprotStream.featureStreamFrom(is)
				/*.peek(entry -> {
					System.out.println(entry.getAccession().get(0));
					entry.getFeature().stream()
						.filter(feature -> "Disordered".equals(feature.getDescription()))
						.forEach(feature -> System.out.printf("%s: %s %d..%d\n", feature.getType(), feature.getDescription(), feature.getLocation().getBegin().getPosition(), feature.getLocation().getEnd().getPosition()));
						//.forEach(feature -> System.out.printf("%s: %s %s\n", feature.getType(), feature.getDescription(), feature.getLocation()));
				})*/
				.count();
			assertEquals(20597, count);
		}		
	}
}
