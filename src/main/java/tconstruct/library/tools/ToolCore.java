package tconstruct.library.tools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.entity.FancyEntityItem;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * NBTTags Main tag - InfiTool
 * 
 * @see ToolBuilder
 * 
 *      Required: Head: Base and render tag, above the handle Handle: Base and
 *      render tag, bottom layer
 * 
 *      Damage: Replacement for metadata MaxDamage: ItemStacks only read
 *      setMaxDamage() Broken: Represents whether the tool is broken (boolean)
 *      Attack: How much damage a mob will take MiningSpeed: The speed at which
 *      a tool mines
 * 
 *      Others: Accessory: Base and tag, above head. Sword guards, binding, etc
 *      Effects: Render tag, top layer. Fancy effects like moss or diamond edge.
 *      Render order: Handle > Head > Accessory > Effect1 > Effect2 > Effect3 >
 *      etc Unbreaking: Reinforced in-game, 10% chance to not use durability per
 *      level Stonebound: Mines faster as the tool takes damage, but has less
 *      attack Spiny: Opposite of stonebound
 * 
 *      Modifiers have their own tags.
 * @see ToolMod
 */

public abstract class ToolCore extends Item implements IEnergyContainerItem
{
    // TE power constants -- TODO grab these from the items added
    protected int capacity = 400000;
    protected int maxReceive = 80;
    protected int maxExtract = 80;

    protected Random random = new Random();
    protected int damageVsEntity;
    public static IIcon blankSprite;
    public static IIcon emptyIcon;

    public ToolCore(int baseDamage)
    {
        super();
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.setUnlocalizedName("InfiTool");
        this.setCreativeTab(TConstructRegistry.toolTab);
        damageVsEntity = baseDamage;
        TConstructRegistry.addToolMapping(this);
        setNoRepair();
        canRepair = false;
    }

    /**
     * Determines crafting behavior with regards to durability 0: None 1: Adds
     * handle modifier 2: Averages part with the rest of the tool (head)
     * 
     * @return type
     */

    public int durabilityTypeHandle ()
    {
        return 1;
    }

    public int durabilityTypeAccessory ()
    {
        return 0;
    }

    public int durabilityTypeExtra ()
    {
        return 0;
    }

    public int getModifierAmount ()
    {
        return 3;
    }

    public String getToolName ()
    {
        return this.getClass().getSimpleName();
    }

    /* Rendering */

    public HashMap<Integer, IIcon> headIcons = new HashMap<Integer, IIcon>();
    public HashMap<Integer, IIcon> brokenIcons = new HashMap<Integer, IIcon>();
    public HashMap<Integer, IIcon> handleIcons = new HashMap<Integer, IIcon>();
    public HashMap<Integer, IIcon> accessoryIcons = new HashMap<Integer, IIcon>();
    public HashMap<Integer, IIcon> effectIcons = new HashMap<Integer, IIcon>();
    public HashMap<Integer, IIcon> extraIcons = new HashMap<Integer, IIcon>();

