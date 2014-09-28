package tconstruct.mechworks.itemblocks;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.logic.TileEntityLandmine;

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
        if (!world.setBlock(x, y, z, TinkerMechworks.landmine, metadata, 3))
        {
            return false;
        }

        if (world.getBlock(x, y, z) == TinkerMechworks.landmine)
        {
            TinkerMechworks.landmine.onBlockPlacedBy(world, x, y, z, player, stack);

            TileEntityLandmine te = (TileEntityLandmine) world.getTileEntity(x, y, z);
            if (te == null)
            {
                te = (TileEntityLandmine) TinkerMechworks.landmine.createTileEntity(world, metadata);
            }

            te.triggerType = stack.getItemDamage();
            world.setTileEntity(x, y, z, te);

            TinkerMechworks.landmine.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

    public static Random getRandom ()
    {
        return itemRand;
    }

}
