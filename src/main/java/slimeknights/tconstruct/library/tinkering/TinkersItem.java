package slimeknights.tconstruct.library.tinkering;


import gnu.trove.set.hash.THashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.ToolTagUtil;

/**
 * The base for each Tinker tool.
 */
public abstract class TinkersItem extends Item implements ITinkerable, IModifyable, IRepairable {

  public final PartMaterialType[] requiredComponents;
  // used to classify what the thing can do
  protected final Set<Category> categories = new THashSet<Category>();

  public TinkersItem(PartMaterialType... requiredComponents) {
    this.requiredComponents = requiredComponents;

    this.setMaxStackSize(1);
    //this.setHasSubtypes(true);
  }

  /* Tool Information */
  protected void addCategory(Category... categories) {
    for(Category category : categories) {
      this.categories.add(category);
    }
  }

  public boolean hasCategory(Category category) {
    return categories.contains(category);
  }

  /* Building the Item */
  public boolean validComponent(int slot, ItemStack stack) {
    if(slot > requiredComponents.length || slot < 0) {
      return false;
    }

    return requiredComponents[slot].isValid(stack);
  }

  /**
   * Builds an Itemstack of this tool with the given materials, if applicable.
   *
   * @param stacks Items to build with. Have to be in the correct order and have exact length. No nulls!
   * @return The built item or null if invalid input.
   */
  public ItemStack buildItemFromStacks(ItemStack[] stacks) {
    List<Material> materials = new ArrayList<Material>(stacks.length);

    if(stacks.length != requiredComponents.length) {
      return null;
    }

    // not a valid part arrangement for this tool
    for(int i = 0; i < stacks.length; i++) {
      if(!validComponent(i, stacks[i])) {
        return null;
      }

      materials.add(TinkerUtil.getMaterialFromStack(stacks[i]));
    }

    return buildItem(materials);
  }

  /**
   * Builds an Itemstack of this tool with the given materials.
   *
   * @param materials Materials to build with. Have to be in the correct order. No nulls!
   * @return The built item or null if invalid input.
   */
  public ItemStack buildItem(List<Material> materials) {
    ItemStack tool = new ItemStack(this);
    tool.setTagCompound(buildItemNBT(materials));

    return tool;
  }

  /**
   * Builds the NBT for a new tinker item with the given data.
   *
   * @param materials Materials to build with. Have to be in the correct order. No nulls!
   * @return The built nbt
   */
  public NBTTagCompound buildItemNBT(List<Material> materials) {
    NBTTagCompound basetag = new NBTTagCompound();
    NBTTagCompound toolTag = buildTag(materials);
    NBTTagCompound dataTag = buildData(materials);

    basetag.setTag(Tags.BASE_DATA, dataTag);
    basetag.setTag(Tags.TOOL_DATA, toolTag);
    // copy of the original tool data
    basetag.setTag(Tags.TOOL_DATA_ORIG, toolTag.copy());

    // add traits
    addMaterialTraits(basetag, materials);

    return basetag;
  }

  /**
   * Creates an NBT Tag with the materials that were used to build the item.
   */
  private NBTTagCompound buildData(List<Material> materials) {
    NBTTagCompound base = new NBTTagCompound();
    NBTTagList materialList = new NBTTagList();

    for(Material material : materials) {
      materialList.appendTag(new NBTTagString(material.identifier));
    }

    // pre-type base-modifier list
    NBTTagList modifierList = new NBTTagList();
    // we cannot set the type directly, but it gets typed by adding a tag, so we add and remove one
    modifierList.appendTag(new NBTTagString());
    modifierList.removeTag(0);

    base.setTag(Tags.BASE_MATERIALS, materialList);
    base.setTag(Tags.BASE_MODIFIERS, modifierList);

    return base;
  }

  /**
   * Builds an unusable tool that only has the rendering info
   */
  public ItemStack buildItemForRendering(List<Material> materials) {
    ItemStack tool = new ItemStack(this);
    NBTTagCompound base = new NBTTagCompound();
    base.setTag(Tags.BASE_DATA, buildData(materials));
    tool.setTagCompound(base);

    return tool;
  }

