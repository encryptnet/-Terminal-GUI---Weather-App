# Terminal Weather App 🌈

A beautiful terminal-based weather application written in Java that displays weather information with animations and a modern UI.

## Features ✨

- 🎨 Beautiful terminal UI with box-drawing characters
- 🌈 Weather-specific animations using emojis
- 🔄 Loading animations
- 📊 Comprehensive weather information:
  - Temperature and "feels like" temperature
  - Humidity levels
  - Wind speed
  - Atmospheric pressure
  - Weather description
- 🌍 Location information with country
- ⚡ Real-time weather updates
- 🎯 Error handling with informative messages

## Prerequisites 📋

- Java JDK 11 or higher
- OpenWeatherMap API key

## Getting Started 🚀

1. Clone this repository:
```bash
git clone https://github.com/yourusername/terminal-weather-app.git
cd terminal-weather-app
```

2. Get your API key:
   - Sign up at [OpenWeatherMap](https://openweathermap.org/)
   - Go to your [API keys](https://home.openweathermap.org/api_keys) section
   - Copy your API key

3. Create a `config.properties` file:
```bash
echo "api.key=YOUR_API_KEY" > config.properties
```

4. Compile the program:
```bash
javac WeatherApp.java
```

5. Run the program:
```bash
java WeatherApp
```

## Usage 💻

1. Enter a city name when prompted
2. View the detailed weather information with beautiful animations
3. Type 'exit' to quit the program

## Screenshots 📸

```
╔══════════════════════════════════════════════════╗
║             🌈 Weather Information 🌈            ║
╚══════════════════════════════════════════════════╝

Enter city name (or 'exit' to quit): London

╔══════════════════════════════════════════════════╗
║               Weather Dashboard                  ║
╠══════════════════════════════════════════════════╣
║  Location: London, GB                           ║
║──────────────────────────────────────────────────║
║           🌤️  ⛅  🌥️  ☁️                        ║
║  Partly cloudy                                  ║
║──────────────────────────────────────────────────║
║  🌡️  Temperature: 18.5°C                        ║
║  🌡️  Feels Like: 17.8°C                         ║
║  💧 Humidity: 76%                               ║
║  💨 Wind Speed: 4.2 m/s                         ║
║  📊 Pressure: 1013 hPa                          ║
╚══════════════════════════════════════════════════╝
```

## Contributing 🤝

Feel free to contribute to this project! Open a PR or submit an issue.

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments 🙏

- Weather data provided by [OpenWeatherMap](https://openweathermap.org/)
- Built with love using Java ☕
