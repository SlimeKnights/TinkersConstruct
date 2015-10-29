package tconstruct.world.itemblocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.world.TinkerWorld;

public class OreberryBushSecondItem extends MultiItemBlock
{
    public Block blockB;
    public static final String blockTypes[] = { "aluminum", "essence", "", "", "aluminum", "essence", "", "", "aluminum", "essence", "", "", "aluminum", "essence", "", "" };

    public OreberryBushSecondItem(Block b)
    {
        super(b, "block.oreberry", blockTypes);
        blockB = b;
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata (int meta)
    {
        return meta % 4;
    }

    /* Place bushes on dirt, grass, or other bushes only */
    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (side != 1)
            return false;

        int meta = stack.getMetadata();
        if (meta % 4 != 1 && world.getFullBlockLightValue(x, y + 1, z) >= 13)
            return false;

        else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack))
        {
            Block block = world.getBlock(x, y, z);

            if (block != null && block.canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable) TinkerWorld.oreBerrySecond) && WorldHelper.isAirBlock(world, x, y + 1, z))
            {
                world.setBlock(x, y + 1, z, blockB, stack.getMetadata() % 4, 3);
                if (!player.capabilities.isCreativeMode)
                    stack.stackSize--;
                if (!world.isRemote)
                    world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block));
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getMetadata() % 4)
        {
        case 0:
            list.add(StatCollector.translateToLocal("oreberrybush5.tooltip"));
            break;
        case 1:
            list.add(StatCollector.translateToLocal("oreberrybush6.tooltip"));
            break;
        }
    }
}
