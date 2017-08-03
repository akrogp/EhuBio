@XmlJavaTypeAdapters({
	@XmlJavaTypeAdapter(type=Double.class, value=DoubleAdapter.class)
})
package es.ehubio.proteomics.isb;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import es.ehubio.DoubleAdapter;