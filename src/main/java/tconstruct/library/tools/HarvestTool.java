package tconstruct.library.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
        // Block block = Block.blocksList[bID];
        if (block == null || block == Blocks.air)
            return false;
        int hlvl = -1;
        if (!(tags.getBoolean("Broken")))
        {
            Block localBlock = world.getBlock(x, y, z);
            int localMeta = world.getBlockMetadata(x, y, z);
            if (block.getHarvestTool(meta) != null && block.getHarvestTool(meta).equals(this.getHarvestType()))
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

                        boolean isEffective = false;

                        for (int iter = 0; iter < getEffectiveMaterials().length; iter++)
                        {
                            if (getEffectiveMaterials()[iter] == localBlock.getMaterial() || localBlock == Blocks.monster_egg)
                            {
                                isEffective = true;
                                break;
                            }
                        }

                        if (localBlock.getMaterial().isToolNotRequired())
                        {
                            isEffective = true;
                        }

                        if (!player.capabilities.isCreativeMode)
                        {
                            if (isEffective)
                            {
                                if (localBlock.removedByPlayer(world, player, x, y, z))
                                {
                                    localBlock.onBlockDestroyedByPlayer(world, x, y, z, localMeta);
                                }

                                // Workaround for dropping experience
                                int fortune = EnchantmentHelper.getFortuneModifier(player);
                                int exp = localBlock.getExpDrop(world, localMeta, fortune);
                                localBlock.dropXpOnBlockBreak(world, x, y, z, exp);

                                localBlock.harvestBlock(world, player, x, y, z, localMeta);
                                localBlock.onBlockHarvested(world, x, y, z, localMeta, player);
                                if (blockHardness > 0f)
                                    onBlockDestroyed(stack, world, localBlock, x, y, z, player);
                                world.func_147479_m(x, y, z);
                            }
                            else
                            {
                                WorldHelper.setBlockToAir(world, x, y, z);
                                world.func_147479_m(x, y, z);
                            }

                        }
                        else
                        {
                            WorldHelper.setBlockToAir(world, x, y, z);
                            world.func_147479_m(x, y, z);
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
            return calculateStrength(tags, block, meta); // No issue if the
                                                         // harvest level is
                                                         // too low
        }
        return super.getDigSpeed(stack, block, meta);
    }

    public float calculateStrength (NBTTagCompound tags, Block block, int meta)
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

    @Override
    public boolean func_150897_b (Block block)
    {
        if (block.getMaterial().isToolNotRequired())
        {
            return true;
        }
        return isEffective(block.getMaterial());
    }

    @Override
    public boolean canHarvestBlock (Block block, ItemStack itemStack)
    {
        return func_150897_b(block);
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "harvest" };
    }

    protected abstract Material[] getEffectiveMaterials ();

    protected abstract String getHarvestType ();

    public boolean isEffective(Material material)
    {
        for(Material m : getEffectiveMaterials())
            if(m == material)
                return true;

        return false;
    }

    //Right-click
    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        /*if (world.isRemote)
            return true;*/

        boolean used = false;
        int hotbarSlot = player.inventory.currentItem;
        int itemSlot = hotbarSlot == 0 ? 8 : hotbarSlot + 1;
        ItemStack nearbyStack = null;

        if (hotbarSlot < 8)
        {
            nearbyStack = player.inventory.getStackInSlot(itemSlot);
            if (nearbyStack != null)
            {
                Item item = nearbyStack.getItem();
                if (item instanceof ItemBlock)
                {
                    int posX = x;
                    int posY = y;
                    int posZ = z;
                    int playerPosX = (int) Math.floor(player.posX);
                    int playerPosY = (int) Math.floor(player.posY);
                    int playerPosZ = (int) Math.floor(player.posZ);
                    if (side == 0)
                    {
                        --posY;
                    }

                    if (side == 1)
                    {
                        ++posY;
                    }

                    if (side == 2)
                    {
                        --posZ;
                    }

                    if (side == 3)
                    {
                        ++posZ;
                    }

                    if (side == 4)
                    {
                        --posX;
                    }

                    if (side == 5)
                    {
                        ++posX;
                    }
                    if (posX == playerPosX && (posY == playerPosY || posY == playerPosY + 1 || posY == playerPosY - 1) && posZ == playerPosZ)
                    {
                        return false;
                    }

                    used = item.onItemUse(nearbyStack, player, world, x, y, z, side, clickX, clickY, clickZ);
                    if (nearbyStack.stackSize < 1)
                    {
                        nearbyStack = null;
                        player.inventory.setInventorySlotContents(itemSlot, null);
                    }
                }
            }
        }

        /*
          if (used) //Update client
          {
               Packet103SetSlot packet = new Packet103SetSlot(player.openContainer.windowId, itemSlot, nearbyStack);
               ((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet); 
          }
         */

        return used;
    }

    @Override
    public int getHarvestLevel (ItemStack stack, String toolClass)
    {
        if (!(stack.getItem() instanceof HarvestTool) || !getHarvestType().equals(toolClass))
        {
            return -1;
        }

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int harvestLvl = tags.getInteger("HarvestLevel");
        return harvestLvl;
    }
}
