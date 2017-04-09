/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    //word list for storing dictionary words
    private ArrayList<String> wordList;
    //word set for storing fast lookup dictionary words
    private HashSet<String> wordSet;
    //hash map for storing lists of anagrams
    private HashMap<String, ArrayList<String>> lettersToWord;
    //hash map for storing anagrams of a particular length
    private HashMap<Integer, ArrayList<String>> sizeToWords;
    //member for storing current word length
    private int wordLength;

    /*
        this app give the user a word from the dictionary and asks
        them to give another word, an anagram of the first with the
        inclusion of an additional letter. the user entered word
        cannot contain the given word in order. the game increases
        in difficulty with each turn, giving a new word of a length
        increased by one. each word given must have at least 5 anagrams
        to make the game fair.
     */

    //constructor for storing words
    public AnagramDictionary(Reader reader) throws IOException {
        wordList = new ArrayList<String>();
        BufferedReader in = new BufferedReader(reader);
        String line;

        wordSet = new HashSet<String>();
        lettersToWord = new HashMap<String, ArrayList<String>>();
        sizeToWords = new HashMap<Integer, ArrayList<String>>();
        //starting word length is default word length
        wordLength = DEFAULT_WORD_LENGTH;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            //add word from dictionary file into list of valid words
            wordList.add(word);
            //add word form dictionary fill into quick lookup
            wordSet.add(word);

            //if particular length is not already stored add key and
            //initialize new list
            if(!sizeToWords.containsKey(word.length()))
                sizeToWords.put(word.length(), new ArrayList<String>());
            //add word to particular word's list
            sizeToWords.get(word.length()).add(word);

            //if anagram's sorted key is not already stored add key
            //and initialize new list
            if(!lettersToWord.containsKey(sortLetters(word)))
                lettersToWord.put(sortLetters(word), new ArrayList<String>());
            //add word to anagram key's list
            lettersToWord.get(sortLetters(word)).add(word);
        }
    }

    //method for evaluating the validity of a word
    public boolean isGoodWord(String word, String base) {
        //if the word is valid and does not contain original
        //word return true
        if(wordSet.contains(word))
                if(!word.contains(base)) return true;
        return false;
    }

    //method for finding the anagrams of a word
    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        //for each word in the dictionary, if it's length is the
        //same as the target and it's sorted key is the same as
        //the target, it is an anagram so return true
        for(String s : wordList) {
            if(s.length() == targetWord.length()) {
                if(sortLetters(targetWord).compareTo(sortLetters(s)) == 0)
                    result.add(s);
            }
        }
        return result;
    }

    //method for finding the anagrams of a word plus one letter
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        //for each letter added to the word, if it's sorted key matches
        //that of another dictionary word's sorted key then add copy its
        //list of anagrams and return
        for(char ii = 'a'; ii <= 'z'; ii++) {
            if(lettersToWord.containsKey(sortLetters(word + ii))) {
                for(String s : lettersToWord.get(sortLetters(sortLetters(word + ii))))
                    result.add(s);
            }
        }
        return result;
    }

    //method to get a good word to start off with
    public String pickGoodStarterWord() {
        //new list of the words that match the current word length
        ArrayList<String> words = sizeToWords.get(wordLength);
        //find a word to start with... if it has the minimum number of
        //possible anagrams and has the length of the current word length
        //difficulty, then it's a good starter word. If we're not at the
        //most difficult level, the max word length, then increment
        //the word length to make the game harder
        while(true) {
            int ii = random.nextInt(words.size());
            if (getAnagramsWithOneMoreLetter(words.get(ii)).size() >= MIN_NUM_ANAGRAMS) {
                   if(wordLength != MAX_WORD_LENGTH) wordLength++;
                    return words.get(ii);
            }
        }
    }

    //helper method to sort words' letters
    public String sortLetters(String str) {
        String ret;
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        ret = new String(chars);
        return ret;
    }
}
