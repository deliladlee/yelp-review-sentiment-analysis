import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import edu.smu.tspell.wordnet.*;

public class CompLex {
	
	public static void addSynonyms(ArrayList<String> array, WordNetDatabase db) {
		
		ArrayList<String> syns = new ArrayList<>();
		
		for(String word : array) {
			
			Synset[] synsets = db.getSynsets(word);
			
			for(Synset set : synsets) {
				WordSense[] synonyms = set.getDerivationallyRelatedForms(word);
				
				for(WordSense sense : synonyms) {
					String synonym = sense.getWordForm();
					syns.add(synonym);
				}
			}
		}
		
		for(String word : syns) {
			array.add(word);
		}
		
		removeDuplicates(array);
	}
	
	public static void addAntonyms(ArrayList<String> pos, ArrayList<String> neg, WordNetDatabase db) {
		
		ArrayList<String> pos_ants = new ArrayList<>();
		ArrayList<String> neg_ants = new ArrayList<>();
		
		for(String word : pos_ants) {
			
			Synset[] synsets = db.getSynsets(word);
			
			for(Synset set : synsets) {
				WordSense[] antonyms = set.getAntonyms(word);
				
				for(WordSense sense : antonyms) {
					String antonym = sense.getWordForm();
					pos_ants.add(antonym);
				}
			}
		}
		
		for(String word : neg_ants) {
			
			Synset[] synsets = db.getSynsets(word);
			
			for(Synset set : synsets) {
				WordSense[] antonyms = set.getAntonyms(word);
				
				for(WordSense sense : antonyms) {
					String antonym = sense.getWordForm();
					neg_ants.add(antonym);
				}
			}
		}
		
		for(String word : pos_ants) {
			neg.add(word);
		}
		
		for(String word : neg_ants) {
			pos.add(word);
		}
		
		removeDuplicates(pos);
		removeDuplicates(neg);
	}
	
	public static int computeWordScore(ArrayList<String> lex, ArrayList<String> positive, ArrayList<String> negative, ArrayList<String> minus, ArrayList<String> plus) {
	
		int pos_score = 0;
		int neg_score = 0;
		
		for(int i=0; i<lex.size(); i++) {

			for(String str : positive) {
				if(lex.get(i).equals(str)) {
					boolean flag = false;
					
					if(i>0) {
						for(String neg : minus) {
							if(lex.get(i-1).equals(neg)) {
								neg_score++;
								flag = true;
							}
						}
						
						for(String pro : plus) {
							if(lex.get(i-1).equals(pro)) {
								pos_score += 2;
								flag = true;
							}
						}
						
						if(!flag)
							pos_score++;
					}
					else
						pos_score++;
				}	
			}
			
			for(String str : negative) {
				if(lex.get(i).equals(str)) {
					boolean flag = false;
					
					if(i>0) {
						for(String neg : minus) {
							if(lex.get(i-1).equals(neg)) {
								pos_score++;
								flag = true;
							}
						}
						
						for(String pro : plus) {
							if(lex.get(i-1).equals(pro)) {
								neg_score += 2;
								flag = true;
							}
						}
						
						if(!flag)
							neg_score++;
					}
					else
						neg_score++;
				}
				
			}
		}
		
		return pos_score - neg_score;
	}
	
	public static int computePhraseScore(ArrayList<String> lex, ArrayList<String[]> positive, ArrayList<String[]> negative, ArrayList<String> minus, ArrayList<String> plus) {
		
		int pos_score = 0;
		int neg_score = 0;
		
		for(int i=1; i<lex.size(); i++) {
			
			for(int j=0; j<positive.size(); j++) {
				String[] pos = positive.get(j);
				
				if((lex.get(i-1).equals(pos[0])) && (lex.get(i).equals(pos[1]))) {
					
					boolean flag = false;
					
					if(i>1) {
						
						for(String word : minus) {
							if(lex.get(i-2).equals(word)) {
								neg_score++;
								flag = true;
							}
						}
						
						for(String word : plus) {
							if(lex.get(i-2).equals(word)) {
								pos_score += 2;
								flag = true;
							}
						}
						
						if(!flag)
							pos_score++;
					}
					else
						pos_score++;
				}
			}
			
			for(int k=1; k<negative.size(); k++) {
				String[] neg = negative.get(k);
			  
				if((lex.get(i-1).equals(neg[0])) && (lex.get(i).equals(neg[1]))) {
					boolean flag = false;
					
					if(i>1) {
						
						for(String word : minus) {
							if(lex.get(i-2).equals(word)) {
								pos_score++;
								flag = true;
							}
						}
						
						for(String word : plus) {
							if(lex.get(i-2).equals(word)) {
								neg_score += 2;
								flag = true;
							}
						}
						
						if(!flag)
							neg_score++;
					}
					else
						neg_score++;
				}
			}
		}
		
		return pos_score - neg_score;
	}
	
