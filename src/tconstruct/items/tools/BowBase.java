package tconstruct.items.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.entity.projectile.ArrowEntity;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BowBase extends ToolCore
{
    public BowBase(int itemID)
    {
        super(itemID, 0);
    }

    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    /* Bow usage */
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int useRemaining)
    {
        int time = this.getMaxItemUseDuration(stack) - useRemaining;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, time);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        time = event.charge;

        boolean creative = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        int slotID = getInventorySlotContainItem(TContent.arrow.itemID, player.inventory);
        int arrowID = getInventorySlotContainItem(Item.arrow.itemID, player.inventory);
        int arrowState = 0;
        ItemStack tinkerArrow = null;
        if (slotID != -1)
            tinkerArrow = player.inventory.getStackInSlot(slotID);

        if (creative || tinkerArrow != null || arrowID != -1)
        {
            NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
            float drawTime = toolTag.getInteger("DrawSpeed");
            float flightSpeed = toolTag.getFloat("FlightSpeed");
            float speedBase = (float) time / drawTime;
            speedBase = (speedBase * speedBase + speedBase * 2.0F) / 3.0F;

            if ((double) speedBase < 0.1D)
            {
                return;
            }

            if (speedBase > flightSpeed)
            {
                speedBase = flightSpeed;
            }

            EntityArrow arrowEntity = null;
            //if (tinkerArrow != null)
            if (slotID != -1 && (arrowID == -1 || slotID < arrowID))
            {
                ItemStack arrowStack = tinkerArrow.copy();
                arrowStack.stackSize = 1;
                arrowEntity = new ArrowEntity(world, player, speedBase * 2.0F, arrowStack);
            }
            else
            {
                arrowEntity = new EntityArrow(world, player, speedBase * 2.0F);
            }

            if (speedBase >= 1.0F)
            {
                arrowEntity.setIsCritical(true);
            }

            int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (var9 > 0)
            {
                arrowEntity.setDamage(arrowEntity.getDamage() + (double) var9 * 0.5D + 0.5D);
            }

            int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (slotID != -1 && (arrowID == -1 || slotID < arrowID))
                ((ArrowEntity) arrowEntity).setKnockbackModStrength(toolTag.getFloat("Knockback"));
            //var10 += toolTag.getFloat("Knockback");

            if (var10 > 0)
            {
                arrowEntity.setKnockbackStrength(var10);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
            {
                arrowEntity.setFire(100);
            }

            int reinforced = 0;

            if (toolTag.hasKey("Unbreaking"))
                reinforced = toolTag.getInteger("Unbreaking");

            if (random.nextInt(10) < 10 - reinforced)
            {
                AbilityHelper.damageTool(stack, 1, player, false);
            }
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + speedBase * 0.5F);

            if (creative)
            {
                arrowEntity.canBePickedUp = 2;
            }
            else
            {
                //if (tinkerArrow != null)
                if (slotID != -1 && (arrowID == -1 || slotID < arrowID))
                {
                    player.inventory.consumeInventoryItem(TContent.arrow.itemID);
                }
                else
                {
                    player.inventory.consumeInventoryItem(Item.arrow.itemID);
                }
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(arrowEntity);
            }
        }
    }

    int getInventorySlotContainItem (int itemID, InventoryPlayer inventory)
    {
        for (int j = 0; j < inventory.mainInventory.length; ++j)
        {
            if (inventory.mainInventory[j] != null && inventory.mainInventory[j].itemID == itemID)
            {
                return j;
            }
        }

        return -1;
    }

    public ItemStack onFoodEaten (ItemStack stack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick (ItemStack stack, World par2World, EntityPlayer player)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!toolTag.getBoolean("Broken"))
            {
                ArrowNockEvent event = new ArrowNockEvent(player, stack);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled())
                {
                    return event.result;
                }

                if (player.capabilities.isCreativeMode || player.inventory.hasItem(Item.arrow.itemID) || player.inventory.hasItem(TContent.arrow.itemID))
                {
                    player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
                }
            }
        }

        return stack;
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    /* Rendering */
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        headIcons1.clear();
        handleIcons1.clear();
        accessoryIcons1.clear();
        extraIcons1.clear();
        effectIcons1.clear();
        Iterator iterOne = headStrings.entrySet().iterator();
        while (iterOne.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterOne.next();
            headIcons1.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_1"));
        }

        iterOne = handleStrings.entrySet().iterator();
        while (iterOne.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterOne.next();
            handleIcons1.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_1"));
        }

        if (getPartAmount() > 2)
        {
            iterOne = accessoryStrings.entrySet().iterator();
            while (iterOne.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterOne.next();
                accessoryIcons1.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_1"));
            }
        }

        if (getPartAmount() > 3)
        {
            iterOne = extraStrings.entrySet().iterator();
            while (iterOne.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterOne.next();
                extraIcons1.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_1"));
            }
        }

        iterOne = effectStrings.entrySet().iterator();
        while (iterOne.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterOne.next();
            effectIcons1.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_1"));
        }

        headIcons2.clear();
        handleIcons2.clear();
        accessoryIcons2.clear();
        extraIcons2.clear();
        effectIcons2.clear();
        Iterator iterTwo = headStrings.entrySet().iterator();
        while (iterTwo.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterTwo.next();
            headIcons2.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_2"));
        }

        iterTwo = handleStrings.entrySet().iterator();
        while (iterTwo.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterTwo.next();
            handleIcons2.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_2"));
        }

        if (getPartAmount() > 2)
        {
            iterTwo = accessoryStrings.entrySet().iterator();
            while (iterTwo.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterTwo.next();
                accessoryIcons2.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_2"));
            }
        }

        if (getPartAmount() > 3)
        {
            iterTwo = extraStrings.entrySet().iterator();
            while (iterTwo.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterTwo.next();
                extraIcons2.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_2"));
            }
        }

        iterTwo = effectStrings.entrySet().iterator();
        while (iterTwo.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterTwo.next();
            effectIcons2.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_2"));
        }

        headIcons3.clear();
        handleIcons3.clear();
        accessoryIcons3.clear();
        extraIcons3.clear();
        effectIcons3.clear();
        Iterator iterThree = headStrings.entrySet().iterator();
        while (iterThree.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterThree.next();
            headIcons3.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_3"));
        }

        iterThree = handleStrings.entrySet().iterator();
        while (iterThree.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterThree.next();
            handleIcons3.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_3"));
        }

        if (getPartAmount() > 2)
        {
            iterThree = accessoryStrings.entrySet().iterator();
            while (iterThree.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterThree.next();
                accessoryIcons3.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_3"));
            }
        }

        if (getPartAmount() > 3)
        {
            iterThree = extraStrings.entrySet().iterator();
            while (iterThree.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iterThree.next();
                extraIcons3.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_3"));
            }
        }

        iterThree = effectStrings.entrySet().iterator();
        while (iterThree.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iterThree.next();
            effectIcons3.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue() + "_3"));
        }

        registerArrows(iconRegister);
    }

    void registerArrows (IconRegister iconRegister)
    {
        arrow1 = iconRegister.registerIcon("tinker:" + getDefaultFolder() + "/arrow_1");
        arrow2 = iconRegister.registerIcon("tinker:" + getDefaultFolder() + "/arrow_2");
        arrow3 = iconRegister.registerIcon("tinker:" + getDefaultFolder() + "/arrow_3");
    }

    @Override
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    if (tags.getBoolean("Broken"))
                        return (brokenIcons.get(tags.getInteger("RenderHandle")));
                    return handleIcons.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons.get(tags.getInteger("Effect6")));
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    /* Animations */
    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        //brokenHeadStrings.put(index, location[1]);
        //handleStrings.put(index, location[2]);
        if (location.length > 3)
            accessoryStrings.put(index, location[3]);
        if (location.length > 4)
            extraStrings.put(index, location[4]);
    }

    @Override
    public void registerAlternatePartPaths (int index, String[] location)
    {
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    public Icon arrow1;
    public Icon arrow2;
    public Icon arrow3;

    public HashMap<Integer, Icon> headIcons1 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> handleIcons1 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> accessoryIcons1 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> extraIcons1 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> effectIcons1 = new HashMap<Integer, Icon>();

    public HashMap<Integer, Icon> headIcons2 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> handleIcons2 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> accessoryIcons2 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> extraIcons2 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> effectIcons2 = new HashMap<Integer, Icon>();

    public HashMap<Integer, Icon> headIcons3 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> handleIcons3 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> accessoryIcons3 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> extraIcons3 = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> effectIcons3 = new HashMap<Integer, Icon>();

    @Override
    public Icon getIcon (ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        int useTime = stack.getMaxItemUseDuration() - useRemaining;
        if (!stack.hasTagCompound())
            return emptyIcon;

        NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
        int drawTime = toolTag.getInteger("DrawSpeed");
        float flightSpeed = toolTag.getFloat("FlightSpeed");
        drawTime *= flightSpeed;
        if (useTime >= drawTime - 2)
        {
            return getIcon3(stack, renderPass);
        }
        if (useTime >= (drawTime * 2 / 3))
        {
            return getIcon2(stack, renderPass);
        }
        return getIcon1(stack, renderPass);
    }

    public Icon getIcon1 (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    return handleIcons1.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons1.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons1.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons1.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons1.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons1.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons1.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons1.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons1.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons1.get(tags.getInteger("Effect6")));
                }
                else
                {
                    return arrow1;
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    public Icon getIcon2 (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    return handleIcons2.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons2.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons2.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons2.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons2.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons2.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons2.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons2.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons2.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons2.get(tags.getInteger("Effect6")));
                }
                else
                {
                    return arrow2;
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    public Icon getIcon3 (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    return handleIcons3.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons3.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons3.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons3.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons3.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons3.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons3.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons3.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons3.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons3.get(tags.getInteger("Effect6")));
                }
                else
                {
                    return arrow3;
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, id) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(getExtraItem(), 1, id) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), new ItemStack(getHandleItem(), 1, 0), accessoryStack, extraStack, name + getToolName());
        if (tool == null)
        {
            if (!TContent.supressMissingToolLogs)
            {
                TConstruct.logger.warning("Creative builder failed tool for " + name + this.getToolName());
                TConstruct.logger.warning("Make sure you do not have item ID conflicts");
            }
        }
        else
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }
}
