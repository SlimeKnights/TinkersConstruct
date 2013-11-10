package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TerraformerItem extends ItemBlock
{
    static String blockType[] = { "freezer", "fumer", "waver", "leecher", "grower", "nether", "lighter", "crystal" };

    //static String[] textureNames = { "crystal_machine_top", "terrafreezer", "terrafumer", "terrawaver", "terraleecher", "terragrower", "terranether", "terralighter", "terracrystal" };

    public TerraformerItem(int id)
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
        return (new StringBuilder()).append("tile.terraformer.").append(blockType[pos]).toString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add("\u00a7b\u00a7oCold to the touch");
            break;
        case 1:
            list.add("\u00a74\u00a7oHot to the touch");
            break;
        }
    }
}
