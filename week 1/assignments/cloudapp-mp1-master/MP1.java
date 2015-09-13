import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
       
    	BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
        String pattern = "[" + Pattern.quote(delimiters) + "]";
        String line = null;
        String[] words = null;
        
        ArrayList<String> wordList = new ArrayList<String>();
        ArrayList<String> wordListCleaned = new ArrayList<String>();
        
        Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
        Boolean hasStopWord;
        int wordCount;
        
        try {
            while ((line = reader.readLine()) != null) {
                words = line.split(pattern);
                
                if (words.length > 0) {
                	for (String word : words) {
                        word = word.toLowerCase().trim();
                        hasStopWord = false;

                        if (word.length() > 0) {
                            for (String stopWord : stopWordsArray) {
                                if (word.equals(stopWord)) {
                                    hasStopWord = true;
                                    break;
                                }
                            }

                            if (!hasStopWord)
                                wordList.add(word);
                        }
                    }
                }
            }
            
            reader.close();
            
            for (Integer idx : getIndexes()) {
                String word = wordList.get(idx);
                
                if (word != null)
                    wordListCleaned.add(word);
            }
            
            for (String word: wordListCleaned) {
                wordCount = 1;

                if (wordFrequencies.containsKey(word)) {
                    wordCount = wordFrequencies.get(word).intValue();

                    if (wordCount > 0)
                        wordCount++;
                }

                wordFrequencies.put(word, wordCount);
            }
            
            List<WordCounter> list = new ArrayList<WordCounter>();
            
            for (Map.Entry<String, Integer> wordMap : wordFrequencies.entrySet()) {
				WordCounter wc = new WordCounter();
				
				wc.setWord(wordMap.getKey());
				wc.setCounter(wordMap.getValue());
				
				list.add(wc);				
			}
            
            Collections.sort(list, WordComparator.descendingOnlyValue(WordComparator.getComparator(WordComparator.VALUE_SORT_DESCENDING, WordComparator.WORD_SORT)));
            
            for (int i = 0; i < ret.length; i++) {
				ret[i] = list.get(i).getWord();
			}
        }
        catch (IOException ioex) {
            System.err.format("IOException: %s%n", ioex);
        }

        return ret;
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}

final class WordCounter {
	private String word;
	private int counter;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + counter;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordCounter other = (WordCounter) obj;
		if (counter != other.counter)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
}

enum WordComparator implements Comparator<WordCounter> {
	WORD_SORT {
		@Override
		public int compare(WordCounter o1, WordCounter o2) {
			return o1.getWord().compareTo(o2.getWord());			
		}
	},
	VALUE_SORT_DESCENDING {
		@Override
		public int compare(WordCounter o1, WordCounter o2) {
			if (o1.getCounter() > o2.getCounter()) {
				return -1;
			}
			else if (o1.getCounter() < o2.getCounter()) {
				return 1;
			}
			
			return 0;
		}
	};
	
	public static Comparator<WordCounter> descendingOnlyValue(final Comparator<WordCounter> wordCounter) {
		return new Comparator<WordCounter>() {
			
			@Override
			public int compare(WordCounter o1, WordCounter o2) {
				return wordCounter.compare(o1, o2);				
			}
		};
	}
	
	public static Comparator<WordCounter> getComparator(final WordComparator... comparators) {
		return new Comparator<WordCounter>() {
			
			@Override
			public int compare(WordCounter o1, WordCounter o2) {
				for (WordComparator comparator : comparators) {
					int result = comparator.compare(o1, o2);
					
					if (result != 0)
						return result; 
				}
				
				return 0;				
			}
		};
	}
}