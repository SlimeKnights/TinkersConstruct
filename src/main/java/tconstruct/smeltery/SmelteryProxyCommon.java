package tconstruct.smeltery;

import cpw.mods.fml.common.network.IGuiHandler;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.common.TProxyCommon;

public class SmelteryProxyCommon implements IGuiHandler
{
    public static final int smelteryGuiID = 7;

    public void initialize ()
    {
        registerGuiHandler();
    }

    protected void registerGuiHandler ()
    {
        TProxyCommon.registerServerGuiHandler(smelteryGuiID, this);
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
        // TODO Auto-generated method stub
        return null;
    }

}
