# 🐉 Poke-UIB: The Mystic World at UIB

<p align="center">
  <img src="menu_principal.jpg" width="800" alt="Poke-UIB Main Menu">
</p>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://www.android.com)
[![Language: Java](https://img.shields.io/badge/Language-Java-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)

## 📝 Project Overview
[cite_start]**Poke-UIB** is an immersive, location-based Android application developed as the final project for the **Algorithms and Data Structures II** course at the **University of the Balearic Islands (UIB)**[cite: 1, 3, 4]. 

[cite_start]The project challenges students to build a complex system capable of managing a large-scale ecosystem of mythical creatures (*Vapordrac, Focguard, Tornadrac, and Aiguard*) within the UIB campus[cite: 9, 19, 28]. [cite_start]The core focus is on the efficient implementation of **Advanced Data Structures** to handle entity tracking, spatial queries, and real-time game state management[cite: 20, 21, 204].

---

## 🎮 Game Mechanics

### 1. Exploration and Detection
[cite_start]The game world is a high-resolution map of the UIB campus (6144x4096 px)[cite: 153]. [cite_start]Using a specialized "arcane energy sensor," players can detect creatures hidden in a parallel reality[cite: 15, 16].

* [cite_start]**Spatial Division:** The map is divided into functional zones (buildings, departments, and services) based on precise GPS-like coordinates[cite: 28, 46].
* [cite_start]**Sensor Behavior:** The reticle at the center of the screen changes from a **white cross** (no creatures nearby) to a **white circle** when entities are within detection range[cite: 90, 91, 108].
* **Dynamic Zoom:** Visibility depends on the creature's genre. [cite_start]Some are visible from a distance, while others require high-level zoom to be detected[cite: 43, 53, 85].

<p align="center">
  <img src="pantalla_juego.jpg" width="700" alt="Gameplay and Map Interface">
</p>

### 2. The Capture Mini-Game
[cite_start]When a player's coordinates collide with a creature's position, a "Rock, Paper, Scissors" challenge is triggered[cite: 25, 26, 93].
* [cite_start]**Victory:** The creature accepts a pact and is added to the **Captured Inventory**[cite: 96].
* [cite_start]**Defeat:** The creature escapes and is logged in the **Escaped History**[cite: 97].
* [cite_start]**Reward System:** Points are awarded based on the rarity of the genre: **Aiguard** (10), **Focguard** (15), **Tornadrac** (20), and **Vapordrac** (30)[cite: 42].

---

## 🛠️ Technical Architecture

### Core Data Structures
[cite_start]A key requirement was implementing custom collections to ensure optimal computational costs ($O(N)$ or $O(log N)$ where applicable)[cite: 204, 213, 216].

1.  [cite_start]**Custom Sets (`UnsortedArraySet` / `UnsortedLinkedListSet`):** Used to manage dynamic collections of creatures and UI elements without duplicates[cite: 205, 212].
2.  [cite_start]**Mapping Systems (Dictionaries):** * **Zone -> (Genre -> Set):** A nested mapping structure to organize the 500 initial creatures by location and type[cite: 29, 205].
    * [cite_start]**Search Engine:** A mapping that links "popular names" (for user search) to "official names" (for UI display)[cite: 194, 196, 207].
    * [cite_start]**Attribute Registry:** Stores specific genre constants like movement speed and detection radius[cite: 38, 43, 210].

### Data Management and JSON
[cite_start]The application's environment is defined by an external `zones.json` file[cite: 156, 158]. Each zone is represented as a `Rect` object defined by two coordinate points:
```json
{
  "zona": "jovellanos",
  "nom": "Gaspar Melchor de Jovellanos",
  "x1": 2893, "y1": 920, "x2": 3432, "y2": 1446
}
