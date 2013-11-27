package tconstruct.items.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class CraftedSoilItemBlock extends ItemBlock
{
    public static final String blockType[] = { "Slime", "Grout", "BlueSlime", "GraveyardSoil", "ConsecratedSoil", "blue", "dirt" };

    public CraftedSoilItemBlock(int id)
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
        if (pos <= 4)
            return (new StringBuilder()).append("CraftedSoil.").append(blockType[pos]).toString();
        return (new StringBuilder()).append("block.slime.soil.").append(blockType[pos]).toString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 3:
            list.add(StatCollector.translateToLocal("craftedsoil1.tooltip"));
            break;
        case 4:
            list.add(StatCollector.translateToLocal("craftedsoil2.tooltip"));
            break;
        }
    }
}
