package tconstruct.library.tools;

import cpw.mods.fml.client.FMLClientHandler;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import tconstruct.library.*;

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
            if (block.getHarvestTool(meta) != null && block.getHarvestTool(meta).equals(this.getHarvestType()))
                hlvl = block.getHarvestLevel(meta);
            int toolLevel = tags.getInteger("HarvestLevel");
            float blockHardness = block.getBlockHardness(world, x, y, z);

            if (hlvl <= toolLevel)
            {
                boolean cancelHarvest = false;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                {
                    if (mod.beforeBlockBreak(this, stack, x, y, z, player))
                        cancelHarvest = true;
                }

                // send blockbreak event
                BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, meta, player);
                event.setCanceled(cancelHarvest);
                MinecraftForge.EVENT_BUS.post(event);
                cancelHarvest = event.isCanceled();

                if (!cancelHarvest)
                {
                    if (block != null)
                    {

                        boolean isEffective = false;

                        for (int iter = 0; iter < getEffectiveMaterials().length; iter++)
                        {
                            if (getEffectiveMaterials()[iter] == block.getMaterial() || block == Blocks.monster_egg)
                            {
                                isEffective = true;
                                break;
                            }
                        }

                        // Microblocks are registered as rock but no HarvestTool is set
                        if (block.getMaterial().isToolNotRequired() || block.getHarvestTool(meta) == null)
                        {
                            isEffective = true;
                        }

                        if (!player.capabilities.isCreativeMode)
                        {
                            if (isEffective)
                            {
                                mineBlock(world, x, y, z, meta, player, block);
                                if (blockHardness > 0f)
                                    onBlockDestroyed(stack, world, block, x, y, z, player);
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
        if (this.getHarvestType().equals(block.getHarvestTool(meta)) && block.getHarvestLevel(meta) > 0)
        {
            return calculateStrength(tags, block, meta); // No issue if the
                                                         // harvest level is
                                                         // too low
        }
        return super.getDigSpeed(stack, block, meta);
    }

    public float calculateStrength (NBTTagCompound tags, Block block, int meta)
    {

        int hlvl = block.getHarvestLevel(meta);
        if (hlvl > tags.getInteger("HarvestLevel"))
            return 0.1f;

        return AbilityHelper.calcToolSpeed(this, tags);
    }

    public float breakSpeedModifier ()
    {
        return 1.0f;
    }

    public float stoneboundModifier ()
    {
        return 72f;
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

    public boolean isEffective (Material material)
    {
        for (Material m : getEffectiveMaterials())
            if (m == material)
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

    // The Scythe is not a HarvestTool and can't call this method, if you change something here you might change it there too.
    public void mineBlock (World world, int x, int y, int z, int meta, EntityPlayer player, Block block)
    {
        // Workaround for dropping experience
        boolean silktouch = EnchantmentHelper.getSilkTouchModifier(player);
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        int exp = block.getExpDrop(world, meta, fortune);

        block.onBlockHarvested(world, x, y, z, meta, player);
        if (block.removedByPlayer(world, player, x, y, z, true))
        {
            block.onBlockDestroyedByPlayer(world, x, y, z, meta);
            block.harvestBlock(world, player, x, y, z, meta);
            // Workaround for dropping experience
            if (!silktouch)
                block.dropXpOnBlockBreak(world, x, y, z, exp);

            if (world.isRemote)
            {
                INetHandler handler = FMLClientHandler.instance().getClientPlayHandler();
                if (handler != null && handler instanceof NetHandlerPlayClient)
                {
                    NetHandlerPlayClient handlerClient = (NetHandlerPlayClient) handler;
                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                }
            }
        }
    }
}
