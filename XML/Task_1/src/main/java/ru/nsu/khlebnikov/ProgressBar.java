package ru.nsu.khlebnikov;

import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ProgressBar {
    private static long totalLines = -1;
    private static final long barPartsNumber = 20;
    public static long getLines(String filePath) {
        try (Stream<String> stream = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {
            totalLines = stream.count();
        } catch (IOException e) {
            System.err.println("Can't open file");
        }
        return totalLines;
    }

    private static long getCurrentLine(XMLEvent event) {
        return event.getLocation().getLineNumber();
    }

    public static void printProgress(XMLEvent event) {
        if (totalLines == -1) {
            System.err.println("Empty file");
            return;
        }
        long currentLine = getCurrentLine(event);
        long percentage = Math.floorDiv(currentLine * 100, totalLines);

        if (percentage >= 0) {
            System.out.print("Progress: " + percentage + "% ");
            System.out.print("[");
            long parts = Math.floorDiv(barPartsNumber * percentage, 100);
            for (int i = 0; i < parts; i++) {
                System.out.print("|");
            }
            for (int i = 0; i < barPartsNumber - parts; i++) {
                System.out.print("_");
            }
            System.out.print("] (" + currentLine + "/" + totalLines + ") \r");
        } else {
            System.out.println("Parsing done!");
        }
    }
}
