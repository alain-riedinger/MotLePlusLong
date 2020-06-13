package MotLePlusLong;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.commons.io.*;

// Class for a complete dictionary
// - at most 10 letter words
// - respecting le Mot le Plus Long rules
// - unaccented, with out separators
public class StrictDictionary {
    final static int MAX_LEN = 10;

    public static void parseUnmunchedDico(String dicoPath, int lineStart, int lineEnd) {
        Hashtable<String, String> dico = new Hashtable<String, String>();

        try {
            // Unmunched dictionary file to be opened for reading  
            FileInputStream fis = new FileInputStream(dicoPath);       
            Scanner sc = new Scanner(fis);

            // Strict list of words, respecting all the rules
            String outPath = FilenameUtils.getBaseName(dicoPath) + "-strict." + FilenameUtils.getExtension(dicoPath);
            File out = new File(outPath);
            FileOutputStream fos = new FileOutputStream(out);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            // Loop through all the lines
            int line = 1;
            while (sc.hasNextLine()) {
                if (lineStart > 0 && line < lineStart) {
                    // The first lines are skipped
                    sc.nextLine();
                }
                else {
                    if (lineEnd > 0 && lineEnd < line) {
                        // Processing is over: exit the loop without parsing last lines
                        break;
                    }

                    // This line must be processed
                    String parsed = parseLine(sc.nextLine());
                    if (parsed != null) {
                        if (!dico.containsKey(parsed)) {
                            dico.put(parsed, "present");

                            // Parsed word is written to strict dictionary
                            bw.write(parsed);
                            bw.newLine();
                            bw.flush();
                        }
                    }
                }
                line++;
            }
            sc.close();
            bw.close();
        }
        catch (Exception ex) {
        }
    }

    private static String parseLine(String line) {
        String parsedLine = "";

        int len = 0;
        // Check the line char by char
        for (int i=0; i<line.length(); i++) {
            char c = line.charAt(i);
            if (c == '/') {
                // Process derived words: accepted, but not added and finishes parsing
                // Must be checked outside of the "switch" to "break" the for loop
                break;
            }

            switch (c) {
                // Process accented characters: accepted and added unaccented
                case 'a':
                case 'à':
                case 'â':
                case 'ä':
                    parsedLine += 'a';
                    len++;
                    break;
                case 'e':
                case 'é':
                case 'è':
                case 'ê':
                case 'ë':
                    parsedLine += 'e';
                    len++;
                    break;
                case 'i':
                case 'î':
                case 'ï':
                    parsedLine += 'i';
                    len++;
                    break;
                case 'o':
                case 'ô':
                case 'ö':
                    parsedLine += 'o';
                    len++;
                    break;
                case 'u':
                case 'ù':
                case 'û':
                case 'ü':
                    parsedLine += 'u';
                    len++;
                    break;
                // Process modified characters: accepted and added
                case 'ç':
                    parsedLine += 'c';
                    len++;
                    break;
                // Process plain characters: accepted and added
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    parsedLine += c;
                    len++;
                    break;
                // Process compound word separator: accepted, but not added
                case '-':
                    break;
                // Process any other character: refused and finishes parsing
                default:
                    return null;
            }
        }
        if (parsedLine.length() > MAX_LEN) {
            // Word is too long: no need to store it
            return null;
        }
        return parsedLine;
    }

    // Computes the index of a given word
    // <Nb><Mask of 13 bytes>
    //   <Nb>, nb of characters of the word
    //   <Mask of 13 bytes>, occurences of each of 26 letters
    //                       - ordered after
    //                       - half bytes, recomposed
    public static BigInteger calcIndex(String word) {
        // Count the occurences of letters
        byte[] counts = new byte[26];
        for (int i=0; i<word.length(); i++) {
            int letter = (byte)(word.charAt(i)) - (byte)('a');
            counts[letter]++;
        }

        // Array of bytes for work
        byte[] rawIdx = new byte[14];
        rawIdx[0] = (byte) word.length();
        for (int r=0; r<13; r++) {
            rawIdx[1+r] = (byte)(((int)(counts[2*r]) << 4) + ((int)(counts[2*r+1])));
        }

        // Returned BigInteger, with hashed index
        BigInteger idx = new BigInteger(rawIdx);
        return idx;
    }

    // Load the dictionary with the index approach:
    // <index> --> List matching words
    public static Hashtable<BigInteger, List<String>> loadStrictDico(String dicoPath) {
        Hashtable<BigInteger, List<String>> dico = new Hashtable<BigInteger, List<String>>();
        try {
            // Strict processed dictionary file to be opened for reading
            FileInputStream fis = new FileInputStream(dicoPath);       
            Scanner sc = new Scanner(fis);

            // Loop through all the words
            while (sc.hasNextLine()) {
                String word = sc.nextLine();
                BigInteger idx = calcIndex(word);
                if (!dico.containsKey(idx)) {
                    // Add a new key / list to the dictionary if not yet existing
                    dico.put(idx, new ArrayList<String>());
                }
                List<String> tmp = dico.get(idx);
                tmp.add(word);
                dico.put(idx, tmp);
            }
            sc.close();
        }
        catch (Exception ex) {
        }

        return dico;
    }
}