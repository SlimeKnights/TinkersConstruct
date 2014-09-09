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
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        int meta = world.getBlockMetadata(x, y, z);
        Block block = player.worldObj.getBlock(x, y, z);
        if (block == null || block == Blocks.air)
            return false;
        int hlvl = -1;
        int shlvl = -1;
        if (block.getHarvestTool(meta) == null || block.getHarvestTool(meta).equals(getHarvestType()))
            hlvl = block.getHarvestLevel(meta);
        if (block.getHarvestTool(meta) == null || block.getHarvestTool(meta).equals(getSecondHarvestType()))
            shlvl = block.getHarvestLevel(meta);

        if (hlvl <= tags.getInteger("HarvestLevel") && shlvl <= tags.getInteger("HarvestLevel2"))
        {
            boolean cancelHarvest = false;
            for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
            {
                if (mod.beforeBlockBreak(this, stack, x, y, z, player))
                    cancelHarvest = true;
            }

            return cancelHarvest;
        }
        else
        {
            if (!player.capabilities.isCreativeMode)
            {
                mineBlock(world, x, y, z, meta, player, block);
            }
            WorldHelper.setBlockToAir(world, x, y, z);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            return true;
        }
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
