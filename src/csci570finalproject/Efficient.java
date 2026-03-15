package csci570finalproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        Basic.GeneratedStrings recursionResult = recursion(strings, 0, m, 0, n, solutions);
        result.x = recursionResult.x;
        result.y = recursionResult.y;
        double afterUsedMem = Basic.getMemoryInKB();
        double endTime = Basic.getTimeInMilliseconds();
        result.totalMemoryUsage = afterUsedMem - beforeUsedMem;
        result.totalTime = endTime - startTime;

        return result;
    }

    public static Basic.GeneratedStrings recursion(Basic.GeneratedStrings strings, int startX, int endX, int startY,
            int endY, int[][] solutions) {
        if (endX - startX < 2 || endY - startY <= 2) {
            return basesolution(strings, startX, endX, startY, endY, solutions);
        }
        String x = strings.x.substring(startX, endX);
        String y = strings.y.substring(startY, endY);
        int m = x.length();
        int n = y.length();
        int msplit = m / 2;
        int lStartX = 0;
        int lEndX = msplit;
        int rStartX = msplit + 1;
        int rEndX = m+1;
        int ySplit = 1;
        int minCost = solutions[msplit][1];
        for (int j = 2; j <= n; j++) {
            if (solutions[msplit][j] < minCost) {
                minCost = solutions[msplit][j];
                ySplit = j;
            }
        }
       
        int lStartY = 0;
        int lEndY = ySplit;
        int rStartY = ySplit + 1;
        int rEndY = n+1;
        Basic.GeneratedStrings leftString = recursion(strings, lStartX, lEndX, lStartY, lEndY, solutions);
        Basic.GeneratedStrings rightString = ySplit == n ? new Basic.GeneratedStrings() : recursion(strings, rStartX, rEndX, rStartY, rEndY, solutions);
        return new Basic.GeneratedStrings(leftString.x + rightString.x, leftString.y + rightString.y);
    }

    public static Basic.GeneratedStrings basesolution(Basic.GeneratedStrings strings, int startX, int endX, int startY,
            int endY, int[][] solutions) {
        String x = strings.x.substring(startX, endX);
        String y = strings.y.substring(startY, endY);
        int m = x.length();
        int n = y.length();

        int i = m;
        int j = n;
        int maxLen = m + n;
        char[] alightmentX = new char[maxLen];
        char[] alightmentY = new char[maxLen];
        int startPos = maxLen;
        while (i > 0 || j > 0) {
            char xChar = '_';
            char yChar = '_';
            int iminus = i - 1;
            int jminus = j - 1;
            char xMinusChar = iminus < 0 ? '_' : x.charAt(iminus);
            char yMinusChar = jminus < 0 ? '_' : y.charAt(jminus);
            int solutionsi = i + startX;
            int solutionsj = j + startY;
            if ((i == 1 && j == 1) || (iminus >= 0 && jminus >= 0
                    && solutions[solutionsi][solutionsj] == solutions[solutionsi - 1][solutionsj - 1]
                            + Basic.getAlphas(xMinusChar, yMinusChar))) {
                xChar = xMinusChar;
                yChar = yMinusChar;
                --i;
                --j;
            } else if (iminus < 0 || (iminus == 0 && jminus <= 1) || (jminus >= 0
                    && solutions[solutionsi][solutionsj] == solutions[solutionsi][solutionsj - 1] + Basic.DELTA)) {
                xChar = '_';
                yChar = yMinusChar;
                --j;
            } else if (jminus < 0 || (jminus == 0 && iminus <= 1) || (iminus >= 0
                    && solutions[solutionsi][solutionsj] == solutions[solutionsi - 1][solutionsj] + Basic.DELTA)) {
                xChar = xMinusChar;
                yChar = '_';
                --i;
            }
            startPos--;
            alightmentX[startPos] = xChar;
            alightmentY[startPos] = yChar;
            if (i == 0 || j == 0) {
                if (i == 0) {
                    while (j > 0) {
                        startPos--;
                        alightmentX[startPos] = '_';
                        alightmentY[startPos] = y.charAt(--j);
                    }
                } else {
                    while (i > 0) {
                        startPos--;
                        alightmentX[startPos] = x.charAt(--i);
                        alightmentY[startPos] = '_';
                    }
                }
            }
        }

        return new Basic.GeneratedStrings(new String(alightmentX, startPos, maxLen - startPos),
                new String(alightmentY, startPos, maxLen - startPos));

    }

}
