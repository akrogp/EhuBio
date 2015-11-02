package es.ehubio.wregex;

import java.util.List;

public final class TrainingMotif {
	private final Result result;
	private final TrainingGroup group;
	
	public TrainingMotif( Result result, TrainingGroup group ) {
		this.result = result;
		this.group = group;
	}
	
	public Result getResult() {
		return result;
	}
	
	public String getName() {
		return result.getName();
	}
	
	public int getStart() {
		return result.getStart();
	}
	
	public int getEnd() {
		return result.getEnd();
	}
	
	public String getMotif() {
		return result.getMatch();
	}
	
	public String getAlignment() {
		return result.getAlignment();
	}
	
	public double getWeight() {
		return group.getWeight();
	}
	
	public String getWeightAsString() {
		return String.format("%.1f", getWeight());
	}
	
	public int getCombinations() {
		return group.size();
	}
	
	public double getDividedWeight() {
		if( !isValid() )
			return 0.0;
		return getWeight()/getCombinations();
	}
	
	public String getDividedWeightAsString() {
		return String.format("%.1f", getDividedWeight());
	}
	
	public double getScore() {
		return result.getScore();
	}
	
	public String getScoreAsString() {
		return String.format("%.1f", getScore());
	}
	
	public boolean overlaps(Result result) {
		return this.result.overlaps(result);
	}
	
	public boolean linked(Result result) {
		return this.result.equals(result);
	}

	public TrainingGroup getGroup() {
		return group;
	}
	
	public List<String> getRegexGroups() {
		return result.getGroups();
	}
	
	public void remove() {
		group.remove(this);
	}
	
	public void recycle() {
		if( !group.contains(this) )
			group.add(this);
	}
	
	public boolean isValid() {
		return group.contains(this);
	}
}
