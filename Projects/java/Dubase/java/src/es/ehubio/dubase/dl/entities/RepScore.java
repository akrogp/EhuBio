package es.ehubio.dubase.dl.entities;

public interface RepScore extends Score {
	boolean getImputed();
	Replicate getReplicateBean();
	Replicate getBasalBean();
}
