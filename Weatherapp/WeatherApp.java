import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

public class WeatherApp {
    private static String API_KEY;
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String[] WEATHER_ANIMATIONS = {
        "🌤️  ⛅  🌥️  ☁️ ",  // Cloudy animation
        "🌧️  🌦️  🌧️  🌦️ ",  // Rainy animation
        "⛈️  🌩️  ⛈️  🌩️ ",  // Thunderstorm animation
        "🌞  🌤️  🌞  🌤️ ",  // Sunny animation
        "❄️  🌨️  ❄️  🌨️ "   // Snowy animation
    };

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            API_KEY = props.getProperty("api.key");
            if (API_KEY == null || API_KEY.trim().isEmpty() || API_KEY.equals("YOUR_API_KEY")) {
                throw new RuntimeException("Please set your API key in config.properties file");
            }
        } catch (Exception e) {
            System.err.println("\n╔══════════════════════════════════════════════════╗");
            System.err.println("║                  ⚠️ Setup Error ⚠️                ║");
            System.err.println("╠══════════════════════════════════════════════════╣");
            System.err.println("║  Missing or invalid config.properties file.       ║");
            System.err.println("║  Please create config.properties with content:    ║");
            System.err.println("║  api.key=YOUR_API_KEY                            ║");
            System.err.println("║                                                  ║");
            System.err.println("║  Get your API key from:                          ║");
            System.err.println("║  https://home.openweathermap.org/api_keys       ║");
            System.err.println("╚══════════════════════════════════════════════════╝");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            clearScreen();
            displayTitle();
            System.out.print("\nEnter city name (or 'exit' to quit): ");
            
            String city = scanner.nextLine().trim();
            
            if (city.equalsIgnoreCase("exit")) {
                displayExitMessage();
                break;
            }
            
            try {
                String weatherData = getWeatherData(city);
                displayLoadingAnimation();
                displayWeather(weatherData);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (errorMessage.contains("401")) {
                    displayError("Invalid API key or API key not yet activated.",
                               "Please wait a few minutes for the API key to activate.",
                               "If the error persists, verify your API key at https://home.openweathermap.org/api_keys");
                } else if (errorMessage.contains("404")) {
                    displayError("City not found!", "Please check the city name and try again.");
                } else {
                    displayError(errorMessage);
                }
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
        
        scanner.close();
    }

    private static String getWeatherData(String city) throws Exception {
        String urlString = String.format("%s?q=%s&appid=%s&units=metric", API_URL, city, API_KEY);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            throw new Exception("Server returned HTTP response code: " + responseCode + "\nResponse: " + errorResponse.toString());
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }

    private static void displayWeather(String jsonData) {
        Pattern tempPattern = Pattern.compile("\"temp\":(\\d+\\.?\\d*)");
        Pattern feelsLikePattern = Pattern.compile("\"feels_like\":(\\d+\\.?\\d*)");
        Pattern humidityPattern = Pattern.compile("\"humidity\":(\\d+)");
        Pattern descriptionPattern = Pattern.compile("\"description\":\"([^\"]+)\"");
        Pattern namePattern = Pattern.compile("\"name\":\"([^\"]+)\"");
        Pattern windSpeedPattern = Pattern.compile("\"speed\":(\\d+\\.?\\d*)");
        Pattern pressurePattern = Pattern.compile("\"pressure\":(\\d+)");
        Pattern mainPattern = Pattern.compile("\"main\":\"([^\"]+)\"");
        Pattern countryPattern = Pattern.compile("\"country\":\"([^\"]+)\"");

        String temp = extract(tempPattern, jsonData);
        String feelsLike = extract(feelsLikePattern, jsonData);
        String humidity = extract(humidityPattern, jsonData);
        String description = extract(descriptionPattern, jsonData);
        String cityName = extract(namePattern, jsonData);
        String windSpeed = extract(windSpeedPattern, jsonData);
        String pressure = extract(pressurePattern, jsonData);
        String mainWeather = extract(mainPattern, jsonData);
        String country = extract(countryPattern, jsonData);

        String weatherAnimation = getWeatherAnimation(mainWeather);
        String location = cityName + ", " + country;
        String capitalizedDescription = description.substring(0, 1).toUpperCase() + description.substring(1);

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║               Weather Dashboard                  ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Location: %-37s ║%n", location);
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("%s%n", centerText(weatherAnimation, 52));
        System.out.printf("%s%n", centerText(capitalizedDescription, 52));
        System.out.println("• • • • • • • • • • • • • • • • • • • • • • • • • •");
        System.out.println();
        System.out.printf("  🌡️  Temperature:  %6.1f     °C                    %n", Double.parseDouble(temp));
        System.out.printf("  🌡️  Feels Like:   %6.1f     °C                    %n", Double.parseDouble(feelsLike));
        System.out.printf("  💧 Humidity:     %6s      %%                    %n", humidity);
        System.out.printf("  💨 Wind Speed:   %6.1f     m/s                   %n", Double.parseDouble(windSpeed));
        System.out.printf("  📊 Pressure:     %6s     hPa                   %n", pressure);
        System.out.println();
        System.out.println("• • • • • • • • • • • • • • • • • • • • • • • • • •");
    }

    private static String extract(Pattern pattern, String data) {
        Matcher matcher = pattern.matcher(data);
        return matcher.find() ? matcher.group(1) : "N/A";
    }

    private static String getWeatherAnimation(String mainWeather) {
        if (mainWeather == null) return WEATHER_ANIMATIONS[3]; // Default to sunny
        switch (mainWeather.toLowerCase()) {
            case "clouds": return WEATHER_ANIMATIONS[0];
            case "rain": return WEATHER_ANIMATIONS[1];
            case "thunderstorm": return WEATHER_ANIMATIONS[2];
            case "clear": return WEATHER_ANIMATIONS[3];
            case "snow": return WEATHER_ANIMATIONS[4];
            default: return WEATHER_ANIMATIONS[3];
        }
    }

    private static void displayLoadingAnimation() {
        String[] frames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        System.out.print("\nFetching weather data ");
        try {
            for (int i = 0; i < 15; i++) {
                System.out.print("\r" + frames[i % frames.length] + " Loading...");
                TimeUnit.MILLISECONDS.sleep(100);
            }
            System.out.println();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void displayTitle() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║             🌈 Weather Information 🌈            ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void displayExitMessage() {
        clearScreen();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║          Thank you for using Weather App!        ║");
        System.out.println("║                    Goodbye! 👋                    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void displayError(String... messages) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║                    ⚠️ Error ⚠️                    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        for (String message : messages) {
            System.out.printf("║  %-46s ║%n", message);
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static String centerText(String text, int width) {
        if (text == null || text.isEmpty()) {
            return " ".repeat(width);
        }
        int textLength = text.codePointCount(0, text.length());
        int totalPadding = width - textLength;
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
