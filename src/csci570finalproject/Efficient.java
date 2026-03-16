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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Efficient {
    public static int DELTA = 30;
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

        Path outputPath = Paths.get(outputFilePath);
        if (!Files.exists(outputPath)) {
            try {
                Files.createFile(outputPath);
            } catch (IOException e) {
                throw new RuntimeException("Error to create file  " + outputFilePath);
            }
        }

        GeneratedStrings strings = getGeneratedStrings(inputFilePath);

        SolutionResult alignment = EfficientSolution(strings);

        output(outputFilePath, alignment);

    }

    public static SolutionResult EfficientSolution(GeneratedStrings strings) {
        SolutionResult result = new SolutionResult();
        double beforeUsedMem = getMemoryInKB();
        double startTime = getTimeInMilliseconds();
    
        SolutionResult recursionResult = DivideAndConquesAllignment(strings.x, strings.y);
        result.x = recursionResult.x;
        result.y = recursionResult.y;
        
        int cost = 0;
        for (int i = 0; i < result.x.length(); i++) {
            char a = result.x.charAt(i);
            char b = result.y.charAt(i);
            if (a == '_' || b == '_') cost += DELTA;
            else cost += getAlphas(a, b);
        }
        result.cost = cost;
        
        double afterUsedMem = getMemoryInKB();
        double endTime = getTimeInMilliseconds();
        result.totalMemoryUsage = result.totalMemoryUsage + afterUsedMem - beforeUsedMem;
        result.totalTime = endTime - startTime;

        return result;
    }
    
    public static SolutionResult DivideAndConquesAllignment(String x, String y) {
        double beforeUsedMem = getMemoryInKB();
        int m = x.length();
        int n = y.length();
        if (m == 0) {
            SolutionResult res = new SolutionResult();
            res.x = "_".repeat(n);
            res.y = y;
            return res;
        }
        if (n == 0) {
            SolutionResult res = new SolutionResult();
            res.x = x;
            res.y = "_".repeat(m);
            return res;
        }
        if (m <= 2 || n <=2) {
            return basesolution(new GeneratedStrings(x, y));
        }
        
        int split = n/2;
        int[] forward = spaceEfficientAlignment(x, y.substring(0, split));
        int[] backward = spaceEfficientAlignmentBackward(x, y.substring(split, n));
        int minIdx = 0;
        int minCost = forward[0] + backward[0];
        for (int i = 1; i <= m; i++) {
            int sum = forward[i] + backward[i];
            if (sum < minCost) {
                minCost = sum;
                minIdx = i;
            }
        }

        SolutionResult left = DivideAndConquesAllignment(x.substring(0, minIdx), y.substring(0, split));
        SolutionResult right = DivideAndConquesAllignment(x.substring(minIdx), y.substring(split));  System.out.println("x: " + x + ", y: " + y);
        // System.out.println("forward: " + Arrays.toString(forward));
        // System.out.println("backward: " + Arrays.toString(backward));
        //  System.out.println("minIdx: " + minIdx + ", split: " + split);
        // System.out.println("x left: " + x.substring(0, minIdx) + ", y left: " + y.substring(0, split));
        // System.out.println("x right: " + x.substring(minIdx) + ", y right: " + y.substring(split));
        SolutionResult result = new SolutionResult();
        result.x = left.x + right.x;
        result.y = left.y + right.y;
        result.totalMemoryUsage = left.totalMemoryUsage + right.totalMemoryUsage + getMemoryInKB() - beforeUsedMem;
        return result;
    }

    public static int[] spaceEfficientAlignmentBackward(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        // Initialize prev for aligning x with empty y
        for (int i = 0; i <= m; i++) {
            prev[i] = (m - i) * DELTA;
        }
        // Process y from right to left
        for (int j = n - 1; j >= 0; j--) {
            curr[m] = (n - j) * DELTA;
            char yChar = y.charAt(j);
            for (int i = m - 1; i >= 0; i--) {
                char xChar = x.charAt(i);
                int costDiag = prev[i + 1] + getAlphas(xChar, yChar);
                int costDown = prev[i] + DELTA;
                int costRight = curr[i + 1] + DELTA;
                curr[i] = Math.min(Math.min(costDiag, costDown), costRight);
            }
            // Copy curr to prev for next iteration
            for (int i = 0; i <= m; i++) {
                prev[i] = curr[i];
            }
        }
        return curr;
    }
    public static int[] spaceEfficientAlignment(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        for (int i = 0; i <= m; i++) {
            prev[i] = i * DELTA;
        }
        
        for (int j = 1; j <=n; j++) {
            curr[0] = j * DELTA;
            char yChar = y.charAt(j-1);
            for (int i = 1; i <= m; i++) {
                char xChar = x.charAt(i - 1);
                int costDiag = prev[i - 1] + getAlphas(xChar, yChar);
                int costUp = prev[i] + DELTA;
                int costLeft = curr[i-1] + DELTA;
                curr[i]  = Math.min(Math.min(costDiag, costUp), costLeft);
            }
            for (int i = 0; i <= m; i++) {
                prev[i] = curr[i] ;
            }
        }
       
        return curr;        
    }
    

 
    public static SolutionResult basesolution(GeneratedStrings strings) {
        SolutionResult result = new SolutionResult();
        double beforeUsedMem = getMemoryInKB();
        int m = strings.x.length();
        int n = strings.y.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 0; i<= m; i++) {
            dp[i][0] = i * DELTA;
        }
        for (int j = 0; j<= n; j++) {
            dp[0][j] = j * DELTA;
        }
        
        for (int i = 1; i <= m; i++)  {
            for (int j = 1; j <= n; j++) {
                int match = dp[i-1][j-1] + getAlphas(strings.x.charAt(i-1), strings.y.charAt(j-1));
                int case2 = dp[i-1][j] + DELTA;
                int case3 = dp[i][j-1] + DELTA;
                dp[i][j] = Math.min(Math.min(match, case2), case3);
            }
        }
        result.cost = dp[m][n];
        int i = m;
        int j = n;
        StringBuilder alignX = new StringBuilder();
        StringBuilder alignY = new StringBuilder();
  
        while (i > 0 && j > 0 ) {
            int score = dp[i][j];
            int scoreDiag = dp[i - 1][j - 1];
            int scoreUp = dp[i - 1][j];
            if (score == scoreDiag + getAlphas(strings.x.charAt(i - 1), strings.y.charAt(j - 1))) {
                alignX.append(strings.x.charAt(i - 1));
                alignY.append(strings.y.charAt(j - 1));
 
                i--;
                j--;
            } else  if (score == scoreUp + DELTA) {
                alignX.append(strings.x.charAt(i - 1));
                alignY.append('_');
                i--;
            } else {
                alignX.append('_');
                alignY.append(strings.y.charAt(j - 1));
                j--;
            } 
        }
        while (j > 0) {
            alignX.append('_');
            alignY.append(strings.y.charAt(--j)); 
        }

        while (i > 0) {
            alignX.append(strings.x.charAt(--i));
            alignY.append('_');
        }
        result.x = alignX.reverse().toString();
        result.y = alignY.reverse().toString();
        double afterUsedMem = getMemoryInKB();
 
        result.totalMemoryUsage = afterUsedMem - beforeUsedMem;

        
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

    public static double getMemoryInKB() {
        double total = Runtime.getRuntime().totalMemory();
        return (total - Runtime.getRuntime().freeMemory()) / 1e3;
    }

    public static double getTimeInMilliseconds() {
        return System.nanoTime() / 10e6;
    }
    
    public static class GeneratedStrings {
        public GeneratedStrings(String x, String y) {
           this.x = x;
           this.y = y;
        }
        public GeneratedStrings() {
         
        }
        public String x;
        public String y;
    }
    
    public static class SolutionResult{
        public String x;
        public String y;    
        public int cost;
        double totalTime;
        double totalMemoryUsage;
        
    }

}
