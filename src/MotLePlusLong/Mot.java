package MotLePlusLong;

import java.math.BigInteger;
import java.util.*;

public class Mot {
    private String voyelles = new String();
    private String consonnes = new String();

    private final int nbTirage = 10;

    private Random rnd = new Random();

    public Mot() {
        // Allocation of vocals frequency on 248 slots
        voyelles = "";
        voyelles += "a".repeat(48);
        voyelles += "e".repeat(92);
        voyelles += "i".repeat(43);
        voyelles += "o".repeat(33);
        voyelles += "u".repeat(29);
        voyelles += "y".repeat(3);
        // Allocation of consonants frequency on 248 slots
        consonnes = "";
        consonnes += "b".repeat(7);
        consonnes += "c".repeat(19);
        consonnes += "d".repeat(21);
        consonnes += "f".repeat(7);
        consonnes += "g".repeat(7);
        consonnes += "h".repeat(7);
        consonnes += "j".repeat(2);
        consonnes += "k".repeat(2);
        consonnes += "l".repeat(28);
        consonnes += "m".repeat(15);
        consonnes += "n".repeat(36);
        consonnes += "p".repeat(14);
        consonnes += "q".repeat(4);
        consonnes += "r".repeat(35);
        consonnes += "s".repeat(37);
        consonnes += "t".repeat(34);
        consonnes += "v".repeat(7);
        consonnes += "w".repeat(1);
        consonnes += "x".repeat(3);
        consonnes += "z".repeat(1);
    }

    // Returns a random tirage
    // Each plaque can only be chosen once
    public String GetPlaques(int nbVoyelles) {
        // Randomly select a list of indexes (can only be chosen once)
        List<Integer> chosenVoyelles = new ArrayList<Integer>();
        List<Integer> chosenConsonnes = new ArrayList<Integer>();

        int maxVoyelles  = voyelles.length();
        int maxConsonnes = consonnes.length();

        int nbConsonnes = nbTirage - nbVoyelles;

        // Choose randomly the letters
        String tirage = "";
        // Start with voyelles
        int nbChosen = 0;
        while (nbChosen < nbVoyelles) {
            int p = rnd.nextInt(maxVoyelles);
            if (!chosenVoyelles.contains(p)) {
                chosenVoyelles.add(p);
                nbChosen++;
            }
        }
        // Then the consonnes
        nbChosen = 0;
        while (nbChosen < nbConsonnes) {
            int p = rnd.nextInt(maxConsonnes);
            if (!chosenConsonnes.contains(p)) {
                chosenConsonnes.add(p);
                nbChosen++;
            }
        }

        // Compose the tirage, by mixing voyelles and consonnes
        while (chosenVoyelles.size() + chosenConsonnes.size() > 0) {
            if (chosenVoyelles.size() > 0 &&
                chosenConsonnes.size() > 0) {
                // Use a range of 100: 0 -> 49 and 50 -> 99
                int p = rnd.nextInt(100);
                if (p < 50) {
                    tirage += voyelles.charAt(chosenVoyelles.get(0));
                    chosenVoyelles.remove(0);
                }
                else {
                    tirage += consonnes.charAt(chosenConsonnes.get(0));
                    chosenConsonnes.remove(0);
                }
            }
            else if (chosenVoyelles.size() > 0) {
                tirage += voyelles.charAt(chosenVoyelles.get(0));
                chosenVoyelles.remove(0);
            }
            else {
                tirage += consonnes.charAt(chosenConsonnes.get(0));
                chosenConsonnes.remove(0);
            }
        }

        return tirage;
    }

    private String removeLetter(String text, int rank) {
        String result = text.substring(0, rank) + text.substring(rank+1);
        return result;
    }

    public Solution SolveTirage(Hashtable<BigInteger, List<String>> dico, Solution solution) {
        // Stop searching for smaller words if something is already found
        if (solution.current.length() < solution.bestLen) {
            // Remaining stub of letters is less than best yet found: nothing to hope
            return null;
        }

        // Retrieve combination from dictionary
        BigInteger idx = StrictDictionary.calcIndex(solution.current);
        List<String> mots = dico.get(idx);
        if (mots != null) {
            // Combination found in the dictionary: return it !
            Solution found = new Solution();
            found.current = solution.current;
            found.bestLen = solution.current.length();
            if (found.bestLen > solution.bestLen) {
                found.best = mots;
            }
            else if (found.bestLen == solution.bestLen) {
                // Solution with same length has been found: add them, if not yet in
                found.best = solution.best;
                for (String w : mots) {
                    if (!found.best.contains(w)) {
                        found.best.add(w);
                    }
                }
            }
            return found;
        }
        else {
            // No matching combination in dictionary: search recursively with one letter less
            int len = solution.current.length();
            Solution bestSol = new Solution();
            for (int i=0; i<len; i++) {
                Solution iter = new Solution();
                iter.current = removeLetter(solution.current, i);
                Solution found = SolveTirage(dico, iter);
                if (found != null) {
                    if (found.bestLen > bestSol.bestLen) {
                        // A better solution has been found: replace it
                        bestSol.bestLen = found.bestLen;
                        bestSol.best = found.best;
                    }
                    // else if (found.bestLen > bestSol.bestLen) {
                    else if (found.bestLen == bestSol.bestLen &&
                             bestSol.best.size() < 10) {
                        // Solution with same length has been found: add them, if not yet in
                        for (String w : found.best) {
                            if (!bestSol.best.contains(w)) {
                                bestSol.best.add(w);
                            }
                        }
                    }
                }
            }
            if (bestSol.bestLen > 0) {
                // A set of solution has been found: return it
                return bestSol;
            }
        }
        // Nothing has been found on this recursion branch
        return null;
    }
}