import java.util.Arrays;
import java.util.List;

public class TharasC_UnoPlayer implements UnoPlayer {
	// COLOR ORDER RED YELLOW GREEN BLUE
	// RANK ORDER NUMBER SKIP REVERSE D2 WILD WILD_D4
		
	List<Card> played;
	private final int[] cols = {25, 25, 25, 25};
	private final int[] ranks = {4, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 4};
	// REQUIRED METHOD
    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
    {
		played = state.getPlayedCards();
    	
		// generates the amount of cards left in the game for each color
    	int[] colsRemain = Arrays.copyOf(playedColorCount(played), 4);
    	int[] ranksRemain = Arrays.copyOf(playedRankCount(played), 15);
    	for (int i = 0; i < 6; i++) {
    		
    		if (i < 4) {
	    		colsRemain[i] = cols[i]-colsRemain[i];
    		}
    		ranksRemain[i] = ranks[i]-ranksRemain[i];
    		
    	}
    	
    	// subtracts penalties for preferred colors
    	for(Color i : state.getMostRecentColorCalledByUpcomingPlayers()) {
    		
    		if (i != null && getColorValue(i) >= 0) {
    			
    			colsRemain[getColorValue(i)] -= 100;
    			
    		}
    		
    	}
    	
    	// updates the amount of cards left in the game based on what we know about each hand
    	for (Card i : hand) {
    		
    		ranksRemain[getRankValue(i)] -= 1;
    		if (i.getColor() != Color.NONE) {
    			
    			colsRemain[getColorValue(i.getColor())] -= 1;
    			
    		}
    		
    	}
    	int[] numberWeights = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    	// strongly discourages passive play when danger is detected
    	int[] hands = state.getNumCardsInHandsOfUpcomingPlayers();
    	int[] points = state.getTotalScoreOfUpcomingPlayers();
    	if (hands[0] - hand.size() > 3 || hands[0] < 4) {
    		
    		adjustRankWeights(ranksRemain, numberWeights, -100);
    		ranksRemain[10] -= 200;
    		ranksRemain[11] -= 200;
    		ranksRemain[12] -= 2000;
    		ranksRemain[14] -= 2000;
    		
    	}
    	
    	// strongly discourages skipping helpful intermediaries when distant player may have low cards
    	if (hands[1] < 5 || hands[2] < 5) {
    		
    		adjustRankWeights(ranksRemain, numberWeights, -100);
    		ranksRemain[10] += 500;
    		ranksRemain[11] -= 500;
    		ranksRemain[12] += 2500;
    		ranksRemain[13] -= 400;
    		ranksRemain[14] += 2500;
    		
    	}
    	
    	// discourages all aggressive play when last player might have low cards
    	if (hands[2] > hands[1]) {
    		
    		adjustRankWeights(ranksRemain, numberWeights, -100);
    		ranksRemain[10] -= 300;
    		ranksRemain[11] += 600;
    		ranksRemain[12] += 800;
    		ranksRemain[13] -= 400;
    		ranksRemain[14] -= 1000;
    		
    	}
    	    	
    	// generates a penalty value for each card in hand
    	int[] penalty = new int[hand.size()];
    	for (int i = 0; i < penalty.length; i++) {
    		
    		Card c = hand.get(i); int cval;
    		if (c.getColor() == Color.NONE) {
    			cval = colsRemain[getColorValue(callColor(hand))];
    		} else {
    			cval = colsRemain[getColorValue(c.getColor())];
    		}
    		penalty[i] += cval*5000 + ranksRemain[getRankValue(c)] - c.forfeitCost();
    		
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
        Color[] colors = {Color.RED, Color.YELLOW,Color.GREEN, Color.BLUE};
    	int[] colorArr = playedColorCount(played);
    	int max = colorArr[0];
        int ind = 0;
        Color c = Color.RED;
        for (int i = 1; i < 4; i++) {
        	
        	if (!colorInHand(colors[i], hand)) {
        		
        		continue;
        		
        	}
        	if (colorArr[i] > max) {
        		
        		ind = i;
        		
        	}
        	
        }
        c = colors[ind];
        
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
    	
    	int[] ranksPlayed = new int[15];
    	for (Card i : played) {
    		
    		ranksPlayed[getRankValue(i)] ++;
    		
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
    private int getRankValue(Card c) {
    	
    	Rank r = c.getRank();
    	switch(r){
    		case NUMBER: return c.getNumber();
    		case SKIP: return 10;
    		case REVERSE: return 11;
    		case DRAW_TWO: return 12;
    		case WILD: return 13;
    		case WILD_D4: return 14;
    	}
    	
    	return -1;
    }
    
    // checks if color exists in hand for calledColor method
    private boolean colorInHand(Color c, List<Card> hand) {
    	
    	for (Card i : hand) {
    		
    		if (i.getColor() == c) {
    			
    			return true;
    			
    		}
    		
    	}
    	
    	return false;
    	
    }
    
    // adjust rank weights
    // intended to modify initial array
    private void adjustRankWeights(int[] weights, int[] indices, int num) {
    	
    	for (int i : indices) {
    		
    		weights[i] += num;
    		
    	}
    	
    }
    
}
