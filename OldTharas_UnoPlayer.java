import java.util.List;

public class OldTharas_UnoPlayer implements UnoPlayer {
    /**
     * play - This method is called when it's your turn and you need to
     * choose what card to play.
     *
     * The hand parameter tells you what's in your hand. You can call
     * getColor(), getRank(), and getNumber() on each of the cards it
     * contains to see what it is. The color will be the color of the card,
     * or "Color.NONE" if the card is a wild card. The rank will be
     * "Rank.NUMBER" for all numbered cards, and another value (e.g.,
     * "Rank.SKIP," "Rank.REVERSE," etc.) for special cards. The value of
     * a card's "number" only has meaning if it is a number card. 
     * (Otherwise, it will be -1.)
     *
     * The upCard parameter works the same way, and tells you what the 
     * up card (in the middle of the table) is.
     *
     * The calledColor parameter only has meaning if the up card is a wild,
     * and tells you what color the player who played that wild card called.
     *
     * Finally, the state parameter is a GameState object on which you can 
     * invoke methods if you choose to access certain detailed information
     * about the game (like who is currently ahead, what colors each player
     * has recently called, etc.)
     *
     * You must return a value from this method indicating which card you
     * wish to play. If you return a number 0 or greater, that means you
     * want to play the card at that index. If you return -1, that means
     * that you cannot play any of your cards (none of them are legal plays)
     * in which case you will be forced to draw a card (this will happen
     * automatically for you.)
     */
	
	// red, green, blue, yellow
	private int[] colorsPlayed = {0, 0, 0, 0};
	
	// ranks played list TODO
	// priority list
	//Rank[] priority = {Rank.WILD_D4, Rank.DRAW_TWO, Rank.SKIP, Rank.REVERSE, Rank.NUMBER, Rank.WILD}; // most aggressive
	Rank[] priority = {Rank.NUMBER, Rank.SKIP, Rank.REVERSE, Rank.WILD, Rank.DRAW_TWO, Rank.WILD_D4}; // least aggressive
	
    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
    {
    	int returnInd = -1;
    	// update colors and ranks played
    	for (Color i : state.getMostRecentColorCalledByUpcomingPlayers()) {
    		
    		if (i != null) { colorsPlayed[getColorValue(i)] ++; }
    		
    	}
    	
    	// get color priority
    	int[] colorPrio = new int[4];
    	colorPrio[0] = colorsPlayed[0];
    	colorPrio[1] = colorsPlayed[1];
    	colorPrio[2] = colorsPlayed[2];
    	colorPrio[3] = colorsPlayed[3];
    	Color[] realColorPriority = new Color[4];
    	for (int i = 0; i < 4; i++) {
    		
    		realColorPriority[i] = getMostUsedColor(colorPrio);
    		colorPrio[getColorValue(realColorPriority[i])] = -1;
    		
    	}
    	
        // get current played color
    	Color c = null;
    	if (upCard.getRank() == Rank.WILD || upCard.getRank() == Rank.WILD_D4) {
    		
    		c = calledColor;
    		
    	} else {
    		
    		c = upCard.getColor();
    		
    	}
    	
    	Rank r = upCard.getRank();
    	// boolean to quit loop
    	boolean breakBool = false;
    	for (Color col : realColorPriority) {
    	
	    	for (Rank i : priority) {
	    		
	    		for (int j = 0; j < hand.size(); j++) {
	    			Card curr = hand.get(j);
	    			
	    			if ((curr.followedByCall() || curr.getColor() == col) && curr.getRank() == i && curr.canPlayOn(upCard, calledColor)) {
	    				
	    				returnInd = j;
	    				breakBool = true;
	    				break;
	    				
	    			}
	    			
	    		}
	    		
	    		if (breakBool) { break; }
	    		
	    	}
	    	
    	}
    	
    	// colorsPlayed[getColorValue(hand.get(returnInd).getColor())] ++;
    	return returnInd;
    	
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
       
    	return getMostUsedColor(colorsPlayed);
        
    }
    
    private Color getMostUsedColor(int[] colorArr) {
    	
    	 int max = colorArr[0];
         int ind = 0;
         for (int i = 1; i < 4; i++) {
         	
         	if (colorArr[i] > max) {
         		
         		ind = i;
         		
         	}
         	
         }
         switch(ind) {
 	        case 0: return Color.RED;
 	        case 1: return Color.GREEN;
 	        case 2: return Color.BLUE;
 	        case 3: return Color.YELLOW;
         }
         
         return Color.RED;
    	
    }
    
    public int getColorValue(Color c) {
    	
    	switch (c) {
    		
	    	case RED : return 0;
	    	case GREEN : return 1;
	    	case BLUE : return 2;
	    	case YELLOW : return 3;
		default:
			break;
    		
    	}
    	
    	return -1;
    	
    }
    
}