import java.util.Arrays;
import java.util.List;

public class TharasC_UnoPlayer implements UnoPlayer {
	// COLOR ORDER RED YELLOW GREEN BLUE
	// RANK ORDER NUMBER SKIP REVERSE D2 WILD WILD_D4
		
	List<Card> played;
	private final int[] cols = {25, 25, 25, 25};
	private final int[] ranks = {76, 8, 8, 8, 4, 4};
	// REQUIRED METHOD
    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
    {
		played = state.getPlayedCards();
    	
		// generates the amount of cards left in the game for each color
    	int[] colsRemain = Arrays.copyOf(playedColorCount(played), 4);
    	int[] ranksRemain = Arrays.copyOf(playedRankCount(played), 6);
    	for (int i = 0; i < 6; i++) {
    		
    		if (i < 4) {
	    		colsRemain[i] = cols[i]-colsRemain[i];
    		}
    		ranksRemain[i] = ranks[i]-ranksRemain[i];
    		
    	}
    	
    	// subtracts penalties for preferred colors
    	for(Color i : state.getMostRecentColorCalledByUpcomingPlayers()) {
    		
    		if (i != null && getColorValue(i) >= 0) {
    			
    			colsRemain[getColorValue(i)] -= 5;
    			
    		}
    		
    	}
    	
    	// updates the amount of cards left in the game based on what we know about each hand
    	for (Card i : hand) {
    		
    		ranksRemain[getRankValue(i.getRank())] -= 1;
    		if (i.getColor() != Color.NONE) {
    			
    			colsRemain[getColorValue(i.getColor())] -= 1;
    			
    		}
    		
    	}
    	
    	// strongly discourages passive play when danger is detected
    	int[] hands = state.getNumCardsInHandsOfUpcomingPlayers();
    	if (hands[1] - hand.size() > 2 || hands[1] < 4) {
    		
    		ranksRemain[0] += 50;
    		ranksRemain[1] -= 40;
    		ranksRemain[2] -= 20;
    		ranksRemain[3] -= 60;
    		ranksRemain[5] -= 100;
    		
    	}
    	
    	// strongly discourages skipping helpful intermediaries when distant player may have low cards
    	if (hands[2] < 4 || hands[3] < 4) {
    		
    		ranksRemain[1] += 60;
    		ranksRemain[2] += 60;
    		ranksRemain[3] += 80;
    		ranksRemain[4] -= 50;
    		ranksRemain[5] -= 50;
    		
    	}
    	
    	// generates a penalty value for each card in hand
    	int[] penalty = new int[hand.size()];
    	for (int i = 0; i < penalty.length; i++) {
    		
    		Card c = hand.get(i); int cval;
    		if (c.getColor() == Color.NONE) {
    			cval = 0;
    		} else {
    			cval = colsRemain[getColorValue(c.getColor())];
    		}
    		penalty[i] += cval*5 + ranksRemain[getRankValue(c.getRank())] - c.forfeitCost()/2;
    		
    	}
    	
    	// picks a card with the minimum penalty
    	int min = Integer.MAX_VALUE;
    	int minind = -1;
    	for (int i = 0; i < penalty.length; i++){
    		
    		if (penalty[i] < min && hand.get(i).canPlayOn(upCard, calledColor)) {
    			
    			min = penalty[i];
    			minind = i;
    			
    		}
    		
    	}
    	
    	return minind;
    	
    }

    // REQUIRED METHOD
    public Color callColor(List<Card> hand)
    {
       
    	int[] colorArr = playedColorCount(played);
    	int max = colorArr[0];
        int ind = 0;
        Color c = Color.RED;
        for (int i = 1; i < 4; i++) {
        	
        	if (colorArr[i] > max) {
        		
        		ind = i;
        		
        	}
        	
        }
        switch(ind) {
	        case 0: c = Color.RED;
	        case 1: c = Color.YELLOW;
	        case 2: c = Color.GREEN;
	        case 3: c = Color.BLUE;
        }
        
    	return c;
        
    }
    
    // get color-indexed count of all colors played
    private int[] playedColorCount(List<Card> played) {
    	
    	int[] colsPlayed = new int[4];
    	for (Card i : played) {
    		
    		if (!i.followedByCall()) {
    			colsPlayed[getColorValue(i.getColor())] ++;
    		}
    		
    	}
    	
    	return colsPlayed;
    	
    }
    
    // get rank-indexed count of all ranks played
    private int[] playedRankCount(List<Card> played) {
    	
    	int[] ranksPlayed = new int[6];
    	for (Card i : played) {
    		
    		ranksPlayed[getRankValue(i.getRank())] ++;
    		
    	}
    	
    	return ranksPlayed;
    	
    }
        
    // returns index from color
    private int getColorValue(Color c) {
    	
    	switch (c) {
	    	case RED : return 0;
	    	case YELLOW : return 1;
	    	case GREEN : return 2;
	    	case BLUE : return 3;
	    	case NONE: return -1;
    	}
    	
    	return -1;
    	
    }
    
    // returns index from rank
    private int getRankValue(Rank r) {
    	
    	switch(r){
    		case NUMBER: return 0;
    		case SKIP: return 1;
    		case REVERSE: return 2;
    		case DRAW_TWO: return 3;
    		case WILD: return 4;
    		case WILD_D4: return 5;
    	}
    	
    	return -1;
    }
    
}