	public static int computeLikeScore(ArrayList<String >lex, ArrayList<String> minus, ArrayList<String> plus) {
		int pos_score = 0;
		int neg_score = 0;
		
		String like = "like";
		String liked = "liked";
		
		for(int i=0; i<lex.size(); i++) {
			if(lex.get(i).equals(like) || lex.get(i).equals(liked)) {
				
				if(i == 1) {
					if(lex.get(i-1).equals("i")) 
						pos_score++;
				}
				else if(i > 1) {
					if(lex.get(i-1).equals("i"))
						pos_score++;
					else if(lex.get(i-2).equals("i")) {
						
						for(String word : minus) {
							if(lex.get(i-2).equals(word)) {
								neg_score++;
							}
						}
						
						for(String word : plus) {
							if(lex.get(i-2).equals(word)){
								pos_score += 2;
							}
						}
						
					}
				}
			}
		}
		
		return pos_score - neg_score;
	}
	
	public static void removeDuplicates(ArrayList<String> array) {
		
		Set<String> hash = new HashSet<>();
		
		hash.addAll(array);
		array.clear();
		array.addAll(hash);
	}

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		WordNetDatabase database = null;
		
		ArrayList<ArrayList<String>> reviews = new ArrayList<>();
		ArrayList<Integer> ratings = new ArrayList<>();
		ArrayList<String> positive_words = new ArrayList<>();
		ArrayList<String> negative_words = new ArrayList<>();
		ArrayList<String[]> positive_phrases = new ArrayList<>();
		ArrayList<String[]> negative_phrases = new ArrayList<>();
		ArrayList<String> negators = new ArrayList<>();
		ArrayList<String> emphasizers = new ArrayList<>();
		int selection=0, number_of_reviews=0, rated=0;
		
		try {
			System.out.println("1. Run with baseline");
			System.out.println("2. Run with improvement algorithms");
			System.out.println("Make your selection: ");
			
			selection = sc.nextInt();
			
			if(selection != 1 && selection != 2) {
				System.out.println("ERROR: Invalid selection");
				System.exit(1);
			}
			
			System.out.println("1. With rating included");
			System.out.println("2. Without rating included");
			System.out.println("Make your selection: ");
			
			rated = sc.nextInt();

			if(selection == 1) {
				Scanner bw = new Scanner(new File("baseline_words.txt"));
				
				while(bw.hasNext()) {
					String temp = bw.next();
						
					if(temp.equals("*")) {
						while(bw.hasNext()) {
							String temp2 = bw.next();
								
							negative_words.add(temp2);
						}
					} else {
						positive_words.add(temp);
					}
				}
					
				bw.close();
			}
			else {
				Scanner bw = new Scanner(new File("test_words.txt"));
				
				while(bw.hasNext()) {
					String temp = bw.next();
						
					if(temp.equals("*")) {
						while(bw.hasNext()) {
							String temp2 = bw.next();
								
							negative_words.add(temp2);
						}
					} else {
						positive_words.add(temp);
					}
				}
				
				removeDuplicates(positive_words);
				removeDuplicates(negative_words);
					
				bw.close();
				
				Scanner phr = new Scanner(new File("phrases.txt"));
				
				while(phr.hasNext()) {
					String[] temp = new String[2];
					
					String first = phr.next();
					String second = phr.next();
					
					if(first.equals("*")) {
						while(phr.hasNext()) {
							String[] temp2 = new String[2];
							
							String first2 = phr.next();
							String second2 = phr.next();
							
							temp2[0] = first2;
							temp2[1] = second2;
							
							negative_phrases.add(temp2);
						}
					} else {
						temp[0] = first;
						temp[1] = second;
						
						positive_phrases.add(temp);
					}
				}
				
				phr.close();
				
				Scanner negrs = new Scanner(new File("negators.txt"));
				
				while(negrs.hasNext()) {
					String word = negrs.next();
					negators.add(word);
				}
				
				negrs.close();
				
				Scanner emphrs = new Scanner(new File("emphasizers.txt"));
				
				while(emphrs.hasNext()) {
					String word = emphrs.next();
					emphasizers.add(word);
				}
				
				emphrs.close();
				
				System.out.println("Enter the path to your WordNet folder:");
				System.out.println("Ex. /Users/delilalee/Downloads/WordNet-3.0 ");
			
				String path = sc.next();
			
				path = path.trim();
			
				path = path + "/dict/";
			
				System.setProperty("wordnet.database.dir", path);
			
				database = WordNetDatabase.getFileInstance();
			}
			
			System.out.println("Enter the name of your file: ");
			
			String fileName = sc.next();
			
			fileName = fileName.trim();
			
			Scanner fr = new Scanner(new File(fileName));
			
			if(rated == 1) {
			if(fr.hasNext()) {
				
				do {
					int rating = fr.nextInt();
					ratings.add(rating);
					
					ArrayList<String> temp = new ArrayList<>();
					String index;
					
					while(!(index=fr.next()).equals("#####")) {
						index = index.replaceAll("[\\Q][(){},.=:;!?<>%\\E]", "");
						index = index.replaceAll("\\P{Print}", "");
						index = index.toLowerCase();
						index.trim();
						
						temp.add(index);
					}
					
					reviews.add(temp);
					number_of_reviews++;
					
				} while(fr.hasNext());
				
			}
			}
			else if(rated == 2) {
				if(fr.hasNext()) {
					
					do {
						ArrayList<String> temp = new ArrayList<>();
						String index;
						
						while(!(index=fr.next()).equals("#####")) {
							index = index.replaceAll("[\\Q][(){},.=:;!?<>%\\E]", "");
							index = index.replaceAll("\\P{Print}", "");
							index = index.toLowerCase();
							index.trim();
							
							temp.add(index);
						}
						
						reviews.add(temp);
						number_of_reviews++;
						
					} while(fr.hasNext());
					
				}
			}
			
			fr.close();
		}
		catch (Exception e) {
			System.out.println("ERROR: " + e);
			System.exit(0);
		}
		
