package mods.tinker.tconstruct.library.tools;

import ic2.api.item.IBoxable;
import ic2.api.item.ICustomElectricItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * NBTTags Main tag - InfiTool
 * <p>
 * <b>Required:</b> <br>
 * Head: Base and render tag, above the handle <br>
 * Handle: Base and render tag, bottom layer <br>
 * Damage: Replacement for metadata MaxDamage: ItemStacks only read <br>
 * setMaxDamage() Broken: Represents whether the tool is broken (boolean) <br>
 * Attack: How much damage a mob will take MiningSpeed: The speed at which a
 * tool mines
 * <p>
 * <b>Others:</b> <br>
 * Accessory: Base and tag, above head. Sword guards, binding, etc <br>
 * Effects: Render tag, top layer. Fancy effects like moss or diamond edge. <br>
 * Render order: Handle > Head > Accessory > Effect1 > Effect2 > Effect3 <br>
 * Unbreaking: Reinforced in-game, 10% chance to not use durability per level <br>
 * Shoddy/Spiny: Mines faster or slower and does less or more attack.
 * <p>
 * Modifiers have their own tags.
 * 
 * @see ToolBuilder
 * @see ToolMod
 */

public abstract class ToolCore extends Item implements ICustomElectricItem, IBoxable
{
    protected Random random = new Random();
    protected int damageVsEntity;
    public static Icon blankSprite;
    public static Icon emptyIcon;

    public ToolCore(int id, int baseDamage)
    {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        this.setUnlocalizedName("InfiTool");
        this.setCreativeTab(TConstructRegistry.toolTab);
        damageVsEntity = baseDamage;
        TConstructRegistry.addToolMapping(this);
        setNoRepair();
        canRepair = false;
    }

    /** Determines what type of heads the tool has.
     * 0: no heads
     * 1: one head
     * 2: two heads
     * 3: Two heads, different uses
     * @return The head type
     */
    public abstract int getHeadType ();

    public String getToolName ()
    {
        return this.getClass().getSimpleName();
    }

    /* Rendering */

    //public HashMap<Integer, String> partTextures = new HashMap<Integer, String>();
    //public HashMap<Integer, String> effectTextures = new HashMap<Integer, String>();
    public HashMap<Integer, Icon> headIcons = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> brokenHeadIcons = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> handleIcons = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> accessoryIcons = new HashMap<Integer, Icon>();
    public HashMap<Integer, Icon> effectIcons = new HashMap<Integer, Icon>();

    //Not liking this
    public HashMap<Integer, String> headStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> brokenHeadStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> handleStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> accessoryStrings = new HashMap<Integer, String>();
    public HashMap<Integer, String> effectStrings = new HashMap<Integer, String>();

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