  public abstract NBTTagCompound buildTag(List<Material> materials);

  public void addMaterialTraits(NBTTagCompound root, List<Material> materials) {
    for(Material material : materials) {
      for(ITrait trait : material.getAllTraits()) {
        ToolBuilder.addTrait(root, trait, material.textColor);
      }
    }
  }

  /* Repairing */

  @Override
  public ItemStack repair(ItemStack repairable, ItemStack[] repairItems) {
    if(repairable.getItemDamage() == 0 && !ToolHelper.isBroken(repairable)) {
      // undamaged and not broken - no need to repair
      return null;
    }

    // we assume the first required part exclusively determines repair material
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(repairable));
    if(materials.isEmpty()) {
      return null;
    }

    Material material = materials.get(0);
    ItemStack[] items = Util.copyItemStackArray(repairItems);
    // ensure the items only contain valid items
    RecipeMatch.Match match = material.matches(items);

    // not a single match -> nothing to repair with
    if(match == null) {
      return null;
    }

    while((match = material.matches(items)) != null) {
      RecipeMatch.removeMatch(items, match);
    }

    for(int i = 0; i < repairItems.length; i++) {
      // was non-null and did not get modified (stacksize changed or null now, usually)
      if(repairItems[i] != null && ItemStack.areItemStacksEqual(repairItems[i], items[i])) {
        // found an item that was not touched
        return null;
      }
    }

    // now do it all over again with the real items, to actually repair \o/
    ItemStack item = repairable.copy();
    // repair for each match so the end result is the same as if each one had been applied individually
    while((match = material.matches(repairItems)) != null) {
      // is the tool still damaged?
      if(item.getItemDamage() == 0) {
        // we're done
        break;
      }
      // todo: fire event?
      // do the actual repair
      int amount = calculateRepair(item, match.amount);
      ToolHelper.repairTool(item, amount);

      // save that we repaired it :I
      NBTTagCompound tag = TagUtil.getExtraTag(item);
      TagUtil.addInteger(tag, Tags.REPAIR_COUNT, 1);
      TagUtil.setExtraTag(item, tag);

      // use up items
      RecipeMatch.removeMatch(repairItems, match);
    }

    return item;
  }

  protected int calculateRepair(ItemStack tool, int materialValue)
  {
    int baseDurability = TagUtil.getOriginalToolStats(tool).durability;
    float increase = (50f  + (baseDurability * 0.4f * materialValue)/Material.VALUE_Ingot);

    int modifiers = ToolTagUtil.getFreeModifiers(TagUtil.getTagSafe(tool));
    float mods = 1.0f;
    if (modifiers == 2)
      mods = 0.9f;
    else if (modifiers == 1)
      mods = 0.8f;
    else if (modifiers == 0)
      mods = 0.7f;

    increase *= mods;

    NBTTagCompound tag = TagUtil.getExtraTag(tool);
    int repair = tag.getInteger(Tags.REPAIR_COUNT);
    float repairCount = (100 - repair) / 100f;
    if (repairCount < 0.5f)
      repairCount = 0.5f;
    increase *= repairCount;

    return (int)increase;
  }

  /* Information */

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip,
                             boolean advanced) {
    // modifiers
    NBTTagList tagList = TagUtil.getModifiersTagList(stack);
    for(int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = tagList.getCompoundTagAt(i);
      ModifierNBT data = ModifierNBT.readTag(tag);

      // get matching modifier
      IModifier modifier = TinkerRegistry.getModifier(data.identifier);
      if(modifier == null) {
        continue;
      }

      tooltip.add(data.color.toString() + modifier.getLocalizedName());
    }
    // remaining data
    if(Config.extraTooltips) {
      Collections.addAll(tooltip, this.getInformation(stack));
    }
  }

  /* NBT loading */

  @Override
  public boolean updateItemStackNBT(NBTTagCompound nbt) {
    // when the itemstack is loaded from NBT we recalculate all the data
    if(nbt.hasKey(Tags.BASE_DATA)) {
      ToolBuilder.rebuildTool(nbt, this);
    }

    // return value shoudln't matter since it's never checked
    return true;
  }
}
