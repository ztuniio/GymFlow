# GymFlow

A modern, minimalist **Android workout tracker** with a dark glassmorphism design. Log workouts, track progress with charts, hit your goals, and keep your streak alive. All data stays on your device.

## Features

- **Workout logger**: build a session from the exercise library, then log sets, reps, and weight as you train
- **Exercise database** with default sets, reps, and weight, plus custom exercises
- **Progress analytics**: weekly workout and weight-trend charts, lifetime stats (total hours, volume lifted), and personal records (estimated 1-rep max)
- **Fitness calculators**: BMI, macros, daily hydration, and 1RM using the Epley formula
- **Goals and achievements**: set fitness goals, earn badges, and track your best streak
- **Daily stats**: calories burned, water intake, and lifetime totals
- **Data export** to JSON
- **Celebrations**: confetti when you finish a workout

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM (ViewModel + Repository) |
| Local storage | Room database |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines |

Minimum Android version: **7.0 (API 24)**. Target: **API 36**.

## Project structure

```
app/src/main/java/com/example/
├── data/
│   ├── dao/          Room DAOs
│   ├── database/     Room database setup
│   ├── model/        Entities: profile, exercises, workouts, sets, goals, achievements, weight logs
│   └── repository/   Data access layer
└── ui/
    ├── components/   Bottom nav, glass cards, charts, calculators, confetti
    ├── screens/      Home, Workout, Active Workout, Progress, Profile
    ├── theme/        Colors, typography, theme
    └── viewmodel/    GymViewModel
```

## Getting started

1. Clone the repository:

   ```bash
   git clone https://github.com/ztuniio/GymFlow.git
   ```

2. Open the project in **Android Studio**.
3. Let Gradle sync, then run the `app` configuration on an emulator or a device.

## Author

Built by [Zaid](https://github.com/ztuniio).
