package tconstruct.blocks.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.common.TContent;
import tconstruct.library.blocks.AdaptiveInventoryLogic;
import tconstruct.library.component.IComponentHolder;
import tconstruct.library.component.LogicComponent;
import tconstruct.library.component.MultiFluidTank;
import tconstruct.library.component.SmelteryComponent;
import tconstruct.library.component.TankLayerScan;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IMasterLogic;

public class AdaptiveSmelteryLogic extends AdaptiveInventoryLogic implements IActiveLogic, IMasterLogic, IComponentHolder
{
    byte direction;
    TankLayerScan structure = new TankLayerScan(this, TContent.smeltery, TContent.lavaTank);
    MultiFluidTank multitank = new MultiFluidTank();
    SmelteryComponent smeltery = new SmelteryComponent(this, this.worldObj, multitank, 800);

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {

    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing)
        {
        case 0:
            direction = 2;
            break;

        case 1:
            direction = 5;
            break;

        case 2:
            direction = 3;
            break;

        case 3:
            direction = 4;
            break;
        }
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
    public void setNeedsUpdate ()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultName ()
    {
        return "crafters.Smeltery";
    }

    @Override
    public List<LogicComponent> getComponents ()
    {
        ArrayList<LogicComponent> ret = new ArrayList<LogicComponent>(1);
        ret.add(structure);
        ret.add(multitank);
        ret.add(smeltery);
        return ret;
    }

    @Override
    public void notifyChange (int x, int y, int z)
    {
        // TODO Auto-generated method stub
        
    }
}
