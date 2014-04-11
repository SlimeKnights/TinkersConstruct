package tconstruct.library.tools;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/* Base class for tools that should be harvesting blocks */

public abstract class HarvestTool extends ToolCore
{
    public HarvestTool(int itemID, int baseDamage)
    {
        super(itemID, baseDamage);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        int bID = player.worldObj.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1)
            return false;
        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());

        if (hlvl <= tags.getInteger("HarvestLevel"))
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
            world.setBlockToAir(x, y, z);
            if (!player.capabilities.isCreativeMode)
                onBlockDestroyed(stack, world, bID, x, y, z, player);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
            return true;
        }
    }

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
            if (materials[i] == block.blockMaterial)
            {
                return calculateStrength(tags, block, meta);
            }
        }
        if (MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType()) > 0)
        {
            return calculateStrength(tags, block, meta); //No issue if the harvest level is too low
        }
        return super.getStrVsBlock(stack, block, meta);
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
        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
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
        if (block.blockMaterial.isToolNotRequired())
        {
            return true;
        }
        for (Material m : getEffectiveMaterials())
        {
            if (m == block.blockMaterial)
                return true;
        }
        return false;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "harvest" };
    }

    protected abstract Material[] getEffectiveMaterials ();

    protected abstract String getHarvestType ();

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

        /*if (used) //Update client
        {
            Packet103SetSlot packet = new Packet103SetSlot(player.openContainer.windowId, itemSlot, nearbyStack);
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
        }*/
        return used;
    }
}
