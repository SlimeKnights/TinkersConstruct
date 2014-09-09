package tconstruct.library.tools;

import mantle.blocks.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class FletchlingLeafMaterial extends FletchingMaterial
{
    public FletchlingLeafMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        super(materialID, value, oredict, craftingItem, accuracy, breakChance, mass);
    }

    @Override
    public boolean matches (ItemStack stack)
    {
        if (matchesLeaves(stack))
            return true;

        return super.matches(stack);
    }

    public static boolean matchesLeaves (ItemStack stack)
    {
        if (stack != null)
        {
            Block block = BlockUtils.getBlockFromItemStack(stack);
            if (block != null)
            {
                if (block.isLeaves(null, 0, 0, 0))
                    return true;
            }
        }
        return false;
    }
}
