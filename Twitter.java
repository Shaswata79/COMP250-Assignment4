import java.util.ArrayList;
import java.util.LinkedList;

public class Twitter {

    private MyHashTable<String,ArrayList<Tweet>> tweetsDate;
    private MyHashTable<String,Tweet> tweetsAuthor;
    private MyHashTable<String,String> stopWords;
    private ArrayList<Tweet> TweetList;

    // O(n+m) where n is the number of tweets, and m the number of stopWords
    public Twitter(ArrayList<Tweet> tweets, ArrayList<String> stopWords) {
        this.tweetsDate = new MyHashTable<String, ArrayList<Tweet>>(tweets.size());
        this.tweetsAuthor = new MyHashTable<String, Tweet>(tweets.size());
        this.stopWords = new MyHashTable<String, String>(stopWords.size());
        this.TweetList = new ArrayList<Tweet>(tweets.size());

        for(int i = 0; i < tweets.size(); i++){
            String DateKey = tweets.get(i).getDateAndTime().substring(0,10);
            ArrayList<Tweet> ThisDateList = new ArrayList<>();
            if(this.tweetsDate.get(DateKey) != null){
                ThisDateList = this.tweetsDate.get(DateKey);
            }
            ThisDateList.add(tweets.get(i));
            this.tweetsDate.put(DateKey, ThisDateList);
            this.tweetsAuthor.put(tweets.get(i).getAuthor(), tweets.get(i));
            this.TweetList.add(i, tweets.get(i));
        }

        for (int j = 0; j < stopWords.size(); j++){
            this.stopWords.put(stopWords.get(j), stopWords.get(j));
        }
    }


    /**
     * Add Tweet t to this Twitter
     * O(1)
     */
    public void addTweet(Tweet t) {
        try{
            String key = t.getDateAndTime().substring(0,10);
            ArrayList<Tweet> ThisDateList = this.tweetsDate.get(key);
            ThisDateList.add(t);
            this.tweetsDate.put(key, ThisDateList);
            key = t.getAuthor();
            this.tweetsAuthor.put(key, t);
        } catch (NullPointerException e) {
            System.out.println("Tweet input cannot be null!");
        }

    }


    /**
     * Search this Twitter for the latest Tweet of a given author.
     * If there are no tweets from the given author, then the 
     * method returns null. 
     * O(1)  
     */
    public Tweet latestTweetByAuthor(String author) {
        int AuthorIndex = this.tweetsAuthor.hashFunction(author);
        LinkedList<HashPair<String,Tweet>> ThisBucket = this.tweetsAuthor.getBuckets().get(AuthorIndex);
        int i = ThisBucket.size() - 1;
        while(!ThisBucket.get(i).getKey().equals(author)){
            i--;
        }
        return ThisBucket.get(i).getValue();
    }


    /**
     * Search this Twitter for Tweets by `date' and return an 
     * ArrayList of all such Tweets. If there are no tweets on 
     * the given date, then the method returns null.
     * O(1)
     */
    public ArrayList<Tweet> tweetsByDate(String date) {
        ArrayList<Tweet> ThisDateList = this.tweetsDate.get(date);
        return ThisDateList;
    }

    /**
     * Returns an ArrayList of words (that are not stop words!) that
     * appear in the tweets. The words should be ordered from most
     * frequent to least frequent by counting in how many tweet messages
     * the words appear. Note that if a word appears more than once
     * in the same tweet, it should be counted only once.
     */
    public ArrayList<String> trendingTopics() {
        ArrayList<ArrayList<String>> WordList = new ArrayList<>();
        MyHashTable<String,Integer> topics = new MyHashTable<>(1);
        String word = null;
        for (int i = 0; i < this.TweetList.size(); i++){     // O(n)
            WordList.add(i, getWords(this.TweetList.get(i).getMessage()));
        }
        for (int j = 0; j < WordList.size(); j++){
            for (int k = 0; k < WordList.get(j).size(); k++){
                word = WordList.get(j).get(k);
                if(this.stopWords.get(word.toLowerCase()) == null){
                    if (topics.get(word.toLowerCase()) == null){
                        topics.put(word.toLowerCase(), 1);
                    }
                    else{
                        int frequency = topics.get(word.toLowerCase());
                        frequency++;
                        topics.remove(word.toLowerCase());
                        topics.put(word.toLowerCase(), frequency);
                    }
                }
            }
        }
        return topics.fastSort(topics);
    }



    /**
     * An helper method you can use to obtain an ArrayList of words from a 
     * String, separating them based on apostrophes and space characters. 
     * All character that are not letters from the English alphabet are ignored. 
     */
    private static ArrayList<String> getWords(String msg) {
        msg = msg.replace('\'', ' ');
        String[] words = msg.split(" ");
        boolean present = false;
        ArrayList<String> wordsList = new ArrayList<String>(words.length);
        for (int i=0; i<words.length; i++) {
            String w = "";
            for (int j=0; j< words[i].length(); j++) {
                char c = words[i].charAt(j);
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
                    w += c;

            }
            present = false;
            for(int j = 0; j < wordsList.size(); j++){
                if(wordsList.get(j).equals(w.toLowerCase())){
                    present = true;
                }
            }
            if(!present){
                wordsList.add(w.toLowerCase());
            }
        }
        return wordsList;
    }



}