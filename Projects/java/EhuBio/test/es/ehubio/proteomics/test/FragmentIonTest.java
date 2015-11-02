package es.ehubio.proteomics.test;

import es.ehubio.proteomics.io.Mzid;

public class FragmentIonTest {
	public static void main( String[] args ) throws Exception {
		Mzid mzid = new Mzid();
		mzid.load("/home/gorka/Bio/Proyectos/Proteómica/HUPO/201410-Madrid/MyMRM/velos003607.mzid.gz",false).markDecoys("decoy");
	}
}
