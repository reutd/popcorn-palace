package com.att.tdp.popcorn_palace.util;

public class InputUtils {

    /**
     * Normalizes the input string by:
     * - Trimming leading and trailing spaces.
     * - Replacing multiple internal whitespace characters with a single space.
     * - Capitalizing the first letter of each word.
     *
     * @param input the input string
     * @return the normalized string, or null if input is null
     */
    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }

        // Remove leading/trailing spaces and collapse multiple spaces to one.
        String trimmed = input.trim().replaceAll("\\s+", " ");

        // Split into words.
        String[] words = trimmed.split(" ");
        StringBuilder sb = new StringBuilder();

        // Capitalize the first letter of every word.
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        // Return the normalized string.
        return sb.toString().trim();
    }
}
