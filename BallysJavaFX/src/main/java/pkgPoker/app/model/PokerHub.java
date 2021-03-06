package pkgPoker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import netgame.common.Hub;
import pkgPokerBLL.Action;
import pkgPokerBLL.Card;
import pkgPokerBLL.CardDraw;
import pkgPokerBLL.Deck;
import pkgPokerBLL.GamePlay;
import pkgPokerBLL.GamePlayPlayerHand;
import pkgPokerBLL.Player;
import pkgPokerBLL.Rule;
import pkgPokerBLL.Table;

import pkgPokerEnum.eAction;
import pkgPokerEnum.eCardDestination;
import pkgPokerEnum.eDrawCount;
import pkgPokerEnum.eGame;
import pkgPokerEnum.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action) message;
			switch (act.getAction()) {
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case Leave:			
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case StartGame:
				// Get the rule from the Action object.
				Rule rle = new Rule(act.geteGame());
				Player DealerID = actPlayer;
				
				if (DealerID.getPlayerID() == null){
					Random generator = new Random();
					Player[] values = (Player[]) HubPokerTable.getHmPlayer().values().toArray();
					Player randomPlayer = (Player) values[generator.nextInt(values.length)];
					
					DealerID = randomPlayer;
				}

				// Lab #5 - Start the new instance of GamePlay
								
				HubGamePlay = GamePlay(rle, DealerID.getPlayerID());
				HubGamePlay.setGamePlayers(HubPokerTable.getHmPlayer());
				// Add Players to Game
				
				
				// Set the order of players
				


			case Draw:

				eDrawCount last =  HubGamePlay.geteDrawCountLast();
				HubGamePlay.seteDrawCountLast(last.geteDrawCount(last.getDrawNo()+1));
				for(Player p: HubPokerTable.getHmPlayer().values()){
					HubGamePlay.drawCard(p, eCardDestination.Player);
				}
				
								
				HubGamePlay.isGameOver();
				
				resetOutput();
				//	Send the state of the gameplay back to the clients
				sendToAll(HubGamePlay);
				break;
			case ScoreGame:
				// Am I at the end of the game?

				resetOutput();
				sendToAll(HubGamePlay);
				break;
			}
			
		}

	}

	private GamePlay GamePlay(Rule rle, UUID randomUUID) {
		// TODO Auto-generated method stub
		return null;
	}

}