package tconstruct.library.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;

/* Base class for tools that should be harvesting blocks */

public abstract class HarvestTool extends ToolCore
{
    public HarvestTool(int baseDamage)
    {
        super(baseDamage);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        Block block = player.worldObj.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        //Block block = Block.blocksList[bID];
        if (block == null || block == Blocks.air)
            return false;
        int hlvl = -1;
        if (!(tags.getBoolean("Broken")))
        {
            Block localBlock = world.getBlock(x, y, z);
            int localMeta = world.getBlockMetadata(x, y, z);
            if (block.getHarvestTool(meta).equals(this.getHarvestType()))
                hlvl = block.getHarvestLevel(meta);
            int toolLevel = tags.getInteger("HarvestLevel");
            float blockHardness = block.getBlockHardness(world, x, y, z);
            float localHardness = localBlock == null ? Float.MAX_VALUE : localBlock.getBlockHardness(world, x, y, z);

            if (hlvl <= toolLevel && localHardness - 1.5 <= blockHardness)
            {
                boolean cancelHarvest = false;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    if (mod.beforeBlockBreak(this, stack, x, y, z, player))
                        cancelHarvest = true;
                }

                if (!cancelHarvest)
                {
                    if (localBlock != null && !(localHardness < 0))
                    {
                        for (int iter = 0; iter < getEffectiveMaterials().length; iter++)
                        {
                            if (getEffectiveMaterials()[iter] == localBlock.getMaterial() || localBlock == Blocks.monster_egg)
                            {
                                if (!player.capabilities.isCreativeMode)
                                {
                                    if (localBlock.removedByPlayer(world, player, x, y, z))
                                    {
                                        localBlock.onBlockDestroyedByPlayer(world, x, y, z, localMeta);
                                    }
                                    localBlock.harvestBlock(world, player, x, y, z, localMeta);
                                    localBlock.onBlockHarvested(world, x, y, z, localMeta, player);
                                    if (blockHardness > 0f)
                                        onBlockDestroyed(stack, world, localBlock, x, y, z, player);
                                }
                                else
                                {
                                    WorldHelper.setBlockToAir(world, x, y, z);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!world.isRemote)
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
        return true;

    }

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.getMaterial())
            {
                return calculateStrength(tags, block, meta);
            }
        }
        if (block.getHarvestLevel(meta) > 0)
        {
            return calculateStrength(tags, block, meta); //No issue if the harvest level is too low
        }
        return super.getDigSpeed(stack, block, meta);
    }

    float calculateStrength (NBTTagCompound tags, Block block, int meta)
    {
        float mineSpeed = tags.getInteger("MiningSpeed");
        int heads = 1;
        if (tags.hasKey("MiningSpeed2"))
        {
            mineSpeed += tags.getInteger("MiningSpeed2");
            heads++;
        }

        if (tags.hasKey("MiningSpeedHandle"))
        {
            mineSpeed += tags.getInteger("MiningSpeedHandle");
            heads++;
        }

        if (tags.hasKey("MiningSpeedExtra"))
        {
            mineSpeed += tags.getInteger("MiningSpeedExtra");
            heads++;
        }
        float trueSpeed = mineSpeed / (heads * 100f);
        int hlvl = block.getHarvestLevel(meta);
        int durability = tags.getInteger("Damage");

        float stonebound = tags.getFloat("Shoddy");
        float bonusLog = (float) Math.log(durability / 72f + 1) * 2 * stonebound;
        trueSpeed += bonusLog;

        if (hlvl <= tags.getInteger("HarvestLevel"))
            return trueSpeed;
        return 0.1f;
    }

    public boolean canHarvestBlock (Block block)
    {
        TConstruct.logger.info(block.getMaterial());
        if (block.getMaterial().isToolNotRequired())
        {
            return true;
        }
        for (Material m : getEffectiveMaterials())
        {
            if (m == block.getMaterial())
                return true;
        }
        TConstruct.logger.info(block.getMaterial());
        return false;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "harvest" };
    }

    protected abstract Material[] getEffectiveMaterials ();

    protected abstract String getHarvestType ();
}
