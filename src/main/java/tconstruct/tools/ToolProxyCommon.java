package tconstruct.tools;

import cpw.mods.fml.common.network.IGuiHandler;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.common.TProxyCommon;

public class ToolProxyCommon implements IGuiHandler
{
    public static final int toolStationID = 0;
    public static final int partBuilderID = 1;
    public static final int patternChestID = 2;
    public static final int stencilTableID = 3;
    public static final int frypanGuiID = 4;
    public static final int toolForgeID = 5;
    public static final int furnaceID = 8;
    public static final int craftingStationID = 11;

    public ToolProxyCommon()
    {
    }

    public void initialize ()
    {
        registerGuiHandler();
    }

    protected void registerGuiHandler ()
    {
        TProxyCommon.registerServerGuiHandler(toolStationID, this);
        TProxyCommon.registerServerGuiHandler(partBuilderID, this);
        TProxyCommon.registerServerGuiHandler(patternChestID, this);
        TProxyCommon.registerServerGuiHandler(stencilTableID, this);
        TProxyCommon.registerServerGuiHandler(frypanGuiID, this);
        TProxyCommon.registerServerGuiHandler(toolForgeID, this);
        TProxyCommon.registerServerGuiHandler(furnaceID, this);
        TProxyCommon.registerServerGuiHandler(craftingStationID, this);
    }

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof InventoryLogic)
        {
            return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
