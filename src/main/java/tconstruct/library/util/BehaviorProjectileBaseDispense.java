package tconstruct.library.util;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.weaponry.IAmmo;

public abstract class BehaviorProjectileBaseDispense extends BehaviorDefaultDispenseItem {
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack)
    {
        World world = blockSource.getWorld();
        IPosition iposition = BlockDispenser.func_149939_a(blockSource);
        EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());

        ItemStack reference;

        if(stack.getItem() instanceof IAmmo)
        {
            IAmmo ammo = (IAmmo) stack.getItem();
            // needs ammo to shoot
            if(ammo.getAmmoCount(stack) <= 0)
                return stack;
            ammo.consumeAmmo(1, stack);
            reference = stack.copy();
            ((IAmmo)reference.getItem()).setAmmo(1, reference);
        }
        else
            reference = stack.splitStack(1);

        ProjectileBase projectile = this.getProjectileEntity(world, iposition, reference);
        projectile.setThrowableHeading((double)enumfacing.getFrontOffsetX(), (double)((float)enumfacing.getFrontOffsetY() + ballistic()), (double)enumfacing.getFrontOffsetZ(), this.accuraccy(), this.speed());
        projectile.returnStack = reference;
        projectile.canBePickedUp = 1;
        world.spawnEntityInWorld(projectile);

        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource p_82485_1_)
    {
        p_82485_1_.getWorld().playAuxSFX(1002, p_82485_1_.getXInt(), p_82485_1_.getYInt(), p_82485_1_.getZInt(), 0);
    }

    /**
     * Return the projectile entity spawned by this dispense behavior.
     */
    protected abstract ProjectileBase getProjectileEntity(World world, IPosition position, ItemStack stack);

    protected float speed()
    {
        return 6.0F;
    }

    protected float accuraccy()
    {
        return 1.1F;
    }

    protected float ballistic() { return 0.1f; }
}
