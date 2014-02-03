package tconstruct.items.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class CraftedSoilItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Slime", "Grout", "BlueSlime", "GraveyardSoil", "ConsecratedSoil", "blue", "dirt", "Grout" };

    public CraftedSoilItemBlock(Block b)
    {
        super(b, "CraftedSoil", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, this.blockType.length - 1);
        if (pos <= 4)
            return super.getUnlocalizedName(itemstack);
        return (new StringBuilder()).append("block.slime.soil.").append(this.blockType[pos]).toString();
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
