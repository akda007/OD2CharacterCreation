# Old Dragon 2: Pocket Legends

Old Dragon 2: Pocket Legends is a native Android application designed as a digital companion and idle battler for the Old Dragon 2nd Edition Tabletop RPG system.

This application demonstrates a modern Android architecture using Jetpack Compose for the UI, Hilt for dependency injection, and Android Foreground Services for persistent background game logic. It serves two primary functions: a strict rules-as-written character creator and an automated dungeon crawler simulation.

## Project Overview

The application is built to strictly adhere to the OD2 SRD (System Reference Document) rules. It handles complex attribute calculations, class restrictions, and race modifiers automatically, ensuring that generated characters are legal for tournament or casual play.

The "Adventure Mode" implements a state-machine-based combat simulator that runs independently of the UI lifecycle, allowing the game loop to process turns, calculate RNG (Random Number Generation), and update player state even when the application is minimized or the device is locked.

## Features

### Character Creation Module
* **Attribute Generation**: Implements three distinct rolling algorithms:
    * *Classic*: 3d6 summed (strict order).
    * *Adventurer*: 3d6 summed (distributable).
    * *Heroic*: 4d6 drop lowest (distributable).
* **Race & Class System**: Modeling of base stats, saving throws, and specific abilities for Humans, Elves, Dwarves, and Halflings paired with Warriors, Clerics, Thieves, and Mages.
* **Dynamic Calculation**: Real-time computation of derived statistics including Armor Class (AC), Hit Points (HP), Base Attack Bonus (BAB), and Saving Throws based on attribute modifiers.

### Idle Battle System
* **Headless Execution**: The battle logic runs within an Android Foreground Service, decoupling the game loop from the UI layer.
* **Combat Simulation**: Turn-based logic that calculates d20 rolls against AC, damage application, and turn management.
* **Persistence**: Game state is preserved across UI recreation cycles using a singleton state manager pattern.
* **System Notifications**: Utilizes `NotificationManager` to provide real-time updates in the system tray and high-priority alerts upon character death.

## Technical Architecture

The project follows the Guide to App Architecture recommendations, utilizing MVVM (Model-View-ViewModel) with a unidirectional data flow.

### Tech Stack

* **Language**: Kotlin
* **UI Toolkit**: Jetpack Compose (Material Design 3)
* **Dependency Injection**: Hilt (Dagger)
* **Asynchrony**: Kotlin Coroutines & Flow
* **Architecture Components**: ViewModel, Lifecycle, Navigation Compose

### Key Components

* **Domain Layer**: Contains pure Kotlin data classes (`Player`, `Monster`, `Race`) and business logic (`RollAttributes`, `BattleSimulator`).
* **UI Layer**: Composable functions observing `StateFlow` from ViewModels.
* **Service Layer**: `BattleService` extends the Android `Service` class to maintain the game loop process. It communicates with the UI via a shared reactive state object (`BattleStateManager`).

## Setup and Installation

### Prerequisites
* Android Studio Hedgehog | 2023.1.1 or newer
* JDK 17
* Android SDK API Level 34 (UpsideDownCake)

### Permissions
The application requires specific permissions to function correctly in background mode. Ensure your device allows notifications for the app.

* `FOREGROUND_SERVICE`: To keep the battle engine alive.
* `POST_NOTIFICATIONS`: To display combat logs and status updates (Android 13+).

### Building the Project
1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Sync Gradle files.
4.  Run on an emulator or physical device.

## Legal

This application is a fan-made project based on the Old Dragon 2nd Edition RPG system published by Buró. It is not officially affiliated with Buró.