    //Override me please!
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
        brokenHeadStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
        accessoryStrings.put(index, location[3]);
    }

    public void registerEffectPath (int index, String location)
    {
        effectStrings.put(index, location);
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        headIcons.clear();
        brokenHeadIcons.clear();
        handleIcons.clear();
        accessoryIcons.clear();
        effectIcons.clear();
        Iterator iter = headStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            headIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        iter = brokenHeadStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            brokenHeadIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
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

        iter = effectStrings.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pairs = (Map.Entry) iter.next();
            effectIcons.put((Integer) pairs.getKey(), iconRegister.registerIcon((String) pairs.getValue()));
        }

        emptyIcon = iconRegister.registerIcon("tinker:blankface");
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
                    return handleIcons.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    if (tags.getBoolean("Broken"))
                        return (brokenHeadIcons.get(tags.getInteger("RenderHead")));
                    else
                        return (headIcons.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons.get(tags.getInteger("RenderAccessory")));
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
        if (tags.hasKey("charge"))
        {
            String color = "";
            //double joules = this.getJoules(stack);
            int power = tags.getInteger("charge");

            if (power != 0)
            {
                if (power <= this.getMaxCharge(stack) / 3)
                    color = "\u00a74";
                else if (power > this.getMaxCharge(stack) * 2 / 3)
                    color = "\u00a72";
                else
                    color = "\u00a76";
            }

            String charge = new StringBuilder().append(color).append(tags.getInteger("charge")).append("/").append(getMaxCharge(stack)).append(" EU").toString();
            list.add(charge);
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

                String reinforced = getReinforcedName(head, handle, binding);
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
    }

    public static String getStyleForType (int type)
    {
        return TConstructRegistry.getMaterial(type).style();
    }

    public String getAbilityNameForType (int type)
    {
        return TConstructRegistry.getMaterial(type).ability();
    }

    public String getReinforcedName (int head, int handle, int accessory)
    {
        ToolMaterial headMat = TConstructRegistry.getMaterial(head);
        ToolMaterial handleMat = TConstructRegistry.getMaterial(handle);
        ToolMaterial accessoryMat = TConstructRegistry.getMaterial(accessory);

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

        if (reinforced > 0)
        {
            return style + getReinforcedString(reinforced);
        }
        return "";
    }

    String getReinforcedString (int reinforced)
    {
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
    
    //Used for sounds and the like
    public void onEntityDamaged(World world, EntityPlayer player, Entity entity)
    {
        
    }

    /* Creative mode tools */
    static String[] toolMaterialNames = { "Wooden ", "Stone ", "Iron ", "Flint ", "Cactus ", "Bone ", "Obsidian ", "Netherrack ", "Slime ", "Paper ", "Cobalt ", "Ardite ", "Manyullyn ", "Copper ",
            "Bronze ", "Alumite ", "Steel ", "Slime " };

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 18; i++)
        {
            Item accessory = getAccessoryItem();
            ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, i) : null;
            ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, i), new ItemStack(getHandleItem(), 1, i), accessoryStack, toolMaterialNames[i] + getToolName());
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    protected abstract Item getHeadItem ();

    protected abstract Item getAccessoryItem ();

    protected Item getHandleItem ()
    {
        return TConstructRegistry.toolRod;
    }

    /* Updating */

    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        if (!world.isRemote && entity instanceof EntityLiving && !((EntityLiving) entity).isSwingInProgress)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (tags.hasKey("Moss"))
            {
                int chance = tags.getInteger("Moss");
                int check = world.canBlockSeeTheSky((int) entity.posX, (int) entity.posY, (int) entity.posZ) ? 750 : 1500;
                if (random.nextInt(check) < chance)
                {
                    AbilityHelper.healTool(stack, 1, (EntityLiving) entity, true, false);
                }
            }
        }
    }

    /* Tool uses */

    //Types
    public abstract String[] toolCategories ();

    //Mining
    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        int bID = player.worldObj.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1 || bID > 4095)
            return false;

        if (tags.getBoolean("Lava") && block.quantityDropped(meta, 0, random) != 0)
        {
            ItemStack smeltStack = new ItemStack(block.idDropped(meta, random, 0), 1, block.damageDropped(meta));
            if (smeltStack.itemID < 0 || smeltStack.itemID >= 32000 || smeltStack.getItem() == null)
                return false;
            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(smeltStack);
            if (result != null)
            {
                world.setBlockToAir(x, y, z);
                if (!player.capabilities.isCreativeMode)
                    onBlockDestroyed(stack, world, bID, x, y, z, player);
                if (!world.isRemote)
                {
                    ItemStack spawnme = result.copy();
                    if (!(result.getItem() instanceof ItemBlock))
                    {
                        int loot = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
                        if (loot > 0)
                        {
                            spawnme.stackSize *= (random.nextInt(loot + 1) + 1);
                        }
                    }
                    EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnme);

                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                    world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
                }
                for (int i = 0; i < 6; i++)
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
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving player)
    {
        return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, player, random);
    }

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
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
        AbilityHelper.onLeftClickEntity(stack, player, entity, this);
        return true;
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player)
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

    //Changes how much durability the base tool has
    public float getDurabilityModifier ()
    {
        return 1f;
    }
    
    public float getDamageModifier ()
    {
        return 1.0f;
    }

    //Right-click
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
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
        
        int hotbarSlot = player.inventory.currentItem;
        if (hotbarSlot == 0)
        {
            ItemStack next = player.inventory.getStackInSlot(8);
            if (next != null && next.getItem() instanceof ItemBlock)
            {
                next.getItem().onItemUse(next, player, world, x, y, z, side, clickX, clickY, clickZ);
                if (next.stackSize < 1)
                    player.inventory.setInventorySlotContents(8, null);
            }
        }
        else if (hotbarSlot < 8)
        {
            ItemStack next = player.inventory.getStackInSlot(hotbarSlot + 1);
            if (next != null && next.getItem() instanceof ItemBlock)
            {
                next.getItem().onItemUse(next, player, world, x, y, z, side, clickX, clickY, clickZ);
                if (next.stackSize < 1)
                    player.inventory.setInventorySlotContents(hotbarSlot + 1, null);
            }
        }
        return false;
    }

    /* IC2 Support
     * Every tool can be an electric tool if you modify it right
     */
    @Override
    public boolean canBeStoredInToolbox (ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean canProvideEnergy (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.hasKey("charge"))
            return false;

        return true;
    }

    @Override
    public int getChargedItemId (ItemStack stack)
    {
        return this.itemID;
    }

    @Override
    public int getEmptyItemId (ItemStack stack)
    {
        return this.itemID;
    }

    @Override
    public int getMaxCharge (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.hasKey("charge"))
            return 0;

        return 10000;
    }

    @Override
    public int getTier (ItemStack itemStack)
    {
        return 0;
    }

    @Override
    public int getTransferLimit (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.hasKey("charge"))
            return 0;

        return 32;
    }

    @Override
    public int charge (ItemStack stack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.hasKey("charge"))
            return 0;

        if (amount > 0)
        {
            if (amount > getTransferLimit(stack) && !ignoreTransferLimit)
            {
                amount = getTransferLimit(stack);
            }

            int charge = tags.getInteger("charge");

            if (amount > getMaxCharge(stack) - charge)
            {
                amount = getMaxCharge(stack) - charge;
            }

            charge += amount;

            if (!simulate)
            {
                tags.setInteger("charge", charge);
                stack.setItemDamage(1 + (getMaxCharge(stack) - charge) * (stack.getMaxDamage() - 2) / getMaxCharge(stack));
            }

            return amount;
        }

        else
            return 0;
    }

    @Override
    public int discharge (ItemStack stack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.hasKey("charge"))
            return 0;

        if (amount > 0)
        {
            if (amount > getTransferLimit(stack) && !ignoreTransferLimit)
            {
                amount = getTransferLimit(stack);
            }

            int charge = tags.getInteger("charge");

            if (amount > charge)
            {
                amount = charge;
            }

            charge -= amount;

            if (!simulate)
            {
                tags.setInteger("charge", charge);
                stack.setItemDamage(1 + (getMaxCharge(stack) - charge) * (stack.getMaxDamage() - 1) / getMaxCharge(stack));
            }

            return amount;
        }

        else
            return 0;
    }

    @Override
    public boolean canShowChargeToolTip (ItemStack itemStack)
    {
        return false;
    }

    @Override
    public boolean canUse (ItemStack itemStack, int amount)
    {
        return false;
    }

    //Vanilla overrides
    public boolean isItemTool (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean getIsRepairable (ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return false;
    }

    public boolean isRepairable ()
    {
        return false;
    }

    public int getItemEnchantability ()
    {
        return 0;
    }

    public boolean isFull3D ()
    {
        return true;
    }

}
