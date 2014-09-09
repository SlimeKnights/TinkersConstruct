package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import tconstruct.library.*;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class LumberAxe extends HarvestTool
{
    public LumberAxe()
    {
        super(0);
        this.setUnlocalizedName("InfiTool.LumberAxe");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "axe";
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 2.5f;
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player)
    {
        if (block != null && block.getMaterial() == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
    }

    static Material[] materials = { Material.wood, Material.vine, Material.circuits, Material.cactus, Material.gourd };

    /* Lumber axe specific */

    /*
     * @Override public void onUpdate (ItemStack stack, World world, Entity
     * entity, int par4, boolean par5) { super.onUpdate(stack, world, entity,
     * par4, par5); if (entity instanceof EntityPlayer) { EntityPlayer player =
     * (EntityPlayer) entity; ItemStack equipped =
     * player.getCurrentEquippedItem(); if (equipped == stack) {
     * player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1, 1)); }
     * } }
     */

    @Override
    public float breakSpeedModifier ()
    {
        return 0.4f;
    }

    @Override
    public float stoneboundModifier ()
    {
        return 216f;
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        World world = player.worldObj;
        final Block wood = world.getBlock(x, y, z);
        ;
        if (wood == null)
        {
            return super.onBlockStartBreak(stack, x, y, z, player);
        }
        if (wood.isWood(world, x, y, z) || wood.getMaterial() == Material.sponge)
        {
            int height = y;
            boolean foundTop = false;
            do
            {
                height++;
                Block block = world.getBlock(x, height, z);
                if (block != wood)
                {
                    height--;
                    foundTop = true;
                }
            } while (!foundTop);

            int numLeaves = 0;
            if (height - y < 50)
            {
                for (int xPos = x - 1; xPos <= x + 1; xPos++)
                {
                    for (int yPos = height - 1; yPos <= height + 1; yPos++)
                    {
                        for (int zPos = z - 1; zPos <= z + 1; zPos++)
                        {
                            Block leaves = world.getBlock(xPos, yPos, zPos);
                            if (leaves != null && leaves.isLeaves(world, xPos, yPos, zPos))
                                numLeaves++;
                        }
                    }
                }
            }

            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            int meta = world.getBlockMetadata(x, y, z);
            if (numLeaves > 3)
                breakTree(world, x, y, z, stack, tags, wood, meta, player);
            else
                destroyWood(world, x, y, z, stack, tags, player);

            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(wood) + (meta << 12));
        }
        else if (wood.getMaterial() == Material.wood)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            int meta = world.getBlockMetadata(x, y, z);
            destroyWood(world, x, y, z, stack, tags, player);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(wood) + (meta << 12));
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    void breakTree (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, Block bID, int meta, EntityPlayer player)
    {
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        Block localBlock = world.getBlock(xPos, yPos, zPos);
                        if (bID == localBlock)
                        {
                            int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                            int hlvl = localBlock.getHarvestLevel(localMeta);
                            float localHardness = localBlock == null ? Float.MAX_VALUE : localBlock.getBlockHardness(world, xPos, yPos, zPos);

                            if (hlvl <= tags.getInteger("HarvestLevel") && !(localHardness < 0))
                            {
                                boolean cancelHarvest = false;
                                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                                {
                                    if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                        cancelHarvest = true;
                                }

                                // send blockbreak event
                                BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, localBlock, localMeta, player);
                                event.setCanceled(cancelHarvest);
                                MinecraftForge.EVENT_BUS.post(event);
                                cancelHarvest = event.isCanceled();

                                if (cancelHarvest)
                                {
                                    breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                }
                                else
                                {
                                    if (localBlock == bID && localMeta % 4 == meta % 4)
                                    {
                                        /*
                                         * world.setBlock(xPos, yPos, zPos, 0,
                                         * 0, 3); if
                                         * (!player.capabilities.isCreativeMode)
                                         * {
                                         * Block.blocksList[bID].harvestBlock(world
                                         * , player, xPos, yPos, zPos, meta);
                                         * onBlockDestroyed(stack, world, bID,
                                         * xPos, yPos, zPos, player); }
                                         */
                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            mineBlock(world, xPos, yPos, zPos, localMeta, player, localBlock);
                                            onBlockDestroyed(stack, world, localBlock, xPos, yPos, zPos, player);
                                        }
                                        else
                                        {
                                            WorldHelper.setBlockToAir(world, xPos, yPos, zPos);
                                        }
                                        if (!world.isRemote)
                                            breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                    }
                                    /*
                                     * else { Block leaves =
                                     * Block.blocksList[localID]; if (leaves !=
                                     * null && leaves.isLeaves(world, xPos,
                                     * yPos, zPos)) {
                                     * WorldHelper.setBlockToAir(world, xPos,
                                     * yPos, zPos); if
                                     * (!player.capabilities.isCreativeMode) {
                                     * Block.blocksList[bID].harvestBlock(world,
                                     * player, xPos, yPos, zPos, meta);
                                     * onBlockDestroyed(stack, world, bID, xPos,
                                     * yPos, zPos, player); } } }
                                     */
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void destroyWood (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, EntityPlayer player)
    {
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y - 1; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        Block localBlock = world.getBlock(xPos, yPos, zPos);
                        int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                        int hlvl = localBlock.getHarvestLevel(localMeta);
                        float localHardness = localBlock == null ? Float.MAX_VALUE : localBlock.getBlockHardness(world, xPos, yPos, zPos);

                        if (localBlock != null && localBlock.getMaterial() == Material.wood && !(localHardness < 0))
                        {
                            if (hlvl <= tags.getInteger("HarvestLevel"))
                            {
                                boolean cancelHarvest = false;
                                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                                {
                                    if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                        cancelHarvest = true;
                                }

                                if (!cancelHarvest)
                                {
                                    if (!player.capabilities.isCreativeMode)
                                    {
                                        mineBlock(world, xPos, yPos, zPos, localMeta, player, localBlock);
                                        onBlockDestroyed(stack, world, localBlock, xPos, yPos, zPos, player);
                                    }
                                    WorldHelper.setBlockToAir(world, xPos, yPos, zPos);
                                    world.func_147479_m(xPos, yPos, zPos);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.broadAxeHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughBinding;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_lumberaxe_head";
        case 1:
            return "_lumberaxe_head_broken";
        case 2:
            return "_lumberaxe_handle";
        case 3:
            return "_lumberaxe_shield";
        case 4:
            return "_lumberaxe_binding";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_lumberaxe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "lumberaxe";
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }
}
