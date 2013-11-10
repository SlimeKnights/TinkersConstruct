package tconstruct.library.signal;

import tconstruct.library.util.CoordTuple;
import net.minecraft.world.World;

public interface ISignalTransceiver
{

    void setBusCoords (World world, int xCoord, int yCoord, int zCoord);

    CoordTuple getBusCoords ();

    byte[] getReceivedSignals ();

    void receiveSignalUpdate (byte[] signals);

    int doUnregister (boolean reHoming);

    int getDroppedWire ();

}
