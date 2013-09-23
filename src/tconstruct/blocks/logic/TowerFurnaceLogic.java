package tconstruct.blocks.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.library.blocks.AdaptiveInventoryLogic;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;

public class TowerFurnaceLogic extends AdaptiveInventoryLogic implements IActiveLogic, IFacingLogic, IMasterLogic
{

    public TowerFurnaceLogic()
    {
        super();
    }

    @Override
    public void notifyChange (int x, int y, int z)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public byte getRenderDirection ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDirection (int side)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getActive ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setActive (boolean flag)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        return null;
    }

}
