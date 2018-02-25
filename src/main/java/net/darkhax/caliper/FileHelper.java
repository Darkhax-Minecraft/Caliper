package net.darkhax.caliper;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class FileHelper {

    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static final String NEW_LINE = System.lineSeparator();
    public static final String NEW_PARAGRAPH = NEW_LINE + NEW_LINE;
    public static final String ANONYMOUS_INFO = " This data is anonymous, and is not automatically submitted to any online service.";

    public static void writeInfoBlock (FileWriter writer, int level, String title, String info) throws IOException {

        writeInfoBlock(writer, level, title, info, false);
    }

    public static void writeInfoBlock (FileWriter writer, int level, String title, String info, boolean anonymous) throws IOException {

        writer.append(StringUtils.repeat('#', Math.max(level, 1)) + " " + title);
        writer.append(NEW_PARAGRAPH);
        writer.append(WordUtils.wrap(info + (anonymous ? ANONYMOUS_INFO : ""), 80));
        writer.append(NEW_PARAGRAPH);
    }
}