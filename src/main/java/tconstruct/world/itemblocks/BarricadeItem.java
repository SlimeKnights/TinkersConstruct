package tconstruct.world.itemblocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class BarricadeItem extends ItemBlock
{
    private Block b;

    public BarricadeItem(Block b)
    {
        super(b);
        this.b = b;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("barricade.tooltip"));
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        Block b = world.getBlock(x, y, z);

        if (b == Blocks.snow && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (b != Blocks.vine && b != Blocks.tallgrass && b != Blocks.deadbush && (b == null || !b.canPlaceBlockAt(world, x, y, z)))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        else if (y == 255 && this.b.getMaterial().isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(this.b, x, y, z, false, side, player, stack))
        {
            Block block = this.b;
            // int meta = this.getMetadata(stack.getItemDamage());
            int rotation = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            int meta = rotation * 4;
            int metadata = this.b.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);

            if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.soundName, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
