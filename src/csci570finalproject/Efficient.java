package csci570finalproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Efficient {
    private static int DELTA = 30;
    public static final int[][] ALPHAS = {
            {0, 110, 48, 94},
            {110, 0, 118, 48},
            {48, 118, 0, 110},
            {94, 48, 110, 0}
        };
    
    public static int getAlphas(String x, String y) {
       int i = getIndex(x);
       int j = getIndex(y);
       return ALPHAS[i][j];
    }

    public static int getIndex(String x) {
        switch(x) {
        case "A":
            return 0;
        case "C":
            return 1;
        case "G":
            return 2;
        case "T":
            return 3;
        default: 
            throw new RuntimeException("invalid char " + x);
        }
    }
    
    public static void main(String[] args) {
        // Ensure that at least two arguments were provided
        if (args.length < 2) {
            System.out.println("Please provide exactly two command-line arguments.");
        }
        
        String inputFilePath = args[0];
        String outputFilePath = args[1];

        Path path = Paths.get(inputFilePath);

        // Check if the file exists
        if (!Files.exists(path)) {
            System.out.println(inputFilePath + " doesn't exist.");
            return;
        }
        if (!isValidFilePath(outputFilePath)) {
            return;
        }
        
        Path outputPath = Paths.get(inputFilePath);
        if (!Files.exists(outputPath)) {
            try {
                Files.createFile(outputPath);
            } catch (IOException e) {
                throw new RuntimeException("Error to create file  " + outputFilePath);
            }
        }

        GeneratedStrings strings = getGeneratedStrings(inputFilePath);
        double beforeUsedMem = getMemoryInKB();
        double startTime = getTimeInMilliseconds();

        //Object alignment = efficientSolution(inputFilePath, strY, delta, alpha);
        double afterUsedMem = getMemoryInKB();
        double endTime = getTimeInMilliseconds();
        double totalUsage = afterUsedMem - beforeUsedMem;
        double totalTime = endTime - startTime;

        output(outputFilePath, 1, strings, totalTime, totalUsage);

    }
    
    public static void output(String outputFilePath, double cost, GeneratedStrings strings, double totalTime, double totalMemoryUsage) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, false))){
            bw.write(cost + System.lineSeparator());
            bw.write(strings.x + System.lineSeparator());
            bw.write(strings.y + System.lineSeparator());
            //write total time
            bw.write(totalTime + System.lineSeparator());
          //write total Memory
                bw.write(totalMemoryUsage + System.lineSeparator());
  
            } catch (IOException e) {
                System.err.println("An I/O error occurred: " + e.getMessage());
            return;
        }
    }
    
    public static boolean validStringACGT(String input) {
        Pattern pattern = Pattern.compile("^[ACGT]+$");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();  
    }

    public static GeneratedStrings getGeneratedStrings(String inputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            GeneratedStrings result = new GeneratedStrings();
            String startString = line = reader.readLine();
            if (isNumeric(line) || !validStringACGT(line)) {
                throw new RuntimeException("First line is invalid in input file  " + inputFilePath);
            }
            
            // Read lines one by one until the end of the file (line is null)
            while ((line = reader.readLine()) != null) {
                if (!isNumeric(line)) {
                    if (!validStringACGT(line)) {
                        throw new RuntimeException("Second String is invalid in input file  " + inputFilePath);
                    }
                    //reset tring 
                    result.x = startString;
                    startString = line; 
                } else {
                    int index = Integer.parseInt(line);
                    if (index >= (startString.length()-1)) {
                        startString += startString;
                    } else {
                        startString = startString.substring(0, index+1) + startString +  startString.substring(index+1);
                    }
                }
            } 
            result.y = startString;
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file from " + inputFilePath + e.getMessage());
        }
    }
    
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) { // Check for null or empty strings
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidFilePath(String path) {
        try {
            Paths.get(path);
            return true;
        } catch (InvalidPathException e) {
            return false;
        } catch (NullPointerException e) {
            // Path string cannot be null
            return false;
        }
    }

    private static double getMemoryInKB() {
        double total = Runtime.getRuntime().totalMemory();
        return (total - Runtime.getRuntime().freeMemory()) / 1e3;
    }

    private static double getTimeInMilliseconds() {
        return System.nanoTime() / 10e6;
    }
    
    public static class GeneratedStrings {
        public String x;
        public String y;
    }
}
