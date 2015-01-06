/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Zeno410Utils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 * @author Zeno410
 */
public class PlayerSpecific<Type> {

    private final PlayerID player;
    private final Type item;
    public PlayerSpecific(PlayerID _player, Type _item) {
        player = _player;
        item = _item;
    }

    public PlayerSpecific(EntityPlayer player, Type item) {
        this (new PlayerID(player),item);
    }

    public final PlayerID player() {return player;}
    public final Type item() {return item;}
    
    public static <StreamType> Streamer<PlayerSpecific<StreamType>> streamer(
            final Streamer<StreamType> substreamer) {
        
        return new Streamer<PlayerSpecific<StreamType>>() {
            PlayerID.PlayerIDStreamer playerStreamer = new PlayerID.PlayerIDStreamer();

            public PlayerSpecific<StreamType> readFrom(DataInput input) throws IOException {
                PlayerID player =playerStreamer.readFrom(input);
                return new PlayerSpecific<StreamType>(player,substreamer.readFrom(input));
            }

            public void writeTo(PlayerSpecific<StreamType> written, DataOutput output) throws IOException {
                playerStreamer.writeTo(written.player, output);
                substreamer.writeTo(written.item, output);
            }
        };
    }

    @Override
    public int hashCode() {return item.hashCode() + player.getName().hashCode();}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final PlayerSpecific<Type> other = (PlayerSpecific<Type>) obj;
        if (this.player != other.player && (this.player == null || !this.player.equals(other.player))) {
            return false;
        }
        if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
            return false;
        }
        return true;
    }

}
