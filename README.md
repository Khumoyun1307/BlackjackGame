# Blackjack Game Console

A fully-featured, console-based Blackjack game written in Java. This project supports user registration, login, persistent player profiles, statistics tracking, and robust Blackjack gameplay with support for advanced moves like split, double, insurance, and surrender.

---

## Features

- **User Accounts:** Register, login, and maintain persistent player profiles with secure password hashing.
- **Blackjack Gameplay:** Play classic Blackjack against a dealer with support for:
  - Hit, Stay, Double, Split, Surrender, Insurance
  - Multiple hands (splits and re-splits)
  - Dealer logic and shoe management (multiple decks)
- **Statistics:** Track wins, losses, pushes, net profit, average bet, and round history per user.
- **Persistence:** User profiles and stats are saved to disk and loaded automatically.
- **Console UI:** Interactive, user-friendly console interface.
- **Testing:** Includes manual and automated test classes for core logic.

---

## Getting Started

### Prerequisites

- Java 17 or later (uses modern Java features)
- [Gson](https://github.com/google/gson) library for JSON serialization (for stats)

### Project Structure

```
BlackjackGame Console/
├── src/
│   ├── Main.java
│   └── com/
│       └── blackjack/
│           ├── game/
│           ├── model/
│           ├── stats/
│           ├── ui/
│           └── user/
├── test/
│   └── ... (test classes)
├── users/         # User profile files
├── stats/         # Player stats files
├── history/       # Round history files
├── backup/        # User backup files
└── README.md
```

---

## How to Run

1. **Clone or Download** the repository.

2. **Build** the project (using your IDE or `javac`). Make sure the `src` folder is in your classpath.

3. **Run the Main Class:**

   ```sh
   java -cp src com.blackjack.Main
   ```

   Or, if using the provided `Main.java`:

   ```sh
   java -cp src Main
   ```

4. **Follow the prompts** in the console to register, login, and play Blackjack.

---

## Gameplay Overview

- **Login/Signup:** Start by creating an account or logging in.
- **Dashboard:** Choose to play Blackjack, view stats, or log out.
- **Blackjack Room:** Play rounds, place bets, and use all standard Blackjack moves.
- **Stats:** View your win/loss record, net profit, and round history at any time.

---

## Saving & Persistence

- **User Profiles:** Saved in the `users/` directory as text files.
- **Stats:** Saved in the `stats/` directory as JSON files.
- **Round History:** Saved in the `history/` directory as text files.
- **Backups:** User data backups are stored in the `backup/` directory.

---

## Testing

- All test classes are located in the `test/` directory.
- To run a test, execute the corresponding class (e.g., `ManualTestHand`, `SplitManualTest`, etc.).
- Tests cover hand logic, split/re-split, and other core features.

---

## Code Organization

- `com.blackjack.model` — Card, Hand, Player, Dealer, Deck, Shoe, Move
- `com.blackjack.game` — Game logic, rules, round manager, main game loop
- `com.blackjack.user` — User and profile management
- `com.blackjack.stats` — Stats and round history management
- `com.blackjack.ui` — Console UI and GameUI interface

---

## Customization

- **Minimum/Maximum Bet:** Change in `RoundManager.java` (`minBet`, `maxBet`).
- **Starting Balance:** Change in `UserManager.java` (default is 1000).
- **Deck Size:** Choose 2 or 6 decks at the start of each session.

---

## Dependencies

- Java 17+
- Gson (for stats serialization)

---

## License

This project is for educational and personal use. No warranty is provided.

---

## Authors

- [Your Name Here]
- Blackjack Game Console contributors

---

## Acknowledgements

- [Google Gson](https://github.com/google/gson) for JSON serialization
- Unicode symbols for card suits and console art

---

## API Documentation

- [JavaDoc HTML Documentation](docs/index.html)

---

Enjoy playing Blackjack in your terminal!
