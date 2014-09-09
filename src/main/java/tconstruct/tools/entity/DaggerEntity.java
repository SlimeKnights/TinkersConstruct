package tconstruct.tools.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.items.tools.Dagger;
import tconstruct.library.tools.*;

public class DaggerEntity extends RotatingBase
{

    public DaggerEntity(World world)
    {
        super(world);
    }

    public DaggerEntity(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        super(world, entityplayer, 0.75F, 0.8F);
        // TConstruct.logger.info("Stack: "+itemstack);
        returnStackSlot = entityplayer.inventory.currentItem;
        returnStack = itemstack;
    }

    public DaggerEntity(World world, EntityPlayer entityplayer, float f, float g)
    {
        super(world, entityplayer, f, g);
    }

    /*
     * public void entityInit () { super.entityInit(); }
     */

    @Override
    public void onHit (MovingObjectPosition movingobjectposition)
    {
        if (movingobjectposition.entityHit != null)
        {
            if (movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeMobDamage(owner), damageDealt))
            {
                worldObj.playSoundAtEntity(this, "random.drr", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
                motionX *= -0.1D;
                motionY *= -0.1D;
                motionZ *= -0.1D;
                rotationYaw += 180F;
                prevRotationYaw += 180F;
                ticksInAir = 0;
                if (movingobjectposition.entityHit instanceof EntityLiving)
                {
                    Dagger dagger = (Dagger) returnStack.getItem();
                    this.hitEntity(returnStack, (EntityLiving) movingobjectposition.entityHit, owner, dagger);
                }
            }
        }
        else
        {
            onGround = true;
            xTile = movingobjectposition.blockX;
            yTile = movingobjectposition.blockY;
            zTile = movingobjectposition.blockZ;
            inTile = worldObj.getBlock(xTile, yTile, zTile);
            motionX = (float) (movingobjectposition.hitVec.xCoord - posX);
            motionY = (float) (movingobjectposition.hitVec.yCoord - posY);
            motionZ = (float) (movingobjectposition.hitVec.zCoord - posZ);
            float f = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            posX -= (motionX / (double) f) * 0.05D;
            posY -= (motionY / (double) f) * 0.05D;
            posZ -= (motionZ / (double) f) * 0.05D;
            worldObj.playSoundAtEntity(this, "random.drr", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
            arrowShake = 7;
            if (!worldObj.isRemote)
                AbilityHelper.damageTool(returnStack, 1, owner, false);
        }
    }

    public boolean hitEntity (ItemStack stack, EntityLiving mob, EntityPlayer player, ToolCore weapon)
    {
        if (!worldObj.isRemote && player.canAttackWithItem())
        {
            AbilityHelper.onLeftClickEntity(stack, player, mob, weapon);
        }
        return true;
    }

    @Override
    protected void dealFireDamage (int i)
    {
    }
}
