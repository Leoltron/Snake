package ru.leoltron.snake.util;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    public static String findFreeFile(String prefix, String suffix) {
        int i = 0;
        String filename = prefix;
        while (Files.exists(Paths.get(filename + suffix))) {
            filename = prefix + '(' + i + ')';
            i++;
        }
        return filename + suffix;
    }

    public static String getTodayDateString() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public static void log(String logFilename, String message, PrintStream primaryStream) {
//        try {
//            new File(logFilename).getParentFile().mkdirs();
//            val path = Paths.get(logFilename);
//            Files.write(path, Collections.singletonList(message), StandardCharsets.UTF_8,
//                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        primaryStream.println(message);
    }

}
