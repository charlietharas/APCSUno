package oldPlayerVersions;
import java.util.List;

public class TharasC_UnoPlayer implements UnoPlayer {
    
	// This code successfully passes all test cases
	
    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
    {
        // THIS IS WHERE YOUR AMAZING CODE GOES
    	Color c = null;
    	if (upCard.getRank() == Rank.WILD || upCard.getRank() == Rank.WILD_D4) {
    		
    		c = calledColor;
    		
    	} else {
    		
    		c = upCard.getColor();
    		
    	}
    	
    	int ind = 0;
    	for (Card i : hand) {
    		
    		if (i.getColor() == c) {
    			
    			return ind;
    			
    		}
    		ind++;
    	}
    	
    	ind = 0;
    	for (Card i : hand) {
    		
    		if  (upCard.getRank() == Rank.NUMBER && i.getNumber() == upCard.getNumber()) {
    			
    			return ind;
    			
    		}
    		else if (upCard.getRank() != Rank.NUMBER && upCard.getRank() == i.getRank()) {
    			
    			return ind;
    			
    		}
    		ind++;
    		
    	}
    	
    	ind = 0;
    	for (Card i : hand) {
    		
    		if (i.getRank() == Rank.WILD || i.getRank() == Rank.WILD_D4) {
    			
    			return ind;
    			
    		}
    		ind++;
    		
    	}
    	
        return -1;
    }

    /**
     * callColor - This method will be called when you have just played a
     * wild card, and is your way of specifying which color you want to 
     * change it to.
     *
     * You must return a valid Color value from this method. You must not
     * return the value Color.NONE under any circumstances.
     */
    public Color callColor(List<Card> hand)
    {
        // THIS IS WHERE YOUR AMAZING CODE GOES
        return Color.RED;
    }
}
