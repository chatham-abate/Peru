package org.perudevteam.misc;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public final class SeqHelpers {
    public static Stream<Character> fileUnchecked(String filename) {
        return file(filename).get().map(Try::get);
    }

    public static Try<Stream<Try<Character>>> file(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            return Try.success(charStream(br));
        } catch (FileNotFoundException e) {
            return Try.failure(e);
        }
    }

    public static Stream<Try<Character>> charStream(BufferedReader br) {
        try {
            int c = br.read();
            return c == -1 ? Stream.empty()
                    : Stream.cons(Try.success((char)c), () -> charStream(br));
        } catch (IOException e) {
            return Stream.of(Try.failure(e));
        }
    }

    public static void validateTable(Seq<? extends Seq<?>> table) {
        Objects.requireNonNull(table);
        int rowSize = -1;
        for (Seq<?> row: table) {
            Objects.requireNonNull(row);
            row.forEach(Objects::requireNonNull);

            if (rowSize == -1) {
                rowSize = row.length();
            } else if (row.length() != rowSize) {
                throw new IllegalArgumentException("Table does not have consistent row length.");
            }
        }
    }

    /**
     * Generate a grid string representation for the given grid of strings.
     * NOTE, this works best with arrays. With normal lists, this will be slow.
     */
    public static String gridString(Seq<? extends String> rowLabels, Seq<? extends String> colLabels,
                                    Seq<? extends Seq<? extends String>> grid) {
        Objects.requireNonNull(rowLabels);
        Objects.requireNonNull(colLabels);
        Objects.requireNonNull(grid);
        grid.forEach(Objects::requireNonNull);

        final int rows = grid.length();

        // All rows must have the same number of cells.
        // There must be at least one row and one column.
        if (rows == 0) {
            throw new IllegalArgumentException("At least 1 row required.");
        }

        final int columns = grid.get(0).length();

        if (columns == 0) {
            throw new IllegalArgumentException("At least 1 column required.");
        }

        if (rowLabels.length() != rows || colLabels.length() != columns) {
            throw new IllegalArgumentException("Incorrect number of labels given.");
        }

        int maxRowLabelWidth = 0;
        Array<Integer> maxColumnWidths = Array.fill(columns, 0);

        // Set initial maxColumnWidths.
        for (int c = 0; c < columns; c++) {
            maxColumnWidths = maxColumnWidths.update(c, colLabels.get(c).length());
        }

        // Check cell widths as well as row label widths.
        for (int r = 0; r < rows; r++) {
            // First let's check the row label.
            int rowLabelWidth = rowLabels.get(r).length();
            if (rowLabelWidth > maxRowLabelWidth) {
                maxRowLabelWidth = rowLabelWidth;
            }

            // Now we must check all the columns.
            Seq<? extends String> row = grid.get(r);
            if (row.length() != columns) {
                throw new IllegalArgumentException("All rows need equal length.");
            }

            for (int c = 0; c < columns; c++) {
                int cellWidth = row.get(c).length();
                if (cellWidth > maxColumnWidths.get(c)) {
                    maxColumnWidths = maxColumnWidths.update(c, cellWidth);
                }
            }
        }

        final int PAD = 2;

        // Now we need the string formats the row labels and for every column.
        String rowLabelFormat = "%" + (maxRowLabelWidth + PAD) + "s";
        Array<String> columnFormats = maxColumnWidths.map(w -> "%" + (w + PAD) + "s");

        // Now to generate the grid string. (Starting with corner cell).
        StringBuilder gridStr = new StringBuilder().append(String.format(rowLabelFormat, ""));

        // Now add all corner labels.
        for (int c = 0; c < columns; c++) {
            gridStr.append(String.format(columnFormats.get(c), colLabels.get(c)));
        }

        gridStr.append('\n');

        // Now for all the rows...
        for (int r = 0; r < rows; r++) {
            // Start with row label.
            gridStr.append(String.format(rowLabelFormat, rowLabels.get(r)));

            // Now all the cells.
            for (int c = 0; c < columns; c++) {
                gridStr.append(String.format(columnFormats.get(c), grid.get(r).get(c)));
            }

            gridStr.append('\n');
        }

        return gridStr.toString();
    }
}
