import java.util.*;

import tester.Tester;

//CS 2510, Assignment 9
//Assignment 9 - problem 1
//McKenzie Camaryn
//mckenzie.cam
//Beckwith Louisa
//beckwith.l

//---------------------------------------------------------------------------------

//A class that defines a new permutation code, as well as methods for encoding
//and decoding of the messages that use this code.

public class PermutationCode {
  // The original list of characters to be encoded
  ArrayList<Character> alphabet = 
      new ArrayList<Character>(Arrays.asList(
          'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
          'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
          't', 'u', 'v', 'w', 'x', 'y', 'z'));

  ArrayList<Character> code = new ArrayList<Character>(26);

  // A random number generator
  Random rand = new Random();

  // Create a new instance of the encoder/decoder with a new permutation code 
  PermutationCode() {
    this.code = this.initEncoder();
  }

  // Create a new instance of the encoder/decoder with the given code 
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }

  // Initialize the encoding permutation of the characters
  ArrayList<Character> initEncoder() {
    ArrayList<Character> key = new ArrayList<Character>();

    ArrayList<Character> alphabetCopy = new ArrayList<Character>(Arrays.asList(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
        't', 'u', 'v', 'w', 'x', 'y', 'z'));

    int index;

    for (int i=0; i < 26; i = i + 1) {
      index = rand.nextInt(alphabetCopy.size());
      key.add(alphabetCopy.get(index));
      alphabetCopy.remove(index);   
    }
    return key;
  }

  // produce an encoded String from the given String
  String encode(String source) {
    StringBuilder encodedList = new StringBuilder(source.length());

    for (int i=0; i < source.length(); i = i + 1) {
      Character preEncode = source.charAt(i);
      Character postEncode = this.code.get(this.alphabet.indexOf(preEncode));
      encodedList.append(postEncode);
    }
    return encodedList.toString();
  }

  // produce a decoded String from the given String)
  String decode(String message) {
    StringBuilder decodedList = new StringBuilder(message.length());

    for (int i=0; i < message.length(); i = i + 1) {
      Character preDecode = message.charAt(i);
      Character postDecode = this.alphabet.get(this.code.indexOf(preDecode));
      
      decodedList.append(postDecode);
    }

    return decodedList.toString();
  }
}
//------------------------------------------------------------------------

class ExamplesCode {

  PermutationCode code1 = new PermutationCode();

  // tests for encode
  boolean testEncode(Tester t) {
    return t.checkExpect(code1.encode("hello").length(), 5)
        && t.checkExpect(code1.decode(code1.encode("hello")), "hello")
        && t.checkExpect(code1.decode("aolks").length(), 5);
    }



}
















