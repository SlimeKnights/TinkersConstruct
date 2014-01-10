package tconstruct.items.tools;

import tconstruct.common.TRepo;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

    public float getDurabilityModifier ()
    {
        return 2.5f;
    }

    public String getToolName ()
    {
        return "Lumber Axe";
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player)
    {
        if (block != null && block.func_149688_o() == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
    }

    static Material[] materials = { Material.field_151575_d, Material.field_151582_l, Material.field_151594_q, Material.cactus, Material.pumpkin };

    /* Lumber axe specific */

    /*@Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped == stack)
            {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1, 1));
            }
        }
    }*/

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.func_149688_o())
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
                float trueSpeed = mineSpeed / (heads * 300f);
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
                int durability = tags.getInteger("Damage");

                float stonebound = tags.getFloat("Shoddy");
                float bonusLog = (float) Math.log(durability / 72f + 1) * 2 * stonebound;
                trueSpeed += bonusLog;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return trueSpeed;
                return 0.1f;
            }
        }
        return super.getStrVsBlock(stack, block, meta);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        World world = player.worldObj;
        final Block wood = world.func_147439_a(x, y, z);;
        if (wood == null)
        {
            return super.onBlockStartBreak(stack, x, y, z, player);
        }
        if (wood.isWood(world, x, y, z) || wood.func_149688_o() == Material.sponge)
        {
            int height = y;
            boolean foundTop = false;
            do
            {
                height++;
                Block block = world.func_147439_a(x, height, z);
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
                            Block leaves = world.func_147439_a(xPos, yPos, zPos);
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
                world.playAuxSFX(2001, x, y, z, woodID + (meta << 12));
        }
        else if (wood.func_149688_o() == Material.field_151575_d)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            int meta = world.getBlockMetadata(x, y, z);
            destroyWood(world, x, y, z, stack, tags, player);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, woodID + (meta << 12));
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    void breakTree (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, Block bID, int meta, EntityPlayer player)
    {
        Block block;
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        Block localblock = world.func_147439_a(xPos, yPos, zPos);
                        if (bID == localblock)
                        {
                            block = localblock;
                            meta = world.getBlockMetadata(xPos, yPos, zPos);
                            int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());

                            if (hlvl <= tags.getInteger("HarvestLevel"))
                            {
                                boolean cancelHarvest = false;
                                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                                {
                                    if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                        cancelHarvest = true;
                                }

                                if (cancelHarvest)
                                {
                                    breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                }
                                else
                                {
                                    if (localblock == bID && world.getBlockMetadata(xPos, yPos, zPos) % 4 == meta % 4)
                                    {
                                        /* world.setBlock(xPos, yPos, zPos, 0, 0, 3);
                                         if (!player.capabilities.isCreativeMode)
                                         {
                                             Block.blocksList[bID].harvestBlock(world, player, xPos, yPos, zPos, meta);
                                             onBlockDestroyed(stack, world, bID, xPos, yPos, zPos, player);
                                         }*/
                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            if (block.removeBlockByPlayer(world, player, xPos, yPos, zPos))
                                            {
                                                block.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, meta);
                                            }
                                            block.harvestBlock(world, player, xPos, yPos, zPos, meta);
                                            block.onBlockHarvested(world, xPos, yPos, zPos, meta, player);
                                            onBlockDestroyed(stack, world, localblock, xPos, yPos, zPos, player);
                                        }
                                        else
                                        {
                                            world.setBlockToAir(xPos, yPos, zPos);
                                        }
                                        breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                                    }
                                    /*else
                                    {
                                        Block leaves = Block.blocksList[localID];
                                        if (leaves != null && leaves.isLeaves(world, xPos, yPos, zPos))
                                        {
                                            world.setBlockToAir(xPos, yPos, zPos);
                                            if (!player.capabilities.isCreativeMode)
                                            {
                                                Block.blocksList[bID].harvestBlock(world, player, xPos, yPos, zPos, meta);
                                                onBlockDestroyed(stack, world, bID, xPos, yPos, zPos, player);
                                            }
                                        }
                                    }*/
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
                        Block block = world.func_147439_a(xPos, yPos, zPos);
                        int meta = world.getBlockMetadata(xPos, yPos, zPos);
                        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());

                        if (block != null && block.func_149688_o() == Material.field_151575_d)
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
                                    world.setBlockToAir(xPos, yPos, zPos);
                                    if (!player.capabilities.isCreativeMode)
                                    {
                                        //TODO harvestBlock
                                        block.func_149636_a(world, player, xPos, yPos, zPos, meta);
                                        onBlockDestroyed(stack, world, block, xPos, yPos, zPos, player);
                                    }
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
        return TRepo.broadAxeHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TRepo.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TRepo.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TRepo.toughBinding;
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

    @Override
    public boolean willAllowOffhandWeapon ()
    {
        return false;
    }

    @Override
    public boolean willAllowShield ()
    {
        return false;
    }

    @Override
    public boolean isOffhandHandDualWeapon ()
    {
        return false;
    }

    @Override
    public boolean sheatheOnBack ()
    {
        return true;
    }
}
