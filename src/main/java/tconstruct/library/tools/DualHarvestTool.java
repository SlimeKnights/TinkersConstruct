package tconstruct.library.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.*;

/* Base class for harvest tools with each head having a different purpose */

public abstract class DualHarvestTool extends HarvestTool
{
    public DualHarvestTool(int baseDamage)
    {
        super(baseDamage);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        // well, we can only get the harvestlevel if we have an item to get it from!
        if(stack == null || !(stack.getItem() instanceof HarvestTool))
            return -1;
        // invalid query or wrong toolclass
        if(toolClass == null)
            return -1;

        if(!stack.hasTagCompound())
            return -1;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        // broken tools suck.
        if (tags.getBoolean("Broken"))
            return -1;

        if(this.getHarvestType().equals(toolClass))
            return tags.getInteger("HarvestLevel");
        else if(this.getSecondHarvestType().equals(toolClass))
            return tags.getInteger("HarvestLevel2");

        return -1;
    }

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.getMaterial())
            {
                float speed = tags.getInteger("MiningSpeed");
                speed /= 100f;
                int hlvl = block.getHarvestLevel(meta);
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return speed;
                return 0.1f;
            }
        }
        materials = getEffectiveSecondaryMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.getMaterial())
            {
                float speed = tags.getInteger("MiningSpeed2");
                speed /= 100f;
                int hlvl = block.getHarvestLevel(meta);
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel2"))
                    return speed;
                return 0.1f;
            }
        }
        return super.getDigSpeed(stack, block, meta);
    }

    @Override
    public boolean func_150897_b (Block block)
    {
        if (block.getMaterial().isToolNotRequired())
            return true;

        return isEffective(block.getMaterial());
    }

    @Override
    public boolean canHarvestBlock (Block block, ItemStack itemStack)
    {
        return func_150897_b(block);
    }

    @Override
    public boolean isEffective (Material material)
    {
        if (super.isEffective(material))
            return true;

        for (Material m : getEffectiveSecondaryMaterials())
            if (m == material)
                return true;

        return false;
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "harvest", "dualharvest" };
    }

    protected abstract Material[] getEffectiveSecondaryMaterials ();

    protected abstract String getSecondHarvestType ();
}
