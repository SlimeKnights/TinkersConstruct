package tinker.tconstruct.blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class TBaseOreItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "cobalt", "ardite"
    };

    public TBaseOreItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
    	 int arrayPos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, 1);
        return new StringBuilder().append("block.tconstruct").append(blockType[arrayPos]).toString();
    }
}
