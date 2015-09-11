import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
       
        // TODO
        /*
         * 1. Divide each sentence into a list of words using delimiters provided in the “delimiters” variable.
         * 2. Make all the tokens lowercase and remove any tailing and leading spaces.
         * 3. Ignore all common words provided in the “stopWordsArray” variable.
         * 4. Keep track of word frequencies.
         * 5. Sort the list by frequency in a descending order. If two words have the same number count, use the lexigraphy. 
         * For example, the following is a sorted list: {(Orange, 3), (Apple, 2), (Banana, 2)}
         * 6. Return the top 20 items from the sorted list as a String Array.
         */
		 
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
                /*
                 * 1. Divide each sentence into a list of words using delimiters provided in the “delimiters” variable.
                 * --------------------------------------------------------------------------------------------------------------------------------------
                 * 
                 * "StringTokenizer is a legacy class that is retained for compatibility reasons although its use is discouraged in new code. 
                 * "It is recommended that anyone seeking this functionality use the split method of String or the java.util.regex package instead."
                 * 
                 * ref: http://docs.oracle.com/javase/7/docs/api/java/util/StringTokenizer.html 
                */
                words = line.split(pattern);
                
                if (words.length > 0) {
                	for (String word : words) {
                        // 2. Make all the tokens lowercase and remove any tailing and leading spaces.
                        word = word.toLowerCase().trim();
                        hasStopWord = false;

                        // 3. Ignore all common words provided in the “stopWordsArray” variable.
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
            
            // removing all indexes of  wordList that wasn't in indexes variable...
            for (Integer idx : getIndexes()) {
                String word = wordList.get(idx);
                
                if (word != null)
                    wordListCleaned.add(word);
            }

            // 4. Keep track of word frequencies.
            for (String word: wordListCleaned) {
                wordCount = 0;

                if (wordFrequencies.containsKey(word)) {
                    wordCount = wordFrequencies.get(word);

                    if (wordCount > 0)
                        wordCount++;
                }

                wordFrequencies.put(word, wordCount);
            }

            // 5. Sort the list by frequency in a descending order. If two words have the same number count, use the lexigraphy.
            List<String, Integer> list = new ArrayList<Entry<String, Integer>>(wordFrequenciese);
            Collections.sort(list,
        		// by value
        		new Comparator<Entry<String, Integer>>() {
        			
        			@Override
        			public int compare(Entry<String,Integer> o1, Entry<String,Integer> o2) {
        				return (o1.getValue()).compareTo(o2.getValue());
        			}
        			
				}.reversed(),
				
				// by key (word)
				new Comparator<Entry<String, Integer>>() {
					
					@Override
					public int compare(Entry<String,Integer> o1, Entry<String,Integer> o2) {
						return (o1.getKey()).compareTo(o2.getKey());
					}
				}
			);
            
            ret = wordFrequencies.keySet().toArray(String[]);
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