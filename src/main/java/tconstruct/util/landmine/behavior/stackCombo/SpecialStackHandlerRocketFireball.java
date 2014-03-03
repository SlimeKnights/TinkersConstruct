package tconstruct.util.landmine.behavior.stackCombo;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpecialStackHandlerRocketFireball extends SpecialStackHandler
{

    @Override
    public void checkStack (World par1World, int par2, int par3, int par4, Entity triggerer, ArrayList<ItemStack> stacks)
    {
        if (stacks.isEmpty())
        {
            return;
        }

        if (this.arrayContainsEqualStack(stacks, new ItemStack(Item.firework)) && this.arrayContainsEqualStack(stacks, new ItemStack(Item.fireballCharge)))
        {
            int index0 = this.arrayIndexOfStack(stacks, new ItemStack(Item.firework));
            int index1 = this.arrayIndexOfStack(stacks, new ItemStack(Item.fireballCharge));
            EnumFacing face = getFacing(par1World, par2, par3, par4);

            while (stacks.get(index0).stackSize > 0 && stacks.get(index1).stackSize > 0)
            {
                double d0 = par2 + (double) ((float) face.getFrontOffsetX() * 0.3F);
                double d1 = par3 + (double) ((float) face.getFrontOffsetX() * 0.3F);
                double d2 = par4 + (double) ((float) face.getFrontOffsetZ() * 0.3F);
                Random random = par1World.rand;
                double d3 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetX();
                double d4 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetY();
                double d5 = random.nextGaussian() * 0.05D + (double) face.getFrontOffsetZ();
                EntityFireball fireball = new EntityLargeFireball(par1World, d0, d1, d2, d3, d4, d5);
                par1World.spawnEntityInWorld(fireball);

                if (triggerer instanceof EntityLivingBase)
                {
                    boolean shouldRun = true;
                    if (triggerer instanceof EntityPlayer)
                    {
                        EntityPlayer player = (EntityPlayer) triggerer;
                        if (player.capabilities.isCreativeMode)
                        {
                            // Remove the bellow comment to make the player in
                            // creative mode not get launched
                            // shouldRun = false;
                        }
                    }

                    if (shouldRun)
                    {
                        triggerer.mountEntity(fireball);
                    }
                }

                this.removeItemFromInventory(par1World, par2, par3, par4, new ItemStack(Item.firework, 1));
                stacks.get(index0).stackSize--;
                this.removeItemFromInventory(par1World, par2, par3, par4, new ItemStack(Item.fireballCharge, 1));
                stacks.get(index1).stackSize--;
            }
        }
    }
}
