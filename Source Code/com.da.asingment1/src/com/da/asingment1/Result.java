package com.da.asingment1;

import java.util.List;

public class Result {
	public List<String> word;
	public List<String> singleWord;
	public double confidenceScore;
	
	public void setWord(List<String> word){
		this.word = word;
	}
	
	public List<String> getWord(){
		return this.word;
	}
	
	public void setSingleWord(List<String> singleWord){
		this.singleWord = singleWord;
	}
	
	public List<String> getSingleWord(){
		return this.singleWord;
	}
	
	public void setConfidenceScore(double confidenceScore){
		this.confidenceScore = confidenceScore;
	}
	
	public double getConfidenceScore(){
		return this.confidenceScore;
	}
	
}
