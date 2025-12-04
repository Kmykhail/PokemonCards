# Pokémon Cards

An Android application built with Jetpack Compose that displays a list of Pokémon with details, allowing users to browse, favorite, and view detailed information about each Pokémon using the PokéAPI.

## Features

- **Browse Pokémon**: Display a scrollable list of Pokémon with infinite pagination (loads 20 at a time)
- **Favorites**: Mark Pokémon as favorites and track favorite count
- **Details View**: View comprehensive Pokémon details including types, abilities, and base stats
- **Image Caching**: Efficient memory cache for Pokémon images (up to 20 items)
- **Delete**: Remove Pokémon from the list
- **Modern UI**: Built with Material Design 3 and Jetpack Compose

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Build System**: Gradle (Kotlin DSL)

### Architecture & Libraries
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Clean Architecture principles
- **UI Framework**: Jetpack Compose with Material3
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit2 with Gson converter
- **Navigation**: Jetpack Navigation Compose
- **Logging**: Timber
- **Coroutines**: Kotlin Coroutines for asynchronous operations

### Testing
- **Unit Testing**: JUnit, MockK
- **Coroutine Testing**: kotlinx-coroutines-test
- **UI Testing**: Espresso, Compose UI Testing

## Project Structure

```
app/src/main/java/com/kote/obrio/
├── MainActivity.kt                    # Main entry point
├── PokemonApplication.kt              # Application class
├── data/                              # Data layer
│   ├── api/                          # API service definitions
│   ├── cache/                        # Image caching implementation
│   ├── mapper/                       # Data mappers (DTO → Domain)
│   ├── model/                        # Data Transfer Objects (DTOs)
│   └── repository/                   # Repository implementations
├── di/                               # Dependency injection modules
├── domain/                           # Domain layer
│   ├── model/                        # Domain models
│   ├── ImageRepository.kt            # Image repository interface
│   └── PokemonRepository.kt          # Pokémon repository interface
├── navigation/                       # Navigation graph
├── ui/                               # Presentation layer
│   ├── common/                       # Reusable UI components
│   ├── screens/                      # Screen composables
│   │   ├── PokemonListScreen.kt     # Main list screen
│   │   └── PokemonDetailsScreen.kt  # Details screen
│   ├── theme/                        # App theme and styling
│   └── viewmodels/                   # ViewModels
```

## Architecture

This project follows Clean Architecture principles with clear separation of concerns:

- **Domain Layer**: Contains business models and repository interfaces
- **Data Layer**: Implements repositories, handles API calls, and manages caching
- **Presentation Layer**: Jetpack Compose UI with ViewModels for state management

### Key Components

- **PokemonViewModel**: Manages Pokémon list state, pagination, favorites, and image loading
- **ImageMemoryCache**: LRU cache implementation storing up to 20 Pokémon images
- **PokemonApiService**: Retrofit interface for PokéAPI endpoints

## API

This app uses the [PokéAPI](https://pokeapi.co/) to fetch Pokémon data:

- **List Endpoint**: `GET /v2/pokemon?offset={offset}&limit={limit}`
- **Details Endpoint**: `GET /v2/pokemon/{name}`

## Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd PokemonCards
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**:
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**:
   - Connect an Android device or start an emulator
   - Click Run in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Android Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
The project includes comprehensive unit tests for:
- `ImageRepository`: Tests for cache functionality and LRU eviction
- `PokemonRepository`: Tests for data fetching and mapping

## Features in Detail

### Pagination
The app loads Pokémon in batches of 20. When scrolling near the bottom (within 3 items), the next batch automatically loads with a loading indicator.

### Image Caching
Uses an in-memory LRU cache to store up to 20 Pokémon images. When the cache is full, the oldest image is evicted to make room for new ones.

### Favorites
Users can mark Pokémon as favorites. The favorite count is displayed in the app bar. Favorites persist during the app session.

## Requirements

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 24+
- Internet connection (to fetch Pokémon data)

## License

This project is for educational and demonstration purposes.

## Acknowledgments

- [PokéAPI](https://pokeapi.co/) - The RESTful Pokémon API
- Pokémon and Pokémon character names are trademarks of Nintendo
