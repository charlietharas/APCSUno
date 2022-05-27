import java.util.Arrays;
import java.util.List;

public class TharasC_UnoPlayer implements UnoPlayer {
	
	/*
	 *            ;               ,           
         ,;                 '.         
        ;:                   :;        
       ::                     ::       
       ::                     ::       
       ':                     :        
        :.                    :        
     ;' ::                   ::  '     
    .'  ';                   ;'  '.    
   ::    :;                 ;:    ::   
   ;      :;.             ,;:     ::   
   :;      :;:           ,;"      ::   
   ::.      ':;  ..,.;  ;:'     ,.;:   
    "'"...   '::,::::: ;:   .;.;""'    
        '"""....;:::::;,;.;"""         
    .:::.....'"':::::::'",...;::::;.   
   ;:' '""'"";.,;:::::;.'""""""  ':;   
  ::'         ;::;:::;::..         :;  
 ::         ,;:::::::::::;:..       :: 
 ;'     ,;;:;::::::::::::::;";..    ':.
::     ;:"  ::::::"""'::::::  ":     ::
 :.    ::   ::::::;  :::::::   :     ; 
  ;    ::   :::::::  :::::::   :    ;  
   '   ::   ::::::....:::::'  ,:   '   
    '  ::    :::::::::::::"   ::       
       ::     ':::::::::"'    ::       
       ':       """""""'      ::       
        ::                   ;:        
        ':;                 ;:"        
          ';              ,;'          
            "'           '"            
              '
              
             GO SPIDERS!!!!!
      please give me more points
	 */
	
	// stores played cards as an instance variable so that callColor() can be smart
	List<Card> played;
	
