package gamestates;

import audio.AudioPlayer;
import main.Game;
import ui.MenuButton;

import java.awt.event.MouseEvent;

public class State {

    protected Game game;

    public State(Game game){
        this.game = game;

    }

    //this gives if the mouse is hovering over the button area..it's a boolean btw
    public boolean isIn(MouseEvent e , MenuButton mb){
        return  mb.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame() {
        return game;
    }

    public void setGamestate(GameState state) {
        switch (state) {
            case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
            case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLvlIndex());
        }

        GameState.state = state;
    }
}
