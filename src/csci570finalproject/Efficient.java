package csci570finalproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import csci570finalproject.Basic.SolutionResult;

public class Efficient {

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
        if (!Basic.isValidFilePath(outputFilePath)) {
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

        Basic.GeneratedStrings strings = Basic.getGeneratedStrings(inputFilePath);

        Basic.SolutionResult alignment = EfficientSolution(strings);

        Basic.output(outputFilePath, alignment);

    }

    public static Basic.SolutionResult EfficientSolution(Basic.GeneratedStrings strings) {
        Basic.SolutionResult result = new Basic.SolutionResult();
        double beforeUsedMem = Basic.getMemoryInKB();
        double startTime = Basic.getTimeInMilliseconds();
        int m = strings.x.length();
        int n = strings.y.length();
        int[][] solutions = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            solutions[i][0] = i * Basic.DELTA;
        }
        for (int j = 0; j <= n; j++) {
            solutions[0][j] = j * Basic.DELTA;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char a = strings.x.charAt(i - 1);
                char b = strings.y.charAt(j - 1);
                int case1 = solutions[i - 1][j - 1] + Basic.getAlphas(a, b);
                int case2 = solutions[i - 1][j] + Basic.DELTA;
                int case3 = solutions[i][j - 1] + Basic.DELTA;
                solutions[i][j] = Math.min(Math.min(case1, case2), case3);
            }
        }
        result.cost = solutions[m][n];

        SolutionResult recursionResult = DivideAndConquesAllignment(strings.x, strings.y);
        result.x = recursionResult.x;
        result.y = recursionResult.y;
        double afterUsedMem = Basic.getMemoryInKB();
        double endTime = Basic.getTimeInMilliseconds();
        result.totalMemoryUsage = afterUsedMem - beforeUsedMem;
        result.totalTime = endTime - startTime;

        return result;
    }
    
    public static Basic.SolutionResult DivideAndConquesAllignment(String x, String y) {
        int m = x.length();
        int n = y.length();
        if (m <= 2 || n <=2) {
            return Basic.basesolution(new Basic.GeneratedStrings(x, y));
        }
        Basic.SolutionResult result = new Basic.SolutionResult();
        int split = n/2;
        int[][] forward = spaceEfficientAlignment(x, y.substring(0, split));
        int[][] backward = backwardSpaceEfficientAlignment(x, y.substring(split+1, n));
        int lowYPos = 0;
        int lowCost = forward[0][1] + backward[0][1];
        for (int i = 1; i <=m; i++) {
            int sum = forward[i][1] + backward[i][1];
            if (lowCost < sum) {
                lowCost = sum;
                lowYPos = i;
            }
        }
        Basic.SolutionResult left = DivideAndConquesAllignment(x.substring(0, lowYPos), y.substring(0, split));
        Basic.SolutionResult right = DivideAndConquesAllignment(x.substring(lowYPos+1, m), y.substring(split+1, n));
                             
        result.x = left.x + x.charAt(lowYPos)+ right.x;
        result.y =left.y + y.charAt(split)+ right.y;
        return result;
    }

    public static int[][] spaceEfficientAlignment(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[][] alignment = new int[m+1][2];
        for (int i = 0; i <= m; i++) {
            alignment[i][0] = i * Basic.DELTA;
        }
        for (int j = 1; j <n; j++) {
            alignment[0][1] = j * Basic.DELTA;
            char a = y.charAt(j);
            for (int i = 1; i < m; i++) {
                char b = x.charAt(i);
                int case1 = alignment[i - 1][0] + Basic.getAlphas(a, b);
                int case2 = alignment[i - 1][1] + Basic.DELTA;
                int case3 = alignment[i][0] + Basic.DELTA;
                alignment[i][1] = Math.min(Math.min(case1, case2), case3);
            }
        }
        for (int i = 0; i < m; i++) {
            alignment[i][0] = alignment[i][1];
        }
        return alignment;        
    }
    
    public static int[][] backwardSpaceEfficientAlignment(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[][] alignment = new int[m+1][2];
        for (int i = m; i >=0; i--) {
            alignment[i][0] = i * Basic.DELTA;
        }
        for (int j = n-1; j >=0; j--) {
            alignment[n][1] = (n-j) * Basic.DELTA;
            char a = y.charAt(j);
            for (int i = m-1; i >= 0; i--) {
                char b = x.charAt(i);
                int case1 = alignment[i + 1][0] + Basic.getAlphas(a, b);
                int case2 = alignment[i + 1][1] + Basic.DELTA;
                int case3 = alignment[i][0] + Basic.DELTA;
                alignment[i][1] = Math.min(Math.min(case1, case2), case3);
            }
        }
        for (int i = 0; i <= m; i++) {
            alignment[i][0] = alignment[i][1];
        }
        return alignment;        
    }
 

}
