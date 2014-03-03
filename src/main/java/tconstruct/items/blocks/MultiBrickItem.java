package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MultiBrickItem extends ItemBlock
{
    static String blockType[] = { "obsidian", "sandstone", "netherrack", "stone.refined", "iron", "gold", "lapis", "diamond", "redstone", "bone", "slime", "blueslime", "endstone", "obsidian.ingot",
            "stone.road", "stone.refined.road" };

    public MultiBrickItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.brick.").append(blockType[pos]).toString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 5:
        case 6:
            list.add(StatCollector.translateToLocal("brick1.tooltip"));
            break;
        case 7:
            list.add(StatCollector.translateToLocal("brick2.tooltip"));
            break;
        case 8:
            list.add(StatCollector.translateToLocal("brick3.tooltip"));
            list.add(StatCollector.translateToLocal("brick4.tooltip"));
            break;
        case 10:
        case 11:
            list.add(StatCollector.translateToLocal("brick5.tooltip"));
            break;
        }
    }
}
