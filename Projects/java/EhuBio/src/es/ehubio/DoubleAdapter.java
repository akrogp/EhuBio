package es.ehubio;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdapter extends XmlAdapter<String, Double> {

	@Override
	public Double unmarshal(String v) throws Exception {
		return Numbers.parseDouble(v);
	}

	@Override
	public String marshal(Double v) throws Exception {
		return Numbers.toString(v);
	}

}