	// stores maximum amount of cards in game to calculate remaining cards for card counting
	private final int[] cols = {25, 25, 25, 25};
	private final int[] ranks = {4, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 4};
	// REQUIRED METHOD
	/**
	 * Play
	 * Selects an index in a given hand of the optimal card to play in that current situation.
	 * Utilizes a weights-based algorithm:
	 * </br>-Counts cards to get the amount of remaining cards per color and per rank (including specific numbers as ranks) 
	 * </br>-Updates penalties based on predetermined rules involving called colors, hand sizes, and point differences
	 * </br>-Calculates a final penalty based on the amount of remaining colors, rank penalties (previous step), ranks remaining, and forfeit value
	 * </br>-Plays the first valid card with the minimum penalty based on these rules
	 */
    public int play(List<Card> hand, Card upCard, Color calledColor, GameState state)
    {
    	    	
		played = state.getPlayedCards();
    	
		// generates the amount of cards left in the game for each color
    	int[] colsRemain = Arrays.copyOf(playedColorCount(played), 4);
    	int[] ranksRemain = Arrays.copyOf(playedRankCount(played), 15);
    	int[] handCols = playedColorCount(hand);
    	int[] handRanks = playedRankCount(hand);
    	for (int i = 0; i < 15; i++) {
    		
    		if (i < 4) {
	    		colsRemain[i] = cols[i]-colsRemain[i]-handCols[i];
    		}
    		ranksRemain[i] = ranks[i]-ranksRemain[i]-handRanks[i];
    		
    	}
    	
    	// subtracts penalties for colors that players have called
    	for(Color i : state.getMostRecentColorCalledByUpcomingPlayers()) {
    		
    		if (i != null && getColorValue(i) >= 0) {
    			
    			colsRemain[getColorValue(i)] -= 200;
    			
    		}
    		
    	}
    	
    	// strongly discourages passive play when danger is detected in the next player
    	int[] numberWeights = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    	int[] hands = state.getNumCardsInHandsOfUpcomingPlayers();
    	int[] points = state.getTotalScoreOfUpcomingPlayers();
    	if (hands[0] - hand.size() > 3 || hands[0] < 4 || points[0] >= points[points.length-1]*2) {
    		
    		adjustRankWeights(ranksRemain, numberWeights, 200);
    		ranksRemain[10] -= 800;
    		ranksRemain[11] -= 200;
    		ranksRemain[12] -= 2000;
    		ranksRemain[14] -= 2000;
    		
    	}
    	
    	// strongly discourages skipping next player when distant player may have low cards
    	if (hands[1] < 5 || hands[2] < 5 || hands[1] - hand.size() > 4 || hands[2] - hand.size() > 4) {
    		
    		adjustRankWeights(ranksRemain, numberWeights, -100);
    		ranksRemain[10] += 1000;
    		ranksRemain[11] -= 600;
    		ranksRemain[12] += 2500;
    		ranksRemain[13] -= 400;
    		ranksRemain[14] += 2500;
    		
    		// fully discourages all aggressive play when last player might be more of a threat
        	if (hands[hands.length-2] > hands[hands.length-3] || points[hands.length-2] >= points[points.length-1]*2) {
        		
        		adjustRankWeights(ranksRemain, numberWeights, -100);
        		ranksRemain[10] -= 300;
        		ranksRemain[11] += 600;
        		ranksRemain[12] += 800;
        		ranksRemain[13] -= 400;
        		ranksRemain[14] -= 1000;
        		
        	}
    		
    	}
    	    	
    	// generates a penalty value for each card in hand based on remaining colors, ranks, cost, and predtermined penalties
    	int[] penalty = new int[hand.size()];
    	for (int i = 0; i < penalty.length; i++) {
    		
    		Card c = hand.get(i); int cval;
    		if (c.getColor() == Color.NONE) {
    			cval = colsRemain[getColorValue(callColor(hand))];
    		} else {
    			cval = colsRemain[getColorValue(c.getColor())];
    		}
    		penalty[i] += cval*800 + ranksRemain[getRankValue(c)] - c.forfeitCost()*10;
    		
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

    /**
     * callColor
     * Selects a color to call when this player plays a Wild or Wild D4 card.
     * Selects the color with the most corresponding cards played (i.e. least proability other players have)
     * 
     */
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
    
    /**
     * Get color-indexed count of all colors played (in order {RED YELLOW GREEN BLUE})
     * @param played list of played cards
     * @return int[] of length 4 with counts of each color played
     */
    private int[] playedColorCount(List<Card> played) {
    	
    	int[] colsPlayed = new int[4];
    	for (Card i : played) {
    		
    		if (!i.followedByCall()) {
    			colsPlayed[getColorValue(i.getColor())] ++;
    		}
    		
    	}
    	
    	return colsPlayed;
    	
    }
    
    /**
     * Get rank-indexed count of all ranks played (in order {0 1 2 3 4 5 6 7 8 9 SKIP REVERSE DRAW_TWO WILD WILD_D4})
     * @param played list of played cards
     * @return int[] of length 15 with counts of each rank played
     */
    private int[] playedRankCount(List<Card> played) {
    	
    	int[] ranksPlayed = new int[15];
    	for (Card i : played) {
    		
    		ranksPlayed[getRankValue(i)] ++;
    		
    	}
    	
    	return ranksPlayed;
    	
    }
        
    /**
     * Returns appropriate index given a color
     * @param c valid color enum value
     * @return -1, 0, 1, 2, or 3 depending on the color value (in order {RED YELLOW GREEN BLUE})
     */
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
    
    /**
     * Returns appropriate rank index given a card
     * @param c valid card
     * @return a number -1 through 14 (-1 if error), values 0-9 signify a number rank, 10-14 are in order {SKIP REVERSE DRAW_TWO WILD WILD_D4}
     */
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
    
    /**
     * Checks if a given color exists in a list of cards
     * @param c color to check
     * @param hand list of cards (intended use is for current hand)
     * @return true if the color is present, false otherwise
     */
    private boolean colorInHand(Color c, List<Card> hand) {
    	
    	for (Card i : hand) {
    		
    		if (i.getColor() == c) {
    			
    			return true;
    			
    		}
    		
    	}
    	
    	return false;
    	
    }
    
    /**
     * Helper function to add a parameter to a selection of indices in a given list. Mutator method that is designed to mutate the parameter.
     * @param weights list to be mutated
     * @param indices indices at which values should be altered
     * @param num number to add to each index
     */
    private void adjustRankWeights(int[] weights, int[] indices, int num) {
    	
    	for (int i : indices) {
    		
    		weights[i] += num;
    		
    	}
    	
    }
    
}
