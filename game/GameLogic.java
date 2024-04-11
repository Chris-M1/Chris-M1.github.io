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

public class GameLogic {

    private final Scanner scanner;
    private int smallBlind;
    private int bigBlind;
    private int potAmount = 0;
    private final List<Player> players = new ArrayList<>();
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
        PlayerWalletWriter.writeToFile(players); // Save player information to file
    }

    private void loadPlayerWallets() {
        File file = new File(PlayerWalletWriter.getFilePath());
        if (!file.exists() || file.length() == 0) {
            System.out.println("No saved player data found. Starting a new game.");
            return; // No saved data, so just return
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue; // Invalid line format
                
                String name = parts[0];
                int balance = Integer.parseInt(parts[1]);
                System.out.println(balance);
                Player player = new Player(name, balance);
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
        initializeGame();
        dealHands();

        preFlopBetting();
        postFlopBetting();
        postTurnBetting();
        postRiverBetting();

        showdown();
        determineWinner();
    }

    private void initializeGame() {
    loadPlayerWallets(); // Attempt to load saved player data

    if (!players.isEmpty()) {
        System.out.println("Loaded saved player data. Select option for each player:");
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            System.out.println("Play using: \nName:" + player.getName() +"\nBalance: "  + "? (yes/no)");
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

        int buyInAmount = readIntInput("Enter buy-in amount for " + playerName + ":", 1, Integer.MAX_VALUE);
        Player player = new Player(playerName, buyInAmount);
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

        Player smallBlindPlayer = players.get(smallBlindPosition);
        Player bigBlindPlayer = players.get(bigBlindPosition);

        // Deduct small and big blinds from players and add to pot
        smallBlindPlayer.deductBalance(smallBlind);
        bigBlindPlayer.deductBalance(bigBlind);
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

        for (Player player : players) {
            if (!player.hasFolded()) {
                System.out.println("" + player.getName() + "'s turn:");
                System.out.println("Options: Fold [f], Call [c], Raise [r]");
                System.out.println("Enter your choice:");
                String choice = scanner.nextLine().toLowerCase();
                switch (choice) {
                    case "f":
                        System.out.println(player.getName() + " folds.");
                        player.fold();
                        break;
                    case "c":
                        System.out.println(player.getName() + " calls.");
                        break;
                    case "r":
                        System.out.println(player.getName() + " raises.");
                        System.out.println("Enter the amount to raise:");
                        int raiseAmount = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        // Logic to handle raising the bet
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose again.");
                        break;
                }
            }
        }
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
        List<Player> winners = new ArrayList<>();
        PokerHand bestHand = null;

        for (Player player : players) {
            // Correctly pass the player's hand and community cards to evaluateBestHand
            PokerHand playerBestHand = PokerHandEvaluator.evaluateBestHand(player.getCards(), communityCards);
            if (bestHand == null) {
                bestHand = playerBestHand;
                winners.add(player);
            } else {
                int comparison = HandComparison.compareHands(playerBestHand, bestHand);
                if (comparison > 0) {
                    // This player has the best hand so far, so clear the winners list and add this player
                    winners.clear();
                    winners.add(player);
                    bestHand = playerBestHand;
                } else if (comparison == 0) {
                    // This player's hand ties with the best hand, so add this player to the winners list
                    winners.add(player);
                }
                // If comparison < 0, the current player's hand is not better than the best hand found so far
            }
        }

        announceWinners(winners, bestHand);
    }

    private void announceWinners(List<Player> winners, PokerHand bestHand) {
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
