package es.ehubio.wregex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.wregex.service.DecoyService;
import es.ehubio.wregex.service.DecoyService.Region;

public class DecoyTest {

	@Test
	public void testReal() {
		Fasta input = new Fasta("P15516", "Histatin-3", "MKFFVFALILALMLSMTGADSHAKRHHGYKRKFHEKHHSHRGYRSNYLYDN", SequenceType.PROTEIN);
		
		List<Region> regions = new ArrayList<>();		
		Region region = new Region();
		region.setBegin(27);
		region.setEnd(51);
		regions.add(region);
		
		Fasta output = DecoyService.getDecoy(input, regions, "decoy-");
		
		assertEquals(input.getSequence().length(), output.getSequence().length());
		assertEquals(output.getSequence().charAt(0), 'M');
		assertNotEquals(input.getSequence(), output.getSequence());
		assertTrue(haveSameLetters(input.getSequence().substring(1, 26), output.getSequence().substring(1, 26)));
		assertTrue(haveSameLetters(input.getSequence().substring(26, 51), output.getSequence().substring(26, 51)));		
	}
	
	@Test
	public void testUnreal() {
		Fasta input = new Fasta("P12345", "test", "MFAAAAAKKKKKKH", SequenceType.PROTEIN);
		
		List<Region> regions = new ArrayList<>();		
		Region region = new Region();
		region.setBegin(3);
		region.setEnd(7);
		regions.add(region);
		region = new Region();
		region.setBegin(8);
		region.setEnd(13);
		regions.add(region);
		
		Fasta output = DecoyService.getDecoy(input, regions, "decoy-");
		
		assertEquals(input.getSequence(), output.getSequence());
	}
	
	@Test
	public void testBegin() {
		Fasta input = new Fasta("P12345", "test", "MAAAAAKKKKKK", SequenceType.PROTEIN);
		
		List<Region> regions = new ArrayList<>();		
		Region region = new Region();
		region.setBegin(1);
		region.setEnd(6);
		regions.add(region);
		
		Fasta output = DecoyService.getDecoy(input, regions, "decoy-");
		
		assertEquals(input.getSequence(), output.getSequence());
	}
	
	@Test
	public void testEnd() {
		Fasta input = new Fasta("P12345", "test", "MAAAAAKKKKKK", SequenceType.PROTEIN);
		
		List<Region> regions = new ArrayList<>();		
		Region region = new Region();
		region.setBegin(7);
		region.setEnd(12);
		regions.add(region);
		
		Fasta output = DecoyService.getDecoy(input, regions, "decoy-");
		
		assertEquals(input.getSequence(), output.getSequence());
	}
	
	@Test
	public void testNested() {
		Fasta input = new Fasta("P12345", "test", "MKFFVFALILALMLSMTGADSHAKRHHGYKRKFHEKHHSHRGYRSNYLYDN", SequenceType.PROTEIN);
		
		List<Region> regions = new ArrayList<>();		
		Region region = new Region();
		region.setBegin(5);
		region.setEnd(30);
		regions.add(region);
		region = new Region();
		region.setBegin(8);
		region.setEnd(20);
		regions.add(region);
		
		Fasta output = DecoyService.getDecoy(input, regions, "decoy-");
		
		assertEquals(input.getSequence().length(), output.getSequence().length());
		assertEquals(output.getSequence().charAt(0), 'M');
		assertNotEquals(input.getSequence(), output.getSequence());
		assertTrue(haveSameLetters(input.getSequence().substring(4, 30), output.getSequence().substring(4, 30)));
		assertFalse(haveSameLetters(input.getSequence().substring(7, 20), output.getSequence().substring(7, 20)));
	}

	private static boolean haveSameLetters(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }
        
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();
        
        Arrays.sort(charArray1);
        Arrays.sort(charArray2);
        
        return Arrays.equals(charArray1, charArray2);
    }
}
