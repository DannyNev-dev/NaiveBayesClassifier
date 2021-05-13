package classifiers.naive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Bayes {
	
	public List<Email> spam = new ArrayList<>();
	public List<Email> nonSpam = new ArrayList<>();
	public List<Email> testEmails = new ArrayList<>();
	double[] spamProbs;
	double[] nonSpamProbs;
	
	
	public static void main(String[] args) throws IOException {
		
		Bayes classifier = new Bayes();	
		
		if(args.length!=2) {
			classifier.loadTrainingData("spamLabelled.dat");
			classifier.computeProbabilities();
			classifier.loadAndClassifyTestData("spamUnlabelled.dat");
		}
		else {
			classifier.loadTrainingData(args[0]);
			classifier.computeProbabilities();
			classifier.loadAndClassifyTestData(args[1]);
		}
	}
	
	public void loadTrainingData(String fileName) throws IOException {
		try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
				  lines.forEach((l) -> {
					  
					  String[] tokens = l.split("\\s+");	
					  
					  String classType = tokens[tokens.length-1]; 
					  
					  List<String> features = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length-1));
					  
					  switch(classType) {		
					  	case "1":
					  		this.spam.add(new Email(classType,features));
						    return;
					  	case "0":
					  		this.nonSpam.add(new Email(classType,features));
						    return;
					  }
				  });
				}
	}
	
	public void loadAndClassifyTestData(String fileName) throws IOException {
		try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
			  lines.forEach((l) -> {
				  String[] tempTokens = l.split("\\s+");	
				  String[] tokens = Arrays.copyOfRange(tempTokens, 1, tempTokens.length);
				  
				  double spamProb = this.spamProbs[0];
				  double nonSpamProb = this.nonSpamProbs[0];
				  
				  for(int i = 0;i<tokens.length;i++) {
					  if(tokens[i].equals("1")) {
						  spamProb*=this.spamProbs[i+1];
					  	  nonSpamProb*=this.nonSpamProbs[i+1];
					  }else {
						  spamProb*=(1.0-this.spamProbs[i+1]);
					  	  nonSpamProb*=(1.0-this.nonSpamProbs[i+1]);
					  }
				  }
				  System.out.println("Classifying Test Instance");
				  System.out.println("SpamProbability: " + spamProb + " || NonSpamProbability: " + nonSpamProb);
			      if(spamProb>nonSpamProb) {
			    	  this.testEmails.add(new Email("1", Arrays.asList(tokens)));
			    	  System.out.println("Classified as spam\n");
			      }else {
			    	  this.testEmails.add(new Email("0", Arrays.asList(tokens)));
			    	  System.out.println("Classified as non-spam\n");
			      }
			  });
			}
	}
	
	public void computeProbabilities() {
		int numFeatures = this.spam.get(0).getFeaturesSize();
		int[] spamCounts = new int[numFeatures+1];
		int[] nonSpamCounts = new int[numFeatures+1];
		
		for(int i = 0;i < numFeatures+1;i++) {
			spamCounts[i] = 1;
			nonSpamCounts[i] = 1;
		}
		
		spamCounts[0] += this.spam.size();
		nonSpamCounts[0] += this.nonSpam.size();
		
		for(Email e:this.spam) {
			for(int i = 0;i < numFeatures;i++) {
				if(e.getFeatures().get(i).equals("1")) {
					spamCounts[i+1]++;
				}
			}
		}
		
		for(Email e:this.nonSpam) {
			for(int i = 0;i < numFeatures;i++) {
				if(e.getFeatures().get(i).equals("1")) {
					nonSpamCounts[i+1]++;
				}
			}
		}
		
		this.nonSpamProbs  = new double[numFeatures+1];
		this.spamProbs = new double[numFeatures+1];
		
		double total = (double)(spamCounts[0] + nonSpamCounts[0]);
		this.nonSpamProbs[0] = (double)nonSpamCounts[0] / total;
		this.spamProbs[0] = (double)spamCounts[0] / total;
		
		for(int i = 1;i < spamProbs.length;i++) {
			this.nonSpamProbs[i] = (double)nonSpamCounts[i] / (double)(nonSpamCounts[0] + 1);
			this.spamProbs[i] = (double)spamCounts[i] / (double)(spamCounts[0] + 1);
		}
		System.out.println("Finished calculating probabilities: \n" +
				"\nNon-Spam-Conditional-Probabilities (first element is p(class)): \n" + Arrays.toString(nonSpamProbs) + 
				"\nSpam-Conditional-Probabilies (first element is p(class)): \n" + Arrays.toString(spamProbs) + "\n");
	}
	

}
