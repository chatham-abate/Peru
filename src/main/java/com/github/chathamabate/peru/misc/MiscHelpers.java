package com.github.chathamabate.peru.misc;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Set of miscellaneous static helper functions.
 */
public final class MiscHelpers {

    /**
     * Private constructor should never be used.
     */
    private MiscHelpers() {

    }

    /**
     * Retrieve the <b>Stream</b> of characters contained within a file.
     * This function is <i>unchecked</i> since it assumes the given file exists
     * and that there will be no errors while reading it.
     *
     * @param filename Filename of the file.
     * @return The <b>Stream</b> characters contained in the file.
     */
    public static Stream<Character> fileUnchecked(String filename) {
        return file(filename).get().map(Try::get);
    }

    /**
     * Retrieve the <b>Stream</b> of characters contained within a file.
     * This function is <i>checked</i> in that if there is an error finding the file,
     * a <b>Failure</b> is returned.
     * <br>
     * Additionally, if there is an error while reading a specific character from the file,
     * a <b>Failure</b> will be placed in the output <b>Stream</b>.
     *
     * @param filename Filename of the file.
     * @return A <b>Try</b> of a <b>Stream</b> containing <b>Try</b>s of characters.
     */
    public static Try<Stream<Try<Character>>> file(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            return Try.success(charStream(br));
        } catch (FileNotFoundException e) {
            return Try.failure(e);
        }
    }

    /**
     * Given a buffered reader, this function lazily extracts characters out of the <b>BufferedReader</b>
     * as <b>Try</b>s.
     *
     * @param br The <b>BufferedReader</b>.
     * @return A <b>Stream</b> of <b>Try</b>s.
     */
    public static Stream<Try<Character>> charStream(BufferedReader br) {
        try {
            int c = br.read();
            return c == -1 ? Stream.empty()
                    : Stream.cons(Try.success((char)c), () -> charStream(br));
        } catch (IOException e) {
            return Stream.of(Try.failure(e));
        }
    }

    /**
     * Given a <b>Seq</b> of <b>Seq</b>s, throw an error if any null values are contained
     * within the <b>Seq</b>.
     *
     * @param nestedSeq The nested <b>Seq</b>.
     */
    public static void requireNonNullNestedSeq(Seq<? extends Seq<?>> nestedSeq) {
        Objects.requireNonNull(nestedSeq);
        for (Seq<?> row: nestedSeq) {
            Objects.requireNonNull(row);
            row.forEach(Objects::requireNonNull);
        }
    }

    /**
     * Throw an error if the given <b>Map</b> is null or if it contains any null keys or values.
     *
     * @param map The <b>Map</b>.
     * @param <K> The key type of the <b>Map</b>.
     * @param <V> The value type of the <b>Map</b>.
     */
    public static <K, V> void requireNonNullMap(Map<K, V> map) {
        Objects.requireNonNull(map);
        map.keySet().forEach(Objects::requireNonNull);
        map.values().forEach(Objects::requireNonNull);
    }

    /**
     * Build a string representation of a given table of values.
     *
     * @param rowLabels The labels for each row of the table.
     * @param colLabels The labels for each column of the table.
     * @param grid The grid of values.
     * @return A string representation of the grid.
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

    /**
     * Match on a <b>Try</b> given some type of <b>Throwable</b>.
     * <br>
     * If the <b>Try</b> is a <b>Success</b>, map it's internal value.
     * <br>
     * If the <b>Try</b> is a <b>Failure</b> of the given <b>Throwable</b> type,
     * map the internal cause.
     * <br>
     * Otherwise the given <b>Try</b> must be a <b>Failure</b> of some other <b>Throwable</b> tyoe.
     * In this case throw an error.
     *
     * @param exClass The <b>Throwable Class</b>
     * @param tryValue The <b>Try</b>.
     * @param valueMap The internal value mapping function.
     * @param exMap The internal <b>Throwable</b> mapping function.
     * @param <T> The internal type of the given <b>Try</b>.
     * @param <U> The internal type of the resulting <b>Try</b>.
     * @param <X> The internal <b>Throwable</b> type.
     * @return The mapped <b>Try</b>.
     */
    public static <T, U, X extends Throwable> Try<U> throwMatch(Class<X> exClass, Try<? extends T> tryValue,
                                                      Function1<? super T, ? extends U> valueMap,
                                                      Function1<? super X, ? extends X> exMap) {
        if (tryValue.isSuccess()) {
            return tryValue.map(valueMap);
        }

        if (tryValue.getCause().getClass() != exClass) {
            throw new IllegalArgumentException("Unexpected Throwable given.");
        }

        return Try.failure(exMap.apply(exClass.cast(tryValue.getCause())));
    }

    /**
     * Map the cause of some <b>Try</b>.
     * <br>
     * If the <b>Try</b> is a <b>Failure</b>, map its internal cause.
     * <br>
     * Otherwise, return the <b>Try</b> as is.
     *
     * @param tryValue The given <b>Try</b>.
     * @param exMap The internal cause mapping function.
     * @param <T> The type of the given <b>Try</b>.
     * @return The mapped <b>Try</b>.
     */
    public static <T> Try<T>
    mapCause(Try<? extends T> tryValue, Function1<? super Throwable, ? extends Throwable> exMap) {
        return tryValue.isSuccess() ? Try.narrow(tryValue) : Try.failure(exMap.apply(tryValue.getCause()));
    }

    /**
     * Map the cause of some <b>Try</b> if it is not already of some given <b>Throwable</b> type.
     *
     * @param exClass The <b>Throwable Class</b>.
     * @param tryValue The given <b>Try</b>.
     * @param exMap The <b>Throwable</b> mapper.
     * @param <T> The internal value type of the given <b>Try</b>.
     * @param <X> The type of the given <b>Throwable Class</b>.
     * @return The mapped <b>Try</b>.
     */
    public static <T, X extends Throwable> Try<T>
    mapCauseIfNeeded(Class<X> exClass, Try<? extends T> tryValue, Function1<? super Throwable, ? extends X> exMap) {
        return tryValue.isSuccess() || tryValue.getCause().getClass() == exClass
                ? Try.narrow(tryValue)
                : Try.failure(exMap.apply(tryValue.getCause()));
    }
}
