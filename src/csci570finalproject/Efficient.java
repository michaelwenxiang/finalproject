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
    
    public static int getAlphas(char x, char y) {
       int i = getIndex(x);
       int j = getIndex(y);
       return ALPHAS[i][j];
    }

    public static int getIndex(char x) {
        switch(x) {
        case 'A':
            return 0;
        case 'C':
            return 1;
        case 'G':
            return 2;
        case 'T':
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
    
        SolutionResult alignment = basesolution(strings);

        output(outputFilePath, alignment);

    }
    
    public static SolutionResult basesolution(GeneratedStrings strings) {
        SolutionResult result = new SolutionResult();
        double beforeUsedMem = getMemoryInKB();
        double startTime = getTimeInMilliseconds();
        int m = strings.x.length();
        int n = strings.y.length();
        int[][] solutons = new int[m+1][n+1];
        for (int i = 0; i<= m; i++) {
            solutons[i][0] = i * DELTA;
        }
        for (int j = 0; j<= n; j++) {
            solutons[0][j] = j * DELTA;
        }
        
        for (int i = 1; i <= m; i++)  {
            for (int j = 1; j <= n; j++) {
                char a = strings.x.charAt(i-1);
                char b = strings.y.charAt(j-1);
                int case1 = solutons[i-1][j-1] + getAlphas(a, b);
                int case2 = solutons[i-1][j] + DELTA;
                int case3 = solutons[i][j-1] + DELTA;
                solutons[i][j] = Math.min(Math.min(case1, case2), case3);
            }
        }
        result.cost = solutons[m][n];
        int i = m;
        int j = n;
        int maxLen = m+n;
        char[] alightmentX = new char[maxLen];
        char[] alightmentY = new char[maxLen];
        int startPos = maxLen;
        while (i > 1 || j > 1 ) {
            char xChar;
            char yChar;
            if (i < 0 ) {
                xChar = '_';
                yChar = strings.y.charAt(--j);
            } else if (j < 0 ) {
                xChar = strings.x.charAt(--i);
                yChar =  '_' ;
            } else if (solutons[i][j] == solutons[i-1][j] + DELTA) {
                xChar =strings.x.charAt(--i);
                yChar = '_';
            } else if (solutons[i][j] == solutons[i][j-1] + DELTA) {            
                xChar =  '_' ;
                yChar = strings.y.charAt(--j);
            } else {
                xChar = strings.x.charAt(--i);
                yChar = strings.y.charAt(--j);
            }
            startPos--;
            alightmentX[startPos] = xChar;
            alightmentY[startPos] = yChar;         
        }
        result.x = new String(alightmentX, startPos, maxLen - startPos);
        result.y = new String(alightmentY, startPos, maxLen - startPos);
        double afterUsedMem = getMemoryInKB();
        double endTime = getTimeInMilliseconds();
        result.totalMemoryUsage = afterUsedMem - beforeUsedMem;
        result.totalTime = endTime - startTime;
        
        return result;
       
    }
    
    public static void output(String outputFilePath,  SolutionResult alignment) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, false))){
            bw.write(alignment.cost + System.lineSeparator());
            bw.write(alignment.x + System.lineSeparator());
            bw.write(alignment.y + System.lineSeparator());
            //write total time
            bw.write(alignment.totalTime + System.lineSeparator());
           //write total Memory
            bw.write(alignment.totalMemoryUsage + System.lineSeparator());
  
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
    
    public static class SolutionResult{
        public String x;
        public String y;    
        public double cost;
        double totalTime;
        double totalMemoryUsage;
        
    }
}
