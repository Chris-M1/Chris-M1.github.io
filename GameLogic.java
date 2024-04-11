package game;

/**
 *
 * @author milas
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.stream.Collectors;

public class GameLogic {

    private final Scanner scanner;
    private int smallBlind;
    private int bigBlind;
    private int potAmount = 0;
    private List<PWallet> players = new ArrayList<>();
    private final List<String> communityCards = new ArrayList<>();
    private final Deck deck = new Deck();

    public GameLogic(Scanner scanner) {
        this.scanner = scanner;
    }

    private String readStringInput(String prompt, Set<String> validOptions) {
        String input = "";
        do {
            System.out.println(prompt);
            input = scanner.nextLine().trim().toLowerCase();
            if (!validOptions.contains(input)) {
                System.out.println("Invalid choice. Please try again.");
                input = ""; // Reset input to trigger re-prompt
            }
        } while (input.isEmpty());
        return input;
    }

    private int readIntInput(String prompt, int min, int max) {
        int input = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input >= min && input <= max) {
                    isValid = true;
                } else {
                    System.out.println("Input must be between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
        scanner.nextLine(); // Consume newline left-over
        return input;
    }

    public void savePlayerWallets() {
        PlayersInput.writeToFile(players); // Save player information to file
    }

    private void loadPlayerWallets() {
        File file = new File(PlayersInput.getFilePath());
        if (!file.exists() || file.length() == 0) {
            System.out.println("No saved player data found. Starting a new game.");
            return; // No saved data, so just return
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue; // Invalid line format
                }
                String name = parts[0];
                int balance = Integer.parseInt(parts[1]);
                System.out.println(balance);
                PWallet player = new PWallet(name, balance);
                players.add(player);
            }
            if (players.isEmpty()) {
                System.out.println("Saved player data found but no valid players were loaded. Starting a new game.");
            } else {
                System.out.println("Loaded saved player data successfully.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while loading saved player data: " + e.getMessage());
            players.clear(); // Clear potentially partially loaded data
        }
    }

    public void start() {
        boolean keepPlaying = true;  // Control flag for continuous play

        while (keepPlaying) {
            initializeGame();  // Prepare the game with initial setup or a new round setup
            dealHands();       // Deal cards to all players

            // Betting rounds
            preFlopBetting();
            postFlopBetting();
            postTurnBetting();
            postRiverBetting();

            showdown();        // Showdown to determine the winner
            determineWinner(); // Determine winner and possibly distribute pot

            // Ask if players want to continue or end the game (this can be replaced with any termination logic)
            System.out.println("Do you want to play another round? (yes/no)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (!input.equals("yes")) {
                keepPlaying = false;
            }

            if (keepPlaying) {
                resetRound();  // Reset the game to start a new round
            }
        }

        System.out.println("Game ended. Thank you for playing!");
    }

    private void initializeGame() {
        loadPlayerWallets(); // Attempt to load saved player data

        if (!players.isEmpty()) {
            System.out.println("Loaded saved player data. Select option for each player:");
            Iterator<PWallet> iterator = players.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                System.out.println("Play using: \nName:" + player.getName() + "\nBalance: " + ((PWallet) player).getWallet() + "? (yes/no)");
                String response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("yes")) {
                    iterator.remove(); // Remove the player if not keeping
                }
            }

            System.out.println("Do you want to add new players? (yes/no)");
            String addNewPlayersResponse = scanner.nextLine().trim().toLowerCase();
            if (addNewPlayersResponse.equals("yes")) {
                promptForNewPlayers();
            }
        } else {
            promptForNewPlayers();
        }

        // Prompt for blinds after determining the player setup
        setupBlinds();
        // Save the current state including any new players added
        savePlayerWallets();
    }

    private void promptForNewPlayers() {
        int numPlayers = readIntInput("Enter number of players (1-10):", 1, 10);
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("Enter name for Player " + (i + 1) + ":");
            String playerName = scanner.nextLine().trim();
            if (playerName.isEmpty()) {
                System.out.println("Player name cannot be empty. Please try again.");
                i--; // Ensure a valid name is entered
                continue;
            }

            int buyInAmount = readIntInput("Enter Wallet amount for " + playerName + ":", 1, Integer.MAX_VALUE);
            PWallet player = new PWallet(playerName, buyInAmount);
            players.add(player);
        }
    }

    private void setupBlinds() {
        System.out.println("Enter small blind amount:");
        smallBlind = readIntInput("", 1, Integer.MAX_VALUE); // Assuming readIntInput handles prompts even if empty
        bigBlind = smallBlind * 2;
        // Deduct blinds for the first round
        deductBlinds();
    }

    private int dealerPosition = -1; // Initialize dealer position

    private void deductBlinds() {
        dealerPosition = (dealerPosition + 1) % players.size(); // Move dealer button
        int smallBlindPosition = (dealerPosition + 1) % players.size();
        int bigBlindPosition = (dealerPosition + 2) % players.size();

        PWallet smallBlindPlayer = players.get(smallBlindPosition);
        PWallet bigBlindPlayer = players.get(bigBlindPosition);

        smallBlindPlayer.deductFromWallet(smallBlind);
        bigBlindPlayer.deductFromWallet(bigBlind);
        potAmount += smallBlind + bigBlind;

        System.out.println(smallBlindPlayer.getName() + " posts small blind of $" + smallBlind);
        System.out.println(bigBlindPlayer.getName() + " posts big blind of $" + bigBlind);
    }

    private void dealHands() {
        for (Player player : players) {
            player.receiveCard(deck.draw());
            player.receiveCard(deck.draw());
        }
    }

    private void preFlopBetting() {
        System.out.println("*** PRE-FLOP Betting Round ***");
        displayTableInfo();
        startBettingRound();
    }

    private void postFlopBetting() {
        System.out.println("\n*** FLOP ***");
        dealCommunityCards(3); // Deal the flop cards
        displayTableInfo();
        startBettingRound();
    }

    private void postTurnBetting() {
        System.out.println("\n*** TURN ***");
        dealCommunityCards(1); // Deal the turn card
        displayTableInfo();
        startBettingRound();
    }

    private void postRiverBetting() {
        System.out.println("\n*** RIVER ***");
        dealCommunityCards(1); // Deal the river card
        displayTableInfo();
        startBettingRound();
    }

    private void startBettingRound() {
    potAmount += smallBlind + bigBlind;
    System.out.println("\n<<< START BETTING >>>");

    int activePlayers = (int) players.stream().filter(p -> !p.hasFolded()).count();
    if (activePlayers <= 1) {
        // Only one player remains
        PWallet lastActivePlayer = players.stream().filter(p -> !p.hasFolded()).findFirst().orElse(null);
        if (lastActivePlayer != null) {
            System.out.println(lastActivePlayer.getName() + " wins the round by default with a pot of $" + potAmount);
            lastActivePlayer.addToWallet(potAmount); // Add pot amount to the last remaining player's wallet
            resetRound(); // Reset game for a new round
            return; // Stop further actions in this betting round
        }
    }

    // Continue with betting if more than one player is still active
    for (PWallet player : players) {
        if (player.hasFolded()) {
            continue; // Skip the players who have already folded
        }

        System.out.println(player.getName() + "'s turn:");
        System.out.println("Options: Fold [f], Call [c], Raise [r]");
        System.out.println("Enter your choice:");
        String choice = scanner.nextLine().toLowerCase();
        switch (choice) {
            case "f":
                System.out.println(player.getName() + " folds.");
                player.fold();
                if (players.stream().filter(p -> !p.hasFolded()).count() <= 1) {
                    // Only one player remains after folding
                    concludeRound();
                    return;
                }
                break;
            case "c":
                System.out.println(player.getName() + " calls.");
                break;
            case "r":
                System.out.println(player.getName() + " raises.");
                int raiseAmount = readIntInput("Enter the amount to raise:", 1, player.getWallet());
                player.deductFromWallet(raiseAmount); // Deduct the raise amount from the player's wallet
                potAmount += raiseAmount; // Add raised amount to pot
                System.out.println(player.getName() + " raises by $" + raiseAmount);
                break;
            default:
                System.out.println("Invalid choice. Please choose again.");
                break;
        }
    }
}


    private void concludeRound() {
    PWallet lastActivePlayer = players.stream().filter(p -> !p.hasFolded()).findFirst().orElse(null);
    if (lastActivePlayer != null) {
        System.out.println(lastActivePlayer.getName() + " wins by default with $" + potAmount);
        lastActivePlayer.addToWallet(potAmount);
    }
    resetRound(); // Reset the game to start a new round
}

private void resetRound() {
    communityCards.clear(); // Only clear community cards when round truly ends
    potAmount = 0;
    for (PWallet player : players) {
        player.setCards(new ArrayList<>()); // Clear cards for new round
        player.unfold(); // Reset fold status
    }
    deck.shuffle(); // Shuffle the deck for the new round
    setupBlinds(); // Setup blinds for the new round
    dealHands(); // Deal new hands
    System.out.println("New round started.");
}

    private void dealCommunityCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.draw());
        }
    }

    private void displayTableInfo() {
        System.out.println("!!!! Table Information !!!!");
        for (Player player : players) {
            if (player.hasFolded()) {
                System.out.println(player.getName() + ": [F]");
            } else {
                System.out.println(player);
            }
        }
        System.out.println("Community Cards: " + communityCards);
        System.out.println("Pot Amount: $" + potAmount);
    }

    private void showdown() {
        System.out.println("\n*** SHOWDOWN ***");
        System.out.println("Community Cards: " + communityCards);
        for (Player player : players) {
            System.out.println(player.getName() + "'s Hand: " + player.getCards());
        }
    }

    private void determineWinner() {
        if (players.stream().filter(p -> !p.hasFolded()).count() <= 1) {
            // If only one or no players are active, the game should not proceed to determine winner in traditional sense
            return;
        }

        List<PWallet> activePlayers = players.stream()
                .filter(p -> !p.hasFolded())
                .collect(Collectors.toList());
        PokerHand bestHand = null;
        List<PWallet> winners = new ArrayList<>();

        for (PWallet player : activePlayers) {
            PokerHand playerBestHand = HandProfiler.evaluateBestHand(player.getCards(), communityCards);
            if (bestHand == null || HandComparison.compareHands(playerBestHand, bestHand) > 0) {
                bestHand = playerBestHand;
                winners.clear();
                winners.add(player);
            } else if (HandComparison.compareHands(playerBestHand, bestHand) == 0) {
                winners.add(player);
            }
        }

        announceWinners(winners, bestHand);
    }

    private void announceWinners(List<PWallet> winners, PokerHand bestHand) {
        if (winners.size() > 1) {
            System.out.println("It's a tie between:");
        } else {
            System.out.println("The winner is:");
        }
        for (Player winner : winners) {
            System.out.println(winner.getName() + " with a hand of " + bestHand.getRank());
        }
    }
}