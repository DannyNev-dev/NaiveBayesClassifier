package classifiers.naive;

import java.util.List;

public class Email {
	
	private List<String> features;
	private String classType;
	private int featuresSize;
	
	public int getFeaturesSize() {
		return featuresSize;
	}

	public void setFeaturesSize(int featuresSize) {
		this.featuresSize = featuresSize;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public Email(String classType, List<String> features){
		this.classType = classType;
		this.features = features;
		this.featuresSize = features.size();
	}
	
}
