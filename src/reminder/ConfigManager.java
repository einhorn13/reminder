package reminder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".reminder_app";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "settings.json";

    public static Config loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return new Config(
                Config.DEFAULT_SHORT_INTERVAL, Config.DEFAULT_LONG_INTERVAL,
                Config.DEFAULT_SHORT_DURATION, Config.DEFAULT_LONG_DURATION
            );
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            String json = jsonBuilder.toString();
            
            int shortInt = parseValue(json, "shortBreakInterval");
            int longInt = parseValue(json, "longBreakInterval");
            int shortDur = parseValue(json, "shortBreakDuration");
            int longDur = parseValue(json, "longBreakDuration");
            
            return new Config(
                shortInt > 0 ? shortInt : Config.DEFAULT_SHORT_INTERVAL, 
                longInt > 0 ? longInt : Config.DEFAULT_LONG_INTERVAL,
                shortDur > 0 ? shortDur : Config.DEFAULT_SHORT_DURATION,
                longDur > 0 ? longDur : Config.DEFAULT_LONG_DURATION
            );
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
            return new Config(
                Config.DEFAULT_SHORT_INTERVAL, Config.DEFAULT_LONG_INTERVAL,
                Config.DEFAULT_SHORT_DURATION, Config.DEFAULT_LONG_DURATION
            );
        }
    }

    public static void saveConfig(Config config) {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                String json = String.format(
                    "{\n" +
                    "  \"shortBreakInterval\": %d,\n" +
                    "  \"longBreakInterval\": %d,\n" +
                    "  \"shortBreakDuration\": %d,\n" +
                    "  \"longBreakDuration\": %d\n" +
                    "}",
                    config.getShortBreakInterval(), config.getLongBreakInterval(),
                    config.getShortBreakDuration(), config.getLongBreakDuration()
                );
                writer.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int parseValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return -1;
            
            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return -1;

            int commaIndex = json.indexOf(",", colonIndex);
            int braceIndex = json.indexOf("}", colonIndex);
            
            int endIndex = commaIndex;
            if (endIndex == -1 || (braceIndex != -1 && braceIndex < endIndex)) {
                endIndex = braceIndex;
            }
            
            if (endIndex == -1) return -1;

            String valueStr = json.substring(colonIndex + 1, endIndex).trim();
            return Integer.parseInt(valueStr);
        } catch (Exception e) {
            return -1;
        }
    }
}