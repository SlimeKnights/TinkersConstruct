package tconstruct.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.library.blocks.AdaptiveInventoryLogic;

public class AdaptiveSmelteryLogic extends AdaptiveInventoryLogic
{

    @Override
    public byte getRenderDirection ()
    {
        return 0;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public void setDirection (int side)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Deprecated
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected boolean isValidBlock (int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
