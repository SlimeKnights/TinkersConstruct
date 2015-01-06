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
public class PlayerID {

    private String name;

    private PlayerID(String _name) {name = _name;}

    public PlayerID(EntityPlayer player) {
        this(player.getDisplayName());
    }

    public String getName() {return name;}

    public static class PlayerIDStreamer extends Zeno410Utils.Streamer<PlayerID> {

        public PlayerID readFrom(DataInput input) throws IOException {
           String result = input.readUTF();
           return new PlayerID(result);
        }
        public void writeTo(PlayerID written, DataOutput output) throws IOException {
            output.writeUTF(written.name);

        }
    }

    // convenience function for player specific things
    public <Type> PlayerSpecific<Type> specific(Type type) {
        return new PlayerSpecific<Type>(this,type);
    }

    @Override
    public int hashCode() {return name.hashCode();}
    
    @Override
    public boolean equals(Object compared) {
        if (compared == null) return false;
        if (compared instanceof PlayerID) {
            if (((PlayerID)compared).name.equals(name)) return true;
        }
        return false;
    }
    

}
