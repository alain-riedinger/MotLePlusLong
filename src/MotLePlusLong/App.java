package MotLePlusLong;

import java.math.BigInteger;
import java.util.*;
import java.util.Hashtable;

public class App {
    private static String dicoPath = "";
    private static int lineStart = 0;
    private static int lineEnd = 0;

    private static void help() {
        System.out.println();
        System.out.println("+------------------+");
        System.out.println("| Mot Le Plus Long |");
        System.out.println("+------------------+");
        System.out.println("A game that consists in finding a word with a set of 10 random letters");
        System.out.println();
        System.out.println("Syntax:");
        System.out.println("-------");
        System.out.println("MotLePlusLong [dico <dico> --start <ln> --end <ln>]");
        System.out.println("- to play a game");
        System.out.println("  no options needed, the application will do");
        System.out.println();
        System.out.println("- to generate the adapted dictionary from an 'unmunch' seed");
        System.out.println("  dico");
        System.out.println("  <dico>, path to the 'unmunch' dictionary file");
        System.out.println("  --start <ln>, optional, line of parsing start, preceding are skipped");
        System.out.println("  --end <ln>, optional, line of parsing end, following are skipped");
        System.out.println();
    }

    private static void parseArgs(String[] args) {
        int i = 0;
        while (i < args.length) {
            switch (args[i].toLowerCase()) {
                case "dico": {
                    if (i+1 < args.length) {
                        App.dicoPath = args[i+1];
                    }
                    else {
                        help();
                        return;
                    }
                    i += 2;
                    break;
                }
                case "--start": {
                    if (i+1 < args.length) {
                        try {
                            App.lineStart = Integer.parseInt(args[i+1]);
                        }
                        catch (Exception ex) {
                            help();
                            return;
                        }
                    }
                    else {
                        help();
                        return;
                    }
                    i += 2;
                    break;
                }
                case "--end": {
                    if (i+1 < args.length) {
                        try {
                            App.lineEnd = Integer.parseInt(args[i+1]);
                        }
                        catch (Exception ex) {
                            help();
                            return;
                        }
                    }
                    else {
                        help();
                        return;
                    }
                    i += 2;
                    break;
                }
                default: {
                    help();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Parse the arguments of the application
        parseArgs(args);

        if (!dicoPath.isEmpty()) {
            // Construction of the dictionary
            StrictDictionary.parseUnmunchedDico(dicoPath, lineStart, lineEnd);
        }
        else {
            // The plain game
            Scanner in = new Scanner(System.in);

            // Load the dictionary
            Hashtable<BigInteger, List<String>> dico = StrictDictionary.loadStrictDico("fr-mlpl-flat-strict.txt");

            ////////////////////////////////////////////////////////
            // For unit tests
            // Mot mot = new Mot();
            // // String test = "abaissante";
            // String test = "wabaissant";
            // Solution solution = new Solution();
            // solution.current = test;
            // Solution found = mot.SolveTirage(dico, solution);
            ////////////////////////////////////////////////////////
    
            // Default number of voyelles
            int nbVoyelles = 4;

            while (true) {
                displayTitle();
                // Launch a game with 30 seconds of think time
                nbVoyelles = newGame(in, dico, nbVoyelles, 30, 30);
                prompt(in, "Want another game ?");
                clearScreen();
            }
        }
    }

    private static int newGame(Scanner in, Hashtable<BigInteger, List<String>> dico, int previous, int chars, int seconds) {
        int nbVoyelles = promptVoyelles(in, previous);
        Mot mot = new Mot();
        String plaques = mot.GetPlaques(nbVoyelles);
        String s = " ";
        for (char t : plaques.toUpperCase().toCharArray()) {
            s += String.format(" %c ", t);
        }
        System.out.println(" +----------------------------+");
        System.out.println(s);
        System.out.println(" +----------------------------+");

        // Wait for some seconds of think time
        Chrono.Countup(chars, seconds);

        prompt(in, "Want a solution ?");

        // Initialize the recursive search root structure
        Solution solution = new Solution();
        solution.current = plaques;
        Solution found = mot.SolveTirage(dico, solution);
        if (found != null) {
            System.out.println("Best words found of: " + found.bestLen + " letters");
            for (String w : found.best) {
                System.out.println(w.toUpperCase());
            }
        }
        else {
            System.out.println(" No acceptable word has been found...");
        }
        System.out.println();

        // To avoid typing this number in the next game
        return nbVoyelles;        
    }

    private static void clearScreen() {  
        try {
            new ProcessBuilder("cmd", "/c", "cls")
            .inheritIO()
            .start()
            .waitFor();
        }
        catch (Exception ex) {
        }
    }

    private static void prompt(Scanner in, String msg) {
        System.out.println(msg + " (press Enter)");
        in.nextLine();
    }

    private static int promptVoyelles(Scanner in, int previous) {
        int nb = previous;
        System.out.println("How many voyelles ? [" + previous + "] (press Enter)");
        String s = in.nextLine();
        if (!s.isEmpty()) {
            nb = Integer.parseInt(s);
        }
        return nb;
    }

   private static void displayTitle() {
        System.out.println("  +---------------------------+");
        System.out.println("  |  Le Mot le plus Long !    |");
        System.out.println("  +---------------------------+");
    }
}