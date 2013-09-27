package tconstruct.util;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tconstruct.entity.projectile.ArrowEntity;

public class TDispenserBehaviorArrow extends BehaviorDefaultDispenseItem
{

    public ItemStack dispenseStack(IBlockSource dispenser, ItemStack stack)
    {
        World world = dispenser.getWorld();
        IPosition iposition = BlockDispenser.getIPositionFromBlockSource(dispenser);
        EnumFacing enumfacing = BlockDispenser.getFacing(dispenser.getBlockMetadata());
        
        ItemStack arrowItem = stack.splitStack(1);

        ArrowEntity projectile = new ArrowEntity(world, iposition.getX(), iposition.getY(), iposition.getZ(), arrowItem);
        projectile.canBePickedUp = 1;
        projectile.setThrowableHeading((double)enumfacing.getFrontOffsetX(), (double)((float)enumfacing.getFrontOffsetY() + 0.1F), (double)enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld(projectile);
        
        return stack;
    }

    protected void playDispenseSound(IBlockSource dispenser)
    {
        dispenser.getWorld().playAuxSFX(1002, dispenser.getXInt(), dispenser.getYInt(), dispenser.getZInt(), 0);
    }

    protected float func_82498_a()
    {
        return 6.0F;
    }

    protected float func_82500_b()
    {
        return 1.1F;
    }
}