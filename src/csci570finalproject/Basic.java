package csci570finalproject;

public class Basic {

    public static void main(String[] args) {
        // Ensure that at least two arguments were provided
        if (args.length >= 2) {
            String firstString = args[0];
            String secondString = args[1];

            System.out.println("First string: " + firstString);
            System.out.println("Second string: " + secondString);
            
            // You can then combine them
            String combinedString = firstString + " " + secondString;
            System.out.println("Combined string: " + combinedString);
            double beforeUsedMem=getMemoryInKB(); 
            double startTime = getTimeInMilliseconds();
            
         //   Object alignment = basicSolution(firstString, secondString, delta, alpha);
            double afterUsedMem = getMemoryInKB(); 
            double endTime = getTimeInMilliseconds();
            double totalUsage = afterUsedMem-beforeUsedMem; 
            double totalTime = endTime - startTime;
        } else {
            System.out.println("Please provide exactly two command-line arguments.");
        }
       
    }
    
    private static double getMemoryInKB() {
        double total = Runtime.getRuntime().totalMemory();
        return (total - Runtime.getRuntime().freeMemory()) / 1e3;
    }

    private static double getTimeInMilliseconds() {
        return System.nanoTime() / 10e6;
    }

}
