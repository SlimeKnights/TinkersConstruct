package tconstruct.mechworks.landmine;

import net.minecraft.block.Block;
import net.minecraft.item.*;

public class LandmineStack
{

    public final boolean isBlock;
    public final Block block;
    public final Item item;
    public final int meta;

    public LandmineStack(Block block)
    {
        this(block, -314159265);
    }

    public LandmineStack(Item item)
    {
        this(item, -314159265);
    }

    public LandmineStack(Block block, int meta)
    {
        isBlock = true;
        this.block = block;
        this.item = null;
        this.meta = meta;
    }

    public LandmineStack(Item item, int meta)
    {
        isBlock = false;
        this.block = null;
        this.item = item;
        this.meta = meta;
    }

    @Override
    public boolean equals (Object o)
    {
        // Comparing landmine stacks
        if (o instanceof LandmineStack)
        {
            LandmineStack stack = (LandmineStack) o;
            if (isBlock)
            {
                return block == stack.block && (meta == stack.meta || meta == -314159265);
            }
            else
            {
                return item == stack.item && (meta == stack.meta || meta == -314159265);
            }
            // Comparing landmine stacks with item stacks
        }
        else if (o instanceof ItemStack)
        {
            ItemStack stack = (ItemStack) o;
            if (isBlock && stack.getItem() instanceof ItemBlock)
            {
                return block == ((ItemBlock) stack.getItem()).field_150939_a && (meta == stack.getItemDamage() || meta == -314159265);
            }
            else if (!isBlock)
            {
                return item == stack.getItem() && (meta == stack.getItemDamage() || meta == -314159265);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

}
