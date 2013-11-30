package tconstruct.items.blocks;

import java.util.List;

import tconstruct.common.TRepo;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreberryBushItem extends ItemBlock
{
    public int blockID;

    public OreberryBushItem(int id)
    {
        super(id);
        blockID = id + 256;
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata (int meta)
    {
        return meta % 4;
    }

    /* Place bushes on dirt, grass, or other bushes only */
    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        if (side != 1)
            return false;

        else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack))
        {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];

            if (block != null && block.canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable) TRepo.oreBerry) && world.isAirBlock(x, y + 1, z))
            {
                world.setBlock(x, y + 1, z, blockID, stack.getItemDamage() % 4, 3);
                if (!player.capabilities.isCreativeMode)
                    stack.stackSize--;
                if (!world.isRemote)
                    world.playAuxSFX(2001, x, y, z, blockID);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /* Block name in inventory */
    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.oreberry.").append(blockType[pos]).toString();
    }

    public static final String blockType[] = { "iron", "gold", "copper", "tin", "iron", "gold", "copper", "tin", "iron", "gold", "copper", "tin", "iron", "gold", "copper", "tin" };

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage() % 4)
        {
        case 0:
            list.add(StatCollector.translateToLocal("oreberrybush1.tooltip"));
            break;
        case 1:
            list.add(StatCollector.translateToLocal("oreberrybush2.tooltip"));
            break;
        case 2:
            list.add(StatCollector.translateToLocal("oreberrybush3.tooltip"));
            break;
        case 3:
            list.add(StatCollector.translateToLocal("oreberrybush4.tooltip"));
            break;
        }
    }
}
