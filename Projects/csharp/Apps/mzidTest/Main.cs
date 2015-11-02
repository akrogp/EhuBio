// $Id: Main.cs 78 2013-09-20 17:21:43Z gorka.prieto@gmail.com $
// 
// Main.cs
//  
// Author:
//      Gorka Prieto <gorka.prieto@gmail.com>
// 
// Description:
//      Simple app for testing library functionality while developing
//  
// Copyright (c) 2011 Gorka Prieto
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

using System;
using EhuBio.Proteomics.Hupo.mzIdentML;

namespace Proteomics {

class Tester {
	public static void Main( string[] args ) {
		//mzidFile.Validate( "PMF_example.mzid" );
		//mzidFile.Validate( "test.mzid" );
	
		mzidFile mz = new mzidFile();
		
		// Generate a mzIdentML file
		mz.AddOntology( "PSI-MS", "Proteomics Standards Initiative Mass Spectrometry Vocabularies", "2.25.0",
			"http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo" );
		mz.AddAnalysisSoftware(
        	"EHU_Tester", "UPV/EHU Tester", "0.1", "http://www.ehu.es", "UPV_EHU",
        	"MS:none", "software vendor", "PSI-MS",
        	"MS:none", "PAnalyzer", "PSI-MS", "No customizations" );
        mz.SetProvider( "SGI", "DOC_OWNER", "MS:none", "Kerman", "PSI-MS" );
        mz.AddPerson( "DOC_OWNER", "Kerman", "Aloria", "kerman.aloria@ehu.es", "UPV_EHU" );
        mz.AddOrganization( "UPV_EHU", "University of the Basque Country",
        	"Barrio Sarriena s/n, 48940 Leioa, Spain", "+34 94 601 200", "secretariageneral@ehu.es" );
        mz.AddProtein( "DBSeq_UVRB_THET8", "SDB_SwissProt", "UVRB_THET8", "MTFRYRGPSPKGDQPKAIAGLVEALRDGERFVTLLGATGTGKTVTMAKVIEALGRPALVLAPNKILAAQLAAEFRELFPENAVEYFISYYDYYQPEAYVPGKDLYIEKDASINPEIERLRHSTTRSLLTRRDVIVVASVSAIYGLGDPREYRARNLVVERGKPYPREVLLERLLELGYQRNDIDLSPGRFRAKGEVLEIFPAYETEPIRVELFGDEVERISQVHPVTGERLRELPGFVLFPATHYLSPEGLEEILKEIEKELWERVRYFEERGEVLYAQRLKERTLYDLEMLRVMGTCPGVENYARYFTGKAPGEPPYTLLDYFPEDFLVFLDESHVTVPQLQGMYRGDYARKKTLVDYGFRLPSALDNRPLRFEEFLERVSQVVFVSATPGPFELAHSGRVVEQIIRPTGLLDPLVRVKPTENQILDLMEGIRERAARGERTLVTVLTVRMAEELTSFLVEHGIRARYLHHELDAFERQALIRDLRLGHYDCLVGINLLREGLDIPEVSLVAILDADKEGFLRSERSLIQTIGRAARNARGEVWLYADRVSEAMQRAIEETNRRRALQEAYNLEHGITPETVRKEVRAVIRPEGYEEAPLEADLSGEDLRERIAELELAMWQAAEALDFERAARLRDEIRALEARLQGVRAPEPVPGGRKRKRR" );
        mz.AddPeptide( "peptide_1_1", "GEVWLYADR" );
		mz.Save( "test.mzid" );
		
		// Read a mzIdentML file
		/*mz.Load( "mzident_with_grouping.mzid" );
		mz.RetrieveSequences();
		
		// Include sequence information
		mz.Save( "test2.mzid" );*/
	}
}

}	// namespace Proteomics