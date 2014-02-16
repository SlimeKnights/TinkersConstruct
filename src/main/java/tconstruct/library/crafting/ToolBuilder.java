package tconstruct.library.crafting;

/** Once upon a time, too many tools to count. Let's put them together automatically */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorMod;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.TToolMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMod;
import tconstruct.library.util.IToolPart;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class ToolBuilder
{
    public static ToolBuilder instance = new ToolBuilder();

    public HashMap<String, ToolRecipe> recipeList = new HashMap<String, ToolRecipe>();
    public List<ToolRecipe> combos = new ArrayList<ToolRecipe>();
    public HashMap<String, String> modifiers = new HashMap<String, String>();
    public List<ToolMod> toolMods = new ArrayList<ToolMod>();
    public List<ArmorMod> armorMods = new ArrayList<ArmorMod>();

    /* Build tools */
    public static void addNormalToolRecipe (ToolCore output, Item head, Item handle)
    {
        ToolRecipe recipe = instance.recipeList.get(output.getToolName());
        if (recipe != null)
        {
            recipe.addHeadItem(head);
            recipe.addHandleItem(handle);
        }
        else
        {
            recipe = new ToolRecipe(head, handle, output);
            instance.combos.add(recipe);
            instance.recipeList.put(output.getToolName(), recipe);
        }
    }

    public static void addNormalToolRecipe (ToolCore output, Item head, Item handle, Item accessory)
    {
        // instance.combos.add(new ToolRecipe(head, accessory, output));
        ToolRecipe recipe = instance.recipeList.get(output.getToolName());
        if (recipe != null)
        {
            recipe.addHeadItem(head);
            recipe.addHandleItem(handle);
            recipe.addAccessoryItem(accessory);
        }
        else
        {
            recipe = new ToolRecipe(head, handle, accessory, output);
            instance.combos.add(recipe);
            instance.recipeList.put(output.getToolName(), recipe);
        }
    }

    public static void addNormalToolRecipe (ToolCore output, Item head, Item handle, Item accessory, Item extra)
    {
        ToolRecipe recipe = instance.recipeList.get(output.getToolName());
        if (recipe != null)
        {
            recipe.addHeadItem(head);
            recipe.addHandleItem(handle);
            recipe.addAccessoryItem(accessory);
            recipe.addExtraItem(extra);
        }
        else
        {
            recipe = new ToolRecipe(head, handle, accessory, extra, output);
            instance.combos.add(recipe);
            instance.recipeList.put(output.getToolName(), recipe);
        }
    }

    public static void addCustomToolRecipe (ToolRecipe recipe)
    {
        instance.combos.add(recipe);
    }

    public static void addToolRecipe (ToolCore output, Item... items)
    {
        if (items.length == 2)
            addNormalToolRecipe(output, items[0], items[1]);
        if (items.length == 3)
            addNormalToolRecipe(output, items[0], items[1], items[2]);
        if (items.length == 4)
            addNormalToolRecipe(output, items[0], items[1], items[2], items[3]);
    }

    public ToolCore getMatchingRecipe (Item head, Item handle, Item accessory, Item extra)
    {
        for (ToolRecipe recipe : combos)
        {
            if (recipe.validHead(head) && recipe.validHandle(handle) && recipe.validAccessory(accessory) && recipe.validExtra(extra))
                return recipe.getType();
        }
        return null;
    }

    // Builds a tool from the parts given
    public ItemStack buildTool (ItemStack headStack, ItemStack handleStack, ItemStack accessoryStack, String name)
    {
        return buildTool(headStack, handleStack, accessoryStack, null, name);
    }

    public int getMaterialID (ItemStack stack)
    {
        if (stack == null)
            return -1;
        Item item = stack.getItem();
        if (item == Items.stick)
            return 0;
        else if (item == Items.bone)
            return 5;
        else if (item instanceof IToolPart)
            return ((IToolPart) item).getMaterialID(stack);

        return -1;
    }

    public ItemStack buildTool (ItemStack headStack, ItemStack handleStack, ItemStack accessoryStack, ItemStack extraStack, String name)
    {
        if (headStack != null)
        {
            if (headStack.getItem() instanceof ToolCore)
                return modifyTool(headStack, handleStack, accessoryStack, extraStack, name);
            else if (headStack.getItem() instanceof ArmorCore)
                return modifyArmor(headStack, new ItemStack[] { handleStack, accessoryStack, extraStack }, name);
        }

        if (headStack == null || handleStack == null) // Nothing to build
                                                      // without these. All
                                                      // tools need at least
                                                      // two parts!
            return null;

        ToolCore item;
        boolean validMaterials = true;
        int head = -1, handle = -1, accessory = -1, extra = -1;
        head = getMaterialID(headStack);
        if (head == -1)
            validMaterials = false;

        handle = getMaterialID(handleStack);
        if (handle == -1)
            validMaterials = false;

        if (!validMaterials)
            return null;

        if (accessoryStack == null)
        {
            item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), null, null);
        }
        else
        {
            /*
             * if (accessoryStack.getItem() instanceof IToolPart) accessory =
             * ((IToolPart)
             * accessoryStack.getItem()).getMaterialID(accessoryStack); else
             * return null;
             */

            accessory = getMaterialID(accessoryStack);
            if (accessory == -1)
                return null;

            if (extraStack != null)
            {
                /*
                 * if (extraStack.getItem() instanceof IToolPart) extra =
                 * ((IToolPart) extraStack.getItem()).getMaterialID(extraStack);
                 * else return null;
                 */

                extra = getMaterialID(extraStack);
                if (extra == -1)
                    return null;

                item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), accessoryStack.getItem(), extraStack.getItem());
            }
            else
            {
                item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), accessoryStack.getItem(), null);
            }
        }

        // TConstruct.logger.info("Valid: "+item);

        if (item == null)
            return null;

        TToolMaterial headMat = null, handleMat = null, accessoryMat = null, extraMat = null;
        headMat = TConstructRegistry.getMaterial(head);
        handleMat = TConstructRegistry.getMaterial(handle);

        if (accessory != -1)
            accessoryMat = TConstructRegistry.getMaterial(accessory);

        if (extra != -1)
            extraMat = TConstructRegistry.getMaterial(extra);

        int durability = headMat.durability();
        int heads = 1;
        int handles = 0;
        float modifier = 1f;
        int attack = headMat.attack();

        if (item.durabilityTypeHandle() == 2)
        {
            heads++;
            durability += handleMat.durability();
            attack += handleMat.attack();
        }
        else if (item.durabilityTypeHandle() == 1)
        {
            handles++;
            modifier = handleMat.handleDurability();
        }

        if (accessory != -1)
        {
            if (item.durabilityTypeAccessory() == 2)
            {
                heads++;
                durability += accessoryMat.durability();
                attack += accessoryMat.attack();
            }
            else if (item.durabilityTypeAccessory() == 1)
            {
                handles++;
                modifier += accessoryMat.handleDurability();
            }
        }

        if (extra != -1)
        {
            if (item.durabilityTypeExtra() == 2)
            {
                heads++;
                durability += extraMat.durability();
                attack += extraMat.attack();
            }
            else if (item.durabilityTypeExtra() == 1)
            {
                handles++;
                modifier += extraMat.handleDurability();
            }
        }

        if (handles > 0)
        {
            modifier *= (0.5 + handles * 0.5);
            modifier /= handles;
        }

        durability = (int) (durability / heads * (0.5 + heads * 0.5) * modifier * item.getDurabilityModifier());
        attack = attack / heads + item.getDamageVsEntity(null);
        if (attack % heads != 0)
            attack++;

        ItemStack tool = new ItemStack(item);
        NBTTagCompound compound = new NBTTagCompound();

        compound.setTag("InfiTool", new NBTTagCompound());
        compound.getCompoundTag("InfiTool").setInteger("Head", head);
        compound.getCompoundTag("InfiTool").setInteger("RenderHead", head);

        compound.getCompoundTag("InfiTool").setInteger("Handle", handle);
        compound.getCompoundTag("InfiTool").setInteger("RenderHandle", handle);

        if (accessory != -1)
        {
            compound.getCompoundTag("InfiTool").setInteger("Accessory", accessory);
            compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", accessory);
        }
        if (extra != -1)
        {
            compound.getCompoundTag("InfiTool").setInteger("Extra", extra);
            compound.getCompoundTag("InfiTool").setInteger("RenderExtra", extra);
        }

        compound.getCompoundTag("InfiTool").setInteger("Damage", 0); // Damage
                                                                     // is
                                                                     // damage
                                                                     // to
                                                                     // the
                                                                     // tool
        compound.getCompoundTag("InfiTool").setInteger("TotalDurability", durability);
        compound.getCompoundTag("InfiTool").setInteger("BaseDurability", durability);
        compound.getCompoundTag("InfiTool").setInteger("BonusDurability", 0); // Modifier
        compound.getCompoundTag("InfiTool").setFloat("ModDurability", 0f); // Modifier
        compound.getCompoundTag("InfiTool").setBoolean("Broken", false);
        compound.getCompoundTag("InfiTool").setInteger("Attack", attack);

        compound.getCompoundTag("InfiTool").setInteger("MiningSpeed", headMat.toolSpeed());
        compound.getCompoundTag("InfiTool").setInteger("HarvestLevel", headMat.harvestLevel());
        if (item.durabilityTypeHandle() == 2)
        {
            compound.getCompoundTag("InfiTool").setInteger("MiningSpeedHandle", handleMat.toolSpeed());
            compound.getCompoundTag("InfiTool").setInteger("HarvestLevelHandle", handleMat.harvestLevel());
        }
        if (accessory != -1 && item.durabilityTypeAccessory() == 2)
        {
            compound.getCompoundTag("InfiTool").setInteger("MiningSpeed2", accessoryMat.toolSpeed());
            compound.getCompoundTag("InfiTool").setInteger("HarvestLevel2", accessoryMat.harvestLevel());
        }

        if (extra != -1 && item.durabilityTypeExtra() == 2)
        {
            compound.getCompoundTag("InfiTool").setInteger("MiningSpeedExtra", extraMat.toolSpeed());
            compound.getCompoundTag("InfiTool").setInteger("HarvestLevelExtra", extraMat.harvestLevel());
        }

        compound.getCompoundTag("InfiTool").setInteger("Unbreaking", buildReinforced(headMat, handleMat, accessoryMat, extraMat));
        compound.getCompoundTag("InfiTool").setFloat("Shoddy", buildShoddy(headMat, handleMat, accessoryMat, extraMat));

        int modifiers = item.getModifierAmount();
        if (accessory == -1)
            modifiers += (head == 9 ? 2 : 0);
        else
            modifiers += (head == 9 ? 1 : 0) + (accessory == 9 ? 1 : 0);
        modifiers += +(handle == 9 ? 1 : 0);
        modifiers += +(extra == 9 ? 1 : 0);

        compound.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);

        if (name != null && !name.equals(""))
        {
            compound.setTag("display", new NBTTagCompound());
            compound.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        ToolCraftEvent.NormalTool event = new ToolCraftEvent.NormalTool(item, compound, new TToolMaterial[] { headMat, handleMat, accessoryMat, extraMat });
        MinecraftForge.EVENT_BUS.post(event);

        if (event.getResult() == Result.DEFAULT)
        {
            tool.setTagCompound(compound);
        }
        else if (event.getResult() == Result.ALLOW)
        {
            tool = event.getResultStack();
        }
        else
        {
            tool = null;
        }

        return tool;
    }

    public ItemStack modifyTool (ItemStack input, ItemStack[] slots, String name)
    {
        ItemStack tool = input.copy();
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.removeTag("Built");

        boolean built = false;
        for (ToolMod mod : toolMods)
        {
            if (mod.matches(slots, tool))
            {
                built = true;
                mod.addMatchingEffect(tool);
                mod.modify(slots, tool);
            }
        }

        tags = tool.getTagCompound();
        if (name != null && !name.equals("") && !tags.hasKey("display"))
        {
            tags.setTag("display", new NBTTagCompound());
            tags.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        if (built)
            return tool;
        else
            return null;
    }

    public ItemStack modifyArmor (ItemStack input, ItemStack[] slots, String name)
    {
        ItemStack armor = input.copy();
        if (!armor.hasTagCompound())
            addArmorTag(input);
        NBTTagCompound tags = armor.getTagCompound().getCompoundTag("TinkerArmor");
        tags.removeTag("Built");

        boolean built = false;
        for (ArmorMod mod : armorMods)
        {
            if (mod.matches(slots, armor))
            {
                built = true;
                mod.addMatchingEffect(armor);
                mod.modify(slots, armor);
            }
        }

        tags = armor.getTagCompound();
        if (name != null && !name.equals("") && !tags.hasKey("display"))
        {
            tags.setTag("display", new NBTTagCompound());
            tags.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        if (built)
            return armor;
        else
            return null;
    }

    @Deprecated
    public ItemStack modifyTool (ItemStack input, ItemStack topSlot, ItemStack bottomSlot, ItemStack extraStack, String name)
    {
        if (extraStack != null)
            return null;

        ItemStack tool = input.copy();
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.removeTag("Built");

        if (topSlot == null && bottomSlot == null)
            return tool;

        boolean built = false;
        for (ToolMod mod : toolMods)
        {
            ItemStack[] slots = new ItemStack[] { topSlot, bottomSlot };
            if (mod.matches(slots, tool))
            {
                built = true;
                mod.addMatchingEffect(tool);
                mod.modify(slots, tool);
            }
        }

        tags = tool.getTagCompound();
        if (name != null && !name.equals("") && !tags.hasKey("display"))
        {
            tags.setTag("display", new NBTTagCompound());
            tags.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        if (built)
            return tool;
        else
            return null;
    }

    public void addArmorTag (ItemStack armor) // Not sure if temporary or not
    {
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag("TinkerArmor", armorTag);

        armor.setTagCompound(baseTag);
    }

    int buildReinforced (TToolMaterial headMat, TToolMaterial handleMat, TToolMaterial accessoryMat, TToolMaterial extraMat)
    {
        int reinforced = 0;

        int dHead = headMat.reinforced();
        int dHandle = handleMat.reinforced();
        int dAccessory = 0;
        if (accessoryMat != null)
            dAccessory = accessoryMat.reinforced();
        int dExtra = 0;
        if (extraMat != null)
            dExtra = extraMat.reinforced();

        if (dHead > reinforced)
            reinforced = dHead;
        if (dHandle > reinforced)
            reinforced = dHandle;
        if (dAccessory > reinforced)
            reinforced = dAccessory;
        if (dExtra > reinforced)
            reinforced = dExtra;

        return reinforced;
    }

    float buildShoddy (TToolMaterial headMat, TToolMaterial handleMat, TToolMaterial accessoryMat, TToolMaterial extraMat)
    {
        float sHead = headMat.shoddy();
        float sHandle = handleMat.shoddy();
        if (extraMat != null)
        {
            float sAccessory = accessoryMat.shoddy();
            float sExtra = extraMat.shoddy();
            return (sHead + sHandle + sAccessory + sExtra) / 4f;
        }

        if (accessoryMat != null)
        {
            float sAccessory = accessoryMat.shoddy();
            return (sHead + sHandle + sAccessory) / 3f;
        }
        return (sHead + sHandle) / 2f;
    }

    public static void registerToolMod (ToolMod mod)
    {
        if (mod == null)
            throw new NullPointerException("Tool modifier cannot be null.");
        instance.toolMods.add(mod);
    }

    public static void registerArmorMod (ArmorMod mod)
    {
        if (mod == null)
            throw new NullPointerException("Armor modifier cannot be null.");
        instance.armorMods.add(mod);
    }
}
