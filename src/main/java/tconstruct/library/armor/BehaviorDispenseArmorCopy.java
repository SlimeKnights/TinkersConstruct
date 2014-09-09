package tconstruct.library.armor;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.command.IEntitySelector;
import net.minecraft.dispenser.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

final class BehaviorDispenseArmorCopy extends BehaviorDefaultDispenseItem
{
    /**
     * Dispense the specified stack, play the dispense sound and spawn
     * particles.
     */
    @Override
    protected ItemStack dispenseStack (IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());
        int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
        int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
        int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
        List list = par1IBlockSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new IEntitySelector.ArmoredMob(par2ItemStack));

        if (list.size() > 0)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) list.get(0);
            int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
            int i1 = EntityLiving.getArmorPosition(par2ItemStack);
            ItemStack itemstack1 = par2ItemStack.copy();
            itemstack1.stackSize = 1;
            entitylivingbase.setCurrentItemOrArmor(i1, itemstack1); // BUGFIX
                                                                    // Forge:
                                                                    // Vanilla
                                                                    // bug fix
                                                                    // associated
                                                                    // with
                                                                    // fixed
                                                                    // setCurrentItemOrArmor
                                                                    // indexs
                                                                    // for
                                                                    // players.

            if (entitylivingbase instanceof EntityLiving)
            {
                ((EntityLiving) entitylivingbase).setEquipmentDropChance(i1, 2.0F);
            }

            --par2ItemStack.stackSize;
            return par2ItemStack;
        }
        else
        {
            return super.dispenseStack(par1IBlockSource, par2ItemStack);
        }
    }
}