		if(selection == 1) {
			if(rated == 1){
			System.out.printf("\n%18s%18s\n", "Computed Ratings:","Actual Ratings:");
			
			int correct=0, ne_po=0, ne_nu=0, po_ne=0, po_nu=0;
			
			for(int i=0; i<reviews.size(); i++) {
				String computed_rating;
				String actual_rating;
				
				int rating = ratings.get(i);
				ArrayList<String> review = reviews.get(i);
				
				int score = computeWordScore(review, positive_words, negative_words, negators, emphasizers);
				
				if(score < 0) 
					computed_rating = "negative";
				else
					computed_rating = "positive";
				
				// Remove neutral as a rating category for actual ratings
				/*if(rating<3)
					actual_rating = "negative";
				else
					actual_rating = "positive";*/
				
				if(rating < 3)
					actual_rating = "negative";
				else if(rating > 3)
					actual_rating = "positive";
				else
					actual_rating = "neutral";
				
				if(computed_rating.equals(actual_rating))
					correct++;
				else if(computed_rating.equals("negative") && actual_rating.equals("positive"))
					ne_po++;
				else if(computed_rating.equals("negative") && actual_rating.equals("neutral"))
					ne_nu++;
				else if(computed_rating.equals("positive") && actual_rating.equals("negative"))
					po_ne++;
				else
					po_nu++;
				
				System.out.printf("%3d%15s%18s\n", i+1,computed_rating,actual_rating);
			}
			
			double percentage = ((double)correct/number_of_reviews)*100;
			
			System.out.printf("\nEvaluation: %d/%d = %.2f%%\n\n",correct,number_of_reviews,percentage);
			
			if(ne_po > 0) 
				System.out.println("Computed: negative, Actual: positive = " + ne_po);
			if(ne_nu > 0) 
				System.out.println("Computed: negative, Actual: neutral = " + ne_nu);
			if(po_ne > 0) 
				System.out.println("Computed: positive, Actual: negative = " + po_ne);
			if(po_nu > 0) 
				System.out.println("Computed: positive, Actual: neutral = " + po_nu);
			}
			else if(rated == 2){
				System.out.printf("\nComputed Ratings:");
				
				for(int i=0; i<reviews.size(); i++) {
					String computed_rating;
					
					ArrayList<String> review = reviews.get(i);
					
					int score = computeWordScore(review, positive_words, negative_words, negators, emphasizers);
					
					if(score < 0) 
						computed_rating = "negative";
					else
						computed_rating = "positive";
					
					System.out.printf((i+1)+ " " + computed_rating + "\n");
				}
			}
		} 
		else {
			
			if(rated == 1) {
			System.out.printf("\n%18s%18s\n", "Computed Ratings:","Actual Ratings:");
			
			int correct=0, ne_po=0, ne_nu=0, po_ne=0, po_nu=0, nu_ne=0, nu_po=0;
			
			for(int i=0; i<reviews.size(); i++) {
				String computed_rating;
				String actual_rating;
				
				int rating = ratings.get(i);
				ArrayList<String> review = reviews.get(i);
				
				int score1 = computePhraseScore(review, positive_phrases, negative_phrases, negators, emphasizers);
				
				//removeDuplicates(review);
				
				addSynonyms(positive_words, database);
				addSynonyms(negative_words, database);
				
				addAntonyms(positive_words, negative_words, database);
				
				for(String word : positive_words) {
					if(word.equals("like")) {
						int index = positive_words.indexOf("like");
						positive_words.set(index, "" );
					}
					if(word.equals("liked")) {
						int index = positive_words.indexOf("liked");
						positive_words.set(index, "" );
					}
				}
				
				int score2 = computeLikeScore(review, negators, emphasizers);
				int score3 = computeWordScore(review, positive_words, negative_words, negators, emphasizers);
				
				int score = score1 + score2 + score3;
				
				// Remove neutral as a rating category
				/*if(score<0)
					computed_rating = "negative";
				else
					computed_rating = "positive";
				
				if(rating < 3)
					actual_rating = "negative";
				else
					actual_rating = "positive";*/
				
				if(score < 0) 
					computed_rating = "negative";
				else if(score > 0)
					computed_rating = "positive";
				else
					computed_rating = "neutral";
				
				if(rating < 3)
					actual_rating = "negative";
				else if(rating > 3)
					actual_rating = "positive";
				else
					actual_rating = "neutral";
				
				if(computed_rating.equals(actual_rating))
					correct++;
				else if(computed_rating.equals("negative") && actual_rating.equals("positive"))
					ne_po++;
				else if(computed_rating.equals("negative") && actual_rating.equals("neutral"))
					ne_nu++;
				else if(computed_rating.equals("positive") && actual_rating.equals("negative"))
					po_ne++;
				else if(computed_rating.equals("positive") && actual_rating.equals("neutral"))
					po_nu++;
				else if(computed_rating.equals("neutral") && actual_rating.equals("positive"))
					nu_po++;
				else
					nu_ne++;
					
				System.out.printf("%3d%15s%18s\n", i+1,computed_rating,actual_rating);
			}
			
			double percentage = ((double)correct/number_of_reviews)*100;
			
			System.out.printf("\nEvaluation: %d/%d = %.2f%%\n\n",correct,number_of_reviews,percentage);
			
			if(ne_po > 0) 
				System.out.println("Computed: negative, Actual: positive = " + ne_po);
			if(ne_nu > 0) 
				System.out.println("Computed: negative, Actual: neutral = " + ne_nu);
			if(po_ne > 0) 
				System.out.println("Computed: positive, Actual: negative = " + po_ne);
			if(po_nu > 0) 
				System.out.println("Computed: positive, Actual: neutral = " + po_nu);
			if(nu_po > 0) 
				System.out.println("Computed: neutral, Actual: positive = " + nu_po);
			if(nu_ne > 0) 
				System.out.println("Computed: neutral, Actual: negative = " + nu_ne);
		}
		else if(rated == 2) 
		{
			System.out.printf("\nComputed Ratings:");
			
			for(int i=0; i<reviews.size(); i++) {
				String computed_rating;
				
				ArrayList<String> review = reviews.get(i);
				
				int score1 = computePhraseScore(review, positive_phrases, negative_phrases, negators, emphasizers);
				
				addSynonyms(positive_words, database);
				addSynonyms(negative_words, database);
				
				addAntonyms(positive_words, negative_words, database);
				
				for(String word : positive_words) {
					if(word.equals("like")) {
						int index = positive_words.indexOf("like");
						positive_words.set(index, "" );
					}
					if(word.equals("liked")) {
						int index = positive_words.indexOf("liked");
						positive_words.set(index, "" );
					}
				}
				
				int score2 = computeLikeScore(review, negators, emphasizers);
				int score3 = computeWordScore(review, positive_words, negative_words, negators, emphasizers);
				
				int score = score1 + score2 + score3;
				
				if(score < 0) 
					computed_rating = "negative";
				else if(score > 0)
					computed_rating = "positive";
				else
					computed_rating = "neutral";
					
				System.out.print((i+1)+" "+computed_rating+"\n");
			}
		
		}	
		}

		
		sc.close();
	}
}
