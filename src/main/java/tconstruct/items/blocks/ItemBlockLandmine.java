package tconstruct.items.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.StatCollector;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.common.TRepo;

/**
 * 
 * @author fuj1n
 *
 */
public class ItemBlockLandmine extends ItemBlock
{

    public ItemBlockLandmine(Block b)
    {
        super(b);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation (ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        String interaction = null;

        switch (par1ItemStack.getItemDamage())
        {
        case 0:
            interaction = StatCollector.translateToLocal("landmine1.tooltip");
            break;
        case 1:
            interaction = StatCollector.translateToLocal("landmine2.tooltip");
            break;
        case 2:
            interaction = StatCollector.translateToLocal("landmine3.tooltip");
            break;
        default:
            interaction = StatCollector.translateToLocal("landmine4.tooltip");
            break;
        }

       par3List.add(StatCollector.translateToLocal("landmine5.tooltip") + interaction);
    }

    @Override
    public int getMetadata (int par1)
    {
        return 0;
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.func_147465_d(x, y, z, TRepo.landmine, metadata, 3))
        {
            return false;
        }

        if (world.func_147439_a(x, y, z) == TRepo.landmine)
        {
            TRepo.landmine.func_149689_a(world, x, y, z, player, stack);

            TileEntityLandmine te = (TileEntityLandmine) world.getBlockTileEntity(x, y, z);
            if (te == null)
            {
                te = (TileEntityLandmine) TRepo.landmine.createTileEntity(world, metadata);
            }

            te.triggerType = stack.getItemDamage();
            world.setBlockTileEntity(x, y, z, te);

            TRepo.landmine.func_149714_e(world, x, y, z, metadata);
        }

        return true;
    }

    public static Random getRandom ()
    {
        return itemRand;
    }

}