    //Not liking this
    public HashMap<Integer, String> headStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> brokenPartStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> handleStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> accessoryStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> effectStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> extraStrings = new HashMap<Integer, String>();

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 9;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return false;
    }

    // Override me please!
    public int getPartAmount ()
    {
        return 3;
    }

    public abstract String getIconSuffix (int partType);

    public abstract String getEffectSuffix ();

    public abstract String getDefaultFolder ();

    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
        if (location.length > 3)
            accessoryStrings.put(index, location[3]);
        if (location.length > 4)
            extraStrings.put(index, location[4]);
    }

    public void registerAlternatePartPaths (int index, String[] location)
    {

    }

    public void registerEffectPath (int index, String location)
    {
        effectStrings.put(index, location);
    }

    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        headIcons.clear();
        brokenIcons.clear();
        handleIcons.clear();
        accessoryIcons.clear();
        extraIcons.clear();
        effectIcons.clear();
        Iterator iter = headStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            headIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        iter = brokenPartStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            brokenIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        iter = handleStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            handleIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        if (getPartAmount() > 2)
        {
            iter = accessoryStrings.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iter.next();
                accessoryIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
            }
        }

        if (getPartAmount() > 3)
        {
            iter = extraStrings.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry pairs = (Map.Entry) iter.next();
                extraIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
            }
        }

        iter = effectStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            effectIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        emptyIcon = iconRegister.registerIcon("tinker:blankface");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        return blankSprite;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    return handleIcons.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    if (tags.getBoolean("Broken"))
                        return (brokenIcons.get(tags.getInteger("RenderHead")));
                    else
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

    /* Tags and information about the tool */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.hasKey("Energy"))
        {
            String color = "";
            int RF = tags.getInteger("Energy");

            if (RF != 0)
            {
                if (RF <= this.getMaxEnergyStored(stack) / 3)
                    color = "\u00a74";
                else if (RF > this.getMaxEnergyStored(stack) * 2 / 3)
                    color = "\u00a72";
                else
                    color = "\u00a76";
            }

            String energy = new StringBuilder().append(color).append(tags.getInteger("Energy")).append("/").append(getMaxEnergyStored(stack)).append(" RF").toString();
            list.add(energy);
        }
        if (tags.hasKey("InfiTool"))
        {
            boolean broken = tags.getCompoundTag("InfiTool").getBoolean("Broken");
            if (broken)
                list.add("\u00A7oBroken");
            else
            {
                int head = tags.getCompoundTag("InfiTool").getInteger("Head");
                int handle = tags.getCompoundTag("InfiTool").getInteger("Handle");
                int binding = tags.getCompoundTag("InfiTool").getInteger("Accessory");
                int extra = tags.getCompoundTag("InfiTool").getInteger("Extra");

                String headName = getAbilityNameForType(head);
                if (!headName.equals(""))
                    list.add(getStyleForType(head) + headName);

                String handleName = getAbilityNameForType(handle);
                if (!handleName.equals("") && handle != head)
                    list.add(getStyleForType(handle) + handleName);

                if (getPartAmount() >= 3)
                {
                    String bindingName = getAbilityNameForType(binding);
                    if (!bindingName.equals("") && binding != head && binding != handle)
                        list.add(getStyleForType(binding) + bindingName);
                }

                if (getPartAmount() >= 4)
                {
                    String extraName = getAbilityNameForType(extra);
                    if (!extraName.equals("") && extra != head && extra != handle && extra != binding)
                        list.add(getStyleForType(extra) + extraName);
                }

                int unbreaking = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");
                String reinforced = getReinforcedName(head, handle, binding, extra, unbreaking);
                if (!reinforced.equals(""))
                    list.add(reinforced);

                boolean displayToolTips = true;
                int tipNum = 0;
                while (displayToolTips)
                {
                    tipNum++;
                    String tooltip = "Tooltip" + tipNum;
                    if (tags.getCompoundTag("InfiTool").hasKey(tooltip))
                    {
                        String tipName = tags.getCompoundTag("InfiTool").getString(tooltip);
                        if (!tipName.equals(""))
                            list.add(tipName);
                    }
                    else
                        displayToolTips = false;
                }
            }
        }
        list.add("");
        int attack = (int) (tags.getCompoundTag("InfiTool").getInteger("Attack") * this.getDamageModifier());
        list.add("\u00A79+" + attack + " " + StatCollector.translateToLocalFormatted("attribute.name.generic.attackDamage"));

    }

    public static String getStyleForType (int type)
    {
        return TConstructRegistry.getMaterial(type).style();
    }

    public String getAbilityNameForType (int type)
    {
        return TConstructRegistry.getMaterial(type).ability();
    }

    public String getReinforcedName (int head, int handle, int accessory, int extra, int unbreaking)
    {
        TToolMaterial headMat = TConstructRegistry.getMaterial(head);
        TToolMaterial handleMat = TConstructRegistry.getMaterial(handle);
        TToolMaterial accessoryMat = TConstructRegistry.getMaterial(accessory);
        TToolMaterial extraMat = TConstructRegistry.getMaterial(extra);

        int reinforced = 0;
        String style = "";
        int current = headMat.reinforced();
        if (current > 0)
        {
            style = headMat.style();
            reinforced = current;
        }
        current = handleMat.reinforced();
        if (current > 0 && current > reinforced)
        {
            style = handleMat.style();
            reinforced = current;
        }
        if (getPartAmount() >= 3)
        {
            current = accessoryMat.reinforced();
            if (current > 0 && current > reinforced)
            {
                style = accessoryMat.style();
                reinforced = current;
            }
        }
        if (getPartAmount() >= 4)
        {
            current = extraMat.reinforced();
            if (current > 0 && current > reinforced)
            {
                style = extraMat.style();
                reinforced = current;
            }
        }

        reinforced += unbreaking - reinforced;

        if (reinforced > 0)
        {
            return style + getReinforcedString(reinforced);
        }
        return "";
    }

    String getReinforcedString (int reinforced)
    {
        if (reinforced > 9)
            return "Unbreakable";
        String ret = "Reinforced ";
        switch (reinforced)
        {
        case 1:
            ret += "I";
            break;
        case 2:
            ret += "II";
            break;
        case 3:
            ret += "III";
            break;
        case 4:
            ret += "IV";
            break;
        case 5:
            ret += "V";
            break;
        case 6:
            ret += "VI";
            break;
        case 7:
            ret += "VII";
            break;
        case 8:
            ret += "VIII";
            break;
        case 9:
            ret += "IX";
            break;
        default:
            ret += "X";
            break;
        }
        return ret;
    }

    // Used for sounds and the like
    public void onEntityDamaged (World world, EntityLivingBase player, Entity entity)
    {

    }

    /* Creative mode tools */

    @Override
    public void getSubItems (Item id, CreativeTabs tab, List list)
    {
        Iterator iter = TConstructRegistry.toolMaterials.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            TToolMaterial material = (TToolMaterial) pairs.getValue();
            buildTool((Integer) pairs.getKey(), material.displayName, list);
        }
    }

    public void buildTool (int id, String name, List list)
    {
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, id) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(extra, 1, id) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), new ItemStack(getHandleItem(), 1, id), accessoryStack, extraStack, name + getToolName());
        if (tool == null)
        {
            Class clazz;
            Field fld;
            boolean supress = false;
            try
            {
                clazz = Class.forName("tconstruct.common.TRepo");
                fld = clazz.getField("supressMissingToolLogs");
                supress = fld.getBoolean(fld);
            }
            catch (Exception e)
            {
                TConstructRegistry.logger.error("TConstruct Library could not find parts of TContent");
                e.printStackTrace();
            }
            if (!supress)
            {
                TConstructRegistry.logger.error("Creative builder failed tool for " + name + this.getToolName());
                TConstructRegistry.logger.error("Make sure you do not have item ID conflicts");
            }
        }
        else
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    public abstract Item getHeadItem ();

    public abstract Item getAccessoryItem ();

    public Item getExtraItem ()
    {
        return null;
    }

    public Item getHandleItem ()
    {
        return TConstructRegistry.getItem("toolRod");// TContent.toolRod;
    }

    /* Updating */

    @Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
        {
            mod.updateTool(this, stack, world, entity);
        }
    }

    /* Tool uses */

    // Types
    public abstract String[] toolCategories ();

    // Mining
    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        if (stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Lava") && world.isRemote)
            for (int i = 0; i < 5; i++)
            {
                float f = (float) x + random.nextFloat();
                float f1 = (float) y + random.nextFloat();
                float f2 = (float) z + random.nextFloat();
                float f3 = 0.52F;
                float f4 = random.nextFloat() * 0.6F - 0.3F;
                world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);

                world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
            }
        return false;
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player)
    {
        if (block != null && (double) block.getBlockHardness(world, x, y, z) != 0.0D)
        {
            return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
        }
        return true;
    }

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags.getCompoundTag("InfiTool").getBoolean("Broken"))
            return 0.1f;
        return 1f;
    }

    // Attacking
    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        AbilityHelper.onLeftClickEntity(stack, player, entity, this, 0);
        return false;
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLivingBase mob, EntityLivingBase player)
    {
        return true;
    }

    public boolean pierceArmor ()
    {
        return false;
    }

    public float chargeAttack ()
    {
        return 1f;
    }

    public int getDamageVsEntity (Entity par1Entity)
    {
        return this.damageVsEntity;
    }

    // Changes how much durability the base tool has
    public float getDurabilityModifier ()
    {
        return 1f;
    }

    public float getRepairCost ()
    {
        return getDurabilityModifier();
    }

    public float getDamageModifier ()
    {
        return 1.0f;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
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
                if (item instanceof ItemPotion && ((ItemPotion) item).isSplash(nearbyStack.getItemDamage()))
                {
                    nearbyStack = item.onItemRightClick(nearbyStack, world, player);
                    if (nearbyStack.stackSize < 1)
                    {
                        nearbyStack = null;
                        player.inventory.setInventorySlotContents(itemSlot, null);
                    }
                }
            }
        }
        return stack;
    }

    /* Vanilla overrides */
    @Override
    public boolean isItemTool (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean getIsRepairable (ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return false;
    }

    @Override
    public boolean isRepairable ()
    {
        return false;
    }

    @Override
    public int getItemEnchantability ()
    {
        return 0;
    }

    @Override
    public boolean isFull3D ()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack, int pass)
    {
        return false;
    }

    /* Proper stack damage */
    public int getItemMaxDamageFromStack (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }
        if (tags.hasKey("Energy"))
        {
            int energy = tags.getInteger("Energy");
            if (energy > 0)
                return this.getMaxEnergyStored(stack);
        }
        return tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
    }

    public int getItemDamageFromStackForDisplay (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (tags == null)
        {
            return 0;
        }
        if (tags.hasKey("Energy"))
        {
            int energy = tags.getInteger("Energy");
            if (energy > 0)
                return getMaxEnergyStored(stack) - energy;
        }
        return tags.getCompoundTag("InfiTool").getInteger("Damage");
    }

    /* Prevent tools from dying */
    public boolean hasCustomEntity (ItemStack stack)
    {
        return true;
    }

    public Entity createEntity (World world, Entity location, ItemStack itemstack)
    {
        return new FancyEntityItem(world, location, itemstack);
    }

    // TE support section -- from COFH core API reference section
    public void setMaxTransfer (int maxTransfer)
    {
        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
    }

    public void setMaxReceive (int maxReceive)
    {
        this.maxReceive = maxReceive;
    }

    public void setMaxExtract (int maxExtract)
    {
        this.maxExtract = maxExtract;
    }

    /* IEnergyContainerItem */
    @Override
    public int receiveEnergy (ItemStack container, int maxReceive, boolean simulate)
    {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy"))
            return 0;
        int energy = tags.getInteger("Energy");
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
        {
            energy += energyReceived;
            tags.setInteger("Energy", energy);
            container.setItemDamage(1 + (getMaxEnergyStored(container) - energy) * (container.getMaxDamage() - 2) / getMaxEnergyStored(container));

        }
        return energyReceived;
    }

    @Override
    public int extractEnergy (ItemStack container, int maxExtract, boolean simulate)
    {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy"))
        {
            return 0;
        }
        int energy = tags.getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
        {
            energy -= energyExtracted;
            tags.setInteger("Energy", energy);
            container.setItemDamage(1 + (getMaxEnergyStored(container) - energy) * (container.getMaxDamage() - 1) / getMaxEnergyStored(container));

        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored (ItemStack container)
    {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy"))
        {
            return 0;
        }
        return tags.getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored (ItemStack container)
    {
        NBTTagCompound tags = container.getTagCompound();
        if (tags == null || !tags.hasKey("Energy"))
            return 0;
        return capacity;
    }
    // end of TE support section
}
