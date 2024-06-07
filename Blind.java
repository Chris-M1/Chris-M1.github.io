
package game;

/**
 *
 * @author dexter
 */
class Blind 
{
    private final int smallBlindAmount;
    private final int bigBlindAmount;

    
    /**
    * Constructs a Blind object with specified small blind and big blind amounts.
    * 
    * @param smallBlindAmount the amount of the small blind
    * @param bigBlindAmount the amount of the big blind
    */
    public Blind(int smallBlindAmount, int bigBlindAmount)
    {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
    }
    
    /**
    * Gets the amount of the small blind.
    * 
    * @return the amount of the small blind
    */
    public int getSmallBlind()
    {
        return smallBlindAmount;
    }
    
    
    /**
    * Gets the amount of the big blind.
    * 
    * @return the amount of the big blind
    */

    public int getBigBlind() 
    {
        return bigBlindAmount;
    }
}
