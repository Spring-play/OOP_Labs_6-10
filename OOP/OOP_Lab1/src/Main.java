import java.util.Arrays;
import java.util.Random;

/**
 * @author Kudrin Pavel 3311
 * */

/**
 * This program demonstrates sorting an array of integers using the bubble sort algorithm.
 * It generates an array of 10 random integers, sorts them in both ascending and descending order,
 * and prints the results.
 */
public class Main {

    /**
     * The main method for this program. It initializes an array of 10 random integers,
     * sorts the array in ascending and descending order using bubble sort, and prints the results.
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        int[] numbers = new int[10];  // Create an array to hold 10 integers
        Random random = new Random(); // Create an instance of Random for generating random numbers

        // Populate the array with random integers between 0 and 99
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(100);
        }

        // Print the original array
        System.out.println("Original Array: " + Arrays.toString(numbers));

        // Sort the array in ascending order and display the sorted result
        sortBubbleSort(numbers, true);
        System.out.println("Sorted Array (ascending): " + Arrays.toString(numbers));

        // Sort the array in descending order and display the sorted result
        sortBubbleSort(numbers, false);
        System.out.println("Sorted Array (descending): " + Arrays.toString(numbers));
    }

    /**
     * Sorts an integer array using the bubble sort algorithm.
     * The sorting order is specified by the 'ascending' parameter.
     * The algorithm repeatedly steps through the list, compares adjacent elements,
     * and swaps them if they are in the wrong order. The pass through the list is repeated
     * until the array is sorted.
     *
     * @param array The array of integers that needs to be sorted
     * @param ascending If true, sorts the array in ascending order; if false, sorts in descending order
     */
    public static void sortBubbleSort(int[] array, boolean ascending) {
        int length = array.length; // Get the length of the array
        boolean swapped; // Flag to track if any swapping occurred

        // Iterate through the array
        for (int i = 0; i < length - 1; i++) {
            swapped = false; // Reset the flag at the beginning of each pass

            // Perform comparisons and swaps
            for (int j = 0; j < length - 1 - i; j++) {
                // Determine if the adjacent elements need to be swapped based on the order
                if ((array[j] > array[j + 1] && ascending) || (array[j] < array[j + 1] && !ascending)) {
                    // Swap elements
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true; // Set the flag to true indicating a swap happened
                }
            }

            // If no elements were swapped, the array is already sorted
            if (!swapped) {
                break;
            }
        }
    }
}
