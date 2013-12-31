package tconstruct.library.tools;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
        int bID = player.worldObj.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1)
            return false;
        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
        int shlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getSecondHarvestType());

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
                onBlockDestroyed(stack, world, bID, x, y, z, player);
            world.setBlockToAir(x, y, z);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
            return true;
        }
    }

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.blockMaterial)
            {
                float speed = tags.getInteger("MiningSpeed");
                speed /= 100f;
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
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
            if (materials[i] == block.blockMaterial)
            {
                float speed = tags.getInteger("MiningSpeed2");
                speed /= 100f;
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel2"))
                    return speed;
                return 0.1f;
            }
        }
        return super.getStrVsBlock(stack, block, meta);
    }

    public boolean canHarvestBlock (Block block)
    {
        if (block.blockMaterial.isToolNotRequired())
        {
            return true;
        }
        for (Material m : getEffectiveMaterials())
        {
            if (m == block.blockMaterial)
                return true;
        }
        for (Material m : getEffectiveSecondaryMaterials())
        {
            if (m == block.blockMaterial)
                return true;
        }
        return false;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "harvest", "dualharvest" };
    }

    protected abstract Material[] getEffectiveSecondaryMaterials ();

    protected abstract String getSecondHarvestType ();
}
