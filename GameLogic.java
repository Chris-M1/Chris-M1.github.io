
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
    
    public void saveGameState() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("gameState.txt"))) {
        for (Player player : players) {
            writer.write(player.getName() + "," + player.getBalance());
            for (String card : player.getCards()) {
                writer.write("," + card);
            }
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void loadGameState() {
    try (BufferedReader reader = new BufferedReader(new FileReader("gameState.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            Player player = new Player(data[0], Integer.parseInt(data[1]));
            // Assuming the first two elements are the player's name and balance, and the rest are cards
            player.setCards(Arrays.asList(Arrays.copyOfRange(data, 2, data.length)));
            players.add(player);
        }
    } catch (IOException e) {
        e.printStackTrace();
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
        System.out.println("Enter number of players (1-10):");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 0; i < numPlayers; i++) {
            System.out.println("Enter name for Player " + (i + 1) + ":");
            String playerName = scanner.nextLine();
            System.out.println("Enter buy-in amount for " + playerName + ":");
            int buyInAmount = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            Player player = new Player(playerName, buyInAmount);
            players.add(player);
        }

        System.out.println("Enter small blind:");
        smallBlind = scanner.nextInt();
        bigBlind = 2 * smallBlind;
        scanner.nextLine(); // Consume newline

        // Add total buy-in amount to the initial pot
        for (Player player : players) {
            potAmount += player.getBalance();
        }
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
        for (Player player : players) {
            System.out.println(player.getName() + "'s Hand: " + player.getCards());
        }
    }

     private void determineWinner() {
        List<Player> winners = new ArrayList<>();
        PokerHand bestHand = null;

        for (Player player : players) {
            PokerHand playerBestHand = evaluateBestHand(player, communityCards);
            if (bestHand == null) {
                bestHand = playerBestHand;
                winners.add(player);
            } else {
                int comparison = compareHands(playerBestHand, bestHand);
                if (comparison > 0) {
                    // Current player has a better hand, so they become the new winner
                    winners.clear();
                    winners.add(player);
                    bestHand = playerBestHand;
                } else if (comparison == 0) {
                    // Tie: add the current player to the list of winners
                    winners.add(player);
                }
                // If comparison < 0, do nothing as the current player's hand is not better
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
    
    private PokerHand evaluateBestHand(Player player, List<String> communityCards) {
    // Placeholder logic for evaluating the player's best hand
    // This should be replaced with actual poker hand evaluation logic
    
    // Example: Assume we're evaluating to find a High Card for simplification
    List<String> allCards = new ArrayList<>(player.getCards());
    allCards.addAll(communityCards);
    Collections.sort(allCards); // Assume this sorts in poker value order
    
    // For demonstration, assume the last card is the highest (which may not always be the case)
    String highCard = allCards.get(allCards.size() - 1);
    
    // Return a PokerHand object representing a High Card hand
    // In a full implementation, you would determine the actual best hand (e.g., pair, two pair, etc.)
    return new PokerHand(PokerHand.HandRank.HIGH_CARD, Collections.singletonList(highCard));
}
    
    public int compareHandRanks(PokerHand hand1, PokerHand hand2) {
    return hand1.getRank().compareTo(hand2.getRank());
}

   public int compareHands(PokerHand hand1, PokerHand hand2) {
    int rankComparison = compareHandRanks(hand1, hand2);
    if (rankComparison != 0) return rankComparison;

    // Assuming both hands have the same rank at this point
    switch (hand1.getRank()) {
        case ROYAL_FLUSH: // No further comparison needed; all royal flushes are equal
            return 0;
        case STRAIGHT_FLUSH:
        case FLUSH:
        case STRAIGHT:
        case HIGH_CARD:
            return compareHighCards(hand1.getCardValues(), hand2.getCardValues());
        case FOUR_OF_A_KIND:
        case FULL_HOUSE:
        case THREE_OF_A_KIND:
        case TWO_PAIR:
        case ONE_PAIR:
            return compareMultiples(hand1, hand2);
        default:
            throw new IllegalArgumentException("Unknown hand rank: " + hand1.getRank());
    }
}
    private int compareMultiples(PokerHand hand1, PokerHand hand2) {
    // This is conceptual; actual implementation depends on how you're representing the hand and its multiples
    int multipleComparison = compareHighCards(hand1.getMultipleRanks(), hand2.getMultipleRanks());
    if (multipleComparison != 0) return multipleComparison;

    // Compare kickers if the multiples are the same
    return compareHighCards(hand1.getKickers(), hand2.getKickers());
}
    
    private int compareHighCards(List<String> cards1, List<String> cards2) {
    // This method should compare the cards from highest to lowest
    // Assuming cards are sorted, compare from the end of the list
    // You need to parse card values, handle Aces, and so on
        for (int i = cards1.size() - 1; i >= 0; i--) {
            int comparisonResult = compareSingleCard(cards1.get(i), cards2.get(i));
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
    return 0; // If all cards are equal
    }
    
    private int compareSingleCard(String card1, String card2) {
    // Implement comparison logic based on card values
    // A simplified version could map card values to integers and compare those
    return Integer.compare(mapCardToValue(card1), mapCardToValue(card2));
    }
    
    private int mapCardToValue(String card) {
    // Map card strings to their numerical values for comparison
    // "Ace" might be high or low depending on the context (e.g., in a straight)
    // This is a placeholder; actual implementation depends on how cards are represented
    return 0;
    }
}




