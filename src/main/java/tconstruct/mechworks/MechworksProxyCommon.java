package tconstruct.mechworks;

import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class MechworksProxyCommon implements IGuiHandler
{

    // public static int drawbridgeID = 9; // Moved to TMechworks
    public static final int landmineID = 10;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == MechworksProxyCommon.landmineID)
        {
            return new ContainerLandmine(player, (TileEntityLandmine) world.getTileEntity(x, y, z));
        }

        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
