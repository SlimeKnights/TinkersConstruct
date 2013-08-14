package mods.tinker.tconstruct.items.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class SlimeTallGrassItem extends ItemBlock
{
    public static final String blockType[] = { "tallgrass", "tallgrass.fern" };

    public SlimeTallGrassItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        int arr = MathHelper.clamp_int(meta, 0, blockType.length);
        return TContent.slimeTallGrass.getIcon(0, arr);
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.slime.").append(blockType[pos]).toString();
    }
}
