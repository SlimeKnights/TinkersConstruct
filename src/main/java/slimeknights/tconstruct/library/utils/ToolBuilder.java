package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.ItemStackList;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;

public final class ToolBuilder {

  private static Logger log = Util.getLogger("ToolBuilder");

  private ToolBuilder() {
  }

  @Nonnull
  public static ItemStack tryBuildTool(NonNullList<ItemStack> stacks, String name) {
    return tryBuildTool(stacks, name, TinkerRegistry.getTools());
  }

  /**
   * Takes an array of Itemstacks and tries to build a tool with it. Amount of itemstacks has to match exactly.
   *
   * @param stacks Input.
   * @return The built tool or null if none could be built.
   */
  @Nonnull
  public static ItemStack tryBuildTool(NonNullList<ItemStack> stacks, String name, Collection<ToolCore> possibleTools) {
    int length = -1;
    NonNullList<ItemStack> input;
    // remove trailing empty slots
    for(int i = 0; i < stacks.size(); i++) {
      if(stacks.get(i).isEmpty()) {
        if(length < 0) {
          length = i;
        }
      }
      else if(length >= 0) {
        // incorrect input. gap with null in the stacks passed
        return ItemStack.EMPTY;
      }
    }

    if(length < 0) {
      return ItemStack.EMPTY;
    }

    input = ItemStackList.of(stacks);

    for(Item item : possibleTools) {
      if(!(item instanceof ToolCore)) {
        continue;
      }
      ItemStack output = ((ToolCore) item).buildItemFromStacks(input);
      if(!output.isEmpty()) {
        // name the item
        if(name != null && !name.isEmpty()) {
          output.setStackDisplayName(name);
        }

        return output;
      }
    }

    return ItemStack.EMPTY;
  }

  /**
   * Adds the trait to the tag, taking max-count and already existing traits into account.
   *
   * @param rootCompound The root compound of the item
   * @param trait        The trait to add.
   * @param color        The color used on the tooltip. Will not be used if the trait already exists on the tool.
   */
  public static void addTrait(NBTTagCompound rootCompound, ITrait trait, int color) {
    // only registered traits allowed
    if(TinkerRegistry.getTrait(trait.getIdentifier()) == null) {
      log.error("addTrait: Trying to apply unregistered Trait {}", trait.getIdentifier());
      return;
    }

    IModifier modifier = TinkerRegistry.getModifier(trait.getIdentifier());

    if(modifier == null || !(modifier instanceof AbstractTrait)) {
      log.error("addTrait: No matching modifier for the Trait {} present", trait.getIdentifier());
      return;
    }

    AbstractTrait traitModifier = (AbstractTrait) modifier;

    NBTTagCompound tag = new NBTTagCompound();
    NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
    int index = TinkerUtil.getIndexInList(tagList, traitModifier.getModifierIdentifier());
    if(index < 0) {
      traitModifier.updateNBTforTrait(tag, color);
      tagList.appendTag(tag);
      TagUtil.setModifiersTagList(rootCompound, tagList);
    }
    else {
      tag = tagList.getCompoundTagAt(index);
    }

    traitModifier.applyEffect(rootCompound, tag);
  }

  @Nonnull
  public static ItemStack tryRepairTool(NonNullList<ItemStack> stacks, ItemStack toolStack, boolean removeItems) {
    if(toolStack == null || !(toolStack.getItem() instanceof IRepairable)) {
      return ItemStack.EMPTY;
    }

    // obtain a working copy of the items if the originals shouldn't be modified
    if(!removeItems) {
      stacks = Util.deepCopyFixedNonNullList(stacks);
    }

    return ((IRepairable) toolStack.getItem()).repair(toolStack, stacks);
  }

  /**
   * Takes a tool and an array of itemstacks and tries to modify the tool with those.
   * If removeItems is true, the items used in the process will be removed from the array.
   *
   * @param input       Items to modify the tool with
   * @param toolStack   The tool
   * @param removeItems If true the applied items will be removed from the array
   * @return The modified tool or null if something went wrong or no modifier applied.
   * @throws TinkerGuiException Thrown when not matching modifiers could be applied. Contains extra-information why the process failed.
   */
  @Nonnull
  public static ItemStack tryModifyTool(NonNullList<ItemStack> input, ItemStack toolStack, boolean removeItems)
      throws TinkerGuiException {
    ItemStack copy = toolStack.copy();

    // obtain a working copy of the items if the originals shouldn't be modified
    NonNullList<ItemStack> stacks = Util.deepCopyFixedNonNullList(input);
    NonNullList<ItemStack> usedStacks = Util.deepCopyFixedNonNullList(input);

    Set<IModifier> appliedModifiers = Sets.newHashSet();
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      Optional<RecipeMatch.Match> matchOptional;
      do {
        matchOptional = modifier.matches(stacks);
        ItemStack backup = copy.copy();

        // found a modifier that is applicable. Try to apply the match
        if(matchOptional.isPresent()) {
          RecipeMatch.Match match = matchOptional.get();
          // we need to apply the whole match
          while(match.amount > 0) {
            TinkerGuiException caughtException = null;
            boolean canApply = false;
            try {
              canApply = modifier.canApply(copy, toolStack);
            } catch(TinkerGuiException e) {
              caughtException = e;
            }

            // but can it be applied?
            if(canApply) {
              modifier.apply(copy);

              appliedModifiers.add(modifier);
              match.amount--;
            }
            else {
              // materials would allow another application, but modifier doesn't
              // if we have already applied another modifier we cancel the whole thing to prevent situations where
              // only a part of the modifiers gets applied. either all or none.
              // if we have a reason, rather tell the player that
              if(caughtException != null && !appliedModifiers.contains(modifier)) {
                throw caughtException;
              }

              copy = backup;
              RecipeMatch.removeMatch(stacks, match);
              break;
            }
          }

          if(match.amount == 0) {
            RecipeMatch.removeMatch(stacks, match);
            RecipeMatch.removeMatch(usedStacks, match);
          }
        }
      } while(matchOptional.isPresent());
    }

    // check if all itemstacks were touched - otherwise there's an invalid item in the input
    for(int i = 0; i < input.size(); i++) {
      if(!input.get(i).isEmpty() && ItemStack.areItemStacksEqual(input.get(i), stacks.get(i))) {
        if(!appliedModifiers.isEmpty()) {
          String error = I18n.translateToLocalFormatted("gui.error.no_modifier_for_item", input.get(i).getDisplayName());
          throw new TinkerGuiException(error);
        }
        return ItemStack.EMPTY;
      }
    }

    // update output itemstacks
    if(removeItems) {
      for(int i = 0; i < input.size(); i++) {
        // stacks might be null because stacksize got 0 during processing, we have to reflect that in the input
        // so the caller can identify that
        if(usedStacks.get(i).isEmpty()) {
          input.get(i).setCount(0);
        }
        else {
          input.get(i).setCount(usedStacks.get(i).getCount());
        }
      }
    }

    if(!appliedModifiers.isEmpty()) {
      // always rebuild tinkers items to ensure consistency and find problems earlier
      if(copy.getItem() instanceof TinkersItem) {
        NBTTagCompound root = TagUtil.getTagSafe(copy);
        rebuildTool(root, (TinkersItem) copy.getItem());
        copy.setTagCompound(root);
      }
      return copy;
    }

    return ItemStack.EMPTY;
  }

  /**
   * Takes a tool and toolparts and replaces the parts inside the tool with the given ones.
   * Toolparts have to be applicable to the tool. Toolparts must not be duplicates of currently used parts.
   *
   * @param toolStack   The tool to replace the parts in
   * @param toolPartsIn The toolparts.
   * @param removeItems If true the applied items will be removed from the array
   * @return The tool with the replaced parts or null if the conditions have not been met.
   */
  @Nonnull
  public static ItemStack tryReplaceToolParts(ItemStack toolStack, final NonNullList<ItemStack> toolPartsIn, final boolean removeItems)
      throws TinkerGuiException {
    if(toolStack == null || !(toolStack.getItem() instanceof TinkersItem)) {
      return ItemStack.EMPTY;
    }

    // we never modify the original. Caller can remove all of them if we return a result
    NonNullList<ItemStack> inputItems = ItemStackList.of(Util.deepCopyFixedNonNullList(toolPartsIn));
    if(!TinkerEvent.OnToolPartReplacement.fireEvent(inputItems, toolStack)) {
      // event cancelled
      return ItemStack.EMPTY;
    }
    // technically we don't need a deep copy here, but meh. less code.
    final NonNullList<ItemStack> toolParts = Util.deepCopyFixedNonNullList(inputItems);

    TIntIntMap assigned = new TIntIntHashMap();
    TinkersItem tool = (TinkersItem) toolStack.getItem();
    // materiallist has to be copied because it affects the actual NBT on the tool if it's changed
    final NBTTagList materialList = TagUtil.getBaseMaterialsTagList(toolStack).copy();

    // assing each toolpart to a slot in the tool
    for(int i = 0; i < toolParts.size(); i++) {
      ItemStack part = toolParts.get(i);
      if(part.isEmpty()) {
        continue;
      }
      if(!(part.getItem() instanceof IToolPart)) {
        // invalid item for toolpart replacement
        return ItemStack.EMPTY;
      }

      int candidate = -1;
      // find an applicable slot in the tool structure corresponding to the toolparts position
      List<PartMaterialType> pms = tool.getRequiredComponents();
      for(int j = 0; j < pms.size(); j++) {
        PartMaterialType pmt = pms.get(j);
        String partMat = ((IToolPart) part.getItem()).getMaterial(part).getIdentifier();
        String currentMat = materialList.getStringTagAt(j);
        // is valid and not the same material?
        if(pmt.isValid(part) && !partMat.equals(currentMat)) {
          // part not taken up by previous part already?
          if(!assigned.valueCollection().contains(j)) {
            candidate = j;
            // if a tool has multiple of the same parts we may want to replace another one as the currently selected
            // for that purpose we only allow to overwrite the current selection if the input slot is a later one than the current one
            if(i <= j) {
              break;
            }
          }
        }
      }

      // no assignment found for a part. Invalid input.
      if(candidate < 0) {
        return ItemStack.EMPTY;
      }
      assigned.put(i, candidate);
    }

    // did we assign nothing?
    if(assigned.isEmpty()) {
      return ItemStack.EMPTY;
    }

    // We now know which parts to replace with which inputs. Yay. Now we only have to do so.
    // to do so we simply switch out the materials used and rebuild the tool
    assigned.forEachEntry((i, j) -> {
      String mat = ((IToolPart) toolParts.get(i).getItem()).getMaterial(toolParts.get(i)).getIdentifier();
      materialList.set(j, new NBTTagString(mat));
      if(removeItems) {
        if(i < toolPartsIn.size() && !toolPartsIn.get(i).isEmpty()) {
          toolPartsIn.get(i).shrink(1);
        }
      }
      return true;
    });

    // check that each material is still compatible with each modifier
    TinkersItem tinkersItem = (TinkersItem) toolStack.getItem();
    ItemStack copyToCheck = tinkersItem.buildItem(TinkerUtil.getMaterialsFromTagList(materialList));
    // this includes traits
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(toolStack);
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String id = modifiers.getStringTagAt(i);
      IModifier mod = TinkerRegistry.getModifier(id);

      boolean canApply = false;
      try {
        // will throw an exception if it can't apply
        canApply = mod != null && mod.canApply(copyToCheck, copyToCheck);
      } catch(TinkerGuiException e) {
        // try again with more modifiers, in case something modified them (tinkers tool leveling)
        // ensure that free modifiers are present (
        if(ToolHelper.getFreeModifiers(copyToCheck) < ToolCore.DEFAULT_MODIFIERS) {
          ItemStack copyWithModifiers = copyToCheck.copy();
          NBTTagCompound nbt = TagUtil.getToolTag(copyWithModifiers);
          nbt.setInteger(Tags.FREE_MODIFIERS, ToolCore.DEFAULT_MODIFIERS);
          TagUtil.setToolTag(copyWithModifiers, nbt);
          canApply = mod.canApply(copyWithModifiers, copyWithModifiers);
        }
      }
      if(!canApply) {
        throw new TinkerGuiException();
      }
    }

    ItemStack output = toolStack.copy();
    TagUtil.setBaseMaterialsTagList(output, materialList);
    NBTTagCompound tag = TagUtil.getTagSafe(output);
    rebuildTool(tag, (TinkersItem) output.getItem());
    output.setTagCompound(tag);

    // check if the output has enough durability. we only allow it if the result would not be broken
    if(output.getItemDamage() > output.getMaxDamage()) {
      String error = I18n.translateToLocalFormatted("gui.error.not_enough_durability", output.getItemDamage() - output.getMaxDamage());
      throw new TinkerGuiException(error);
    }

    return output;
  }

  /**
   * Takes a pattern and itemstacks and crafts the materialitem of the pattern out of it.
   * The output consists of an ItemStack[2] array that contains the part in the first slot and eventual leftover output in the 2nd one.
   * The itemstacks have to match at least 1 material.
   * If multiple materials match, matches with multiple items are preferred.
   * Otherwise the first match will be taken.
   *
   * @param pattern       Input-pattern. Has to be a Pattern.
   * @param materialItems The Itemstacks to craft the item out of
   * @param removeItems   If true the match will be removed from the passed items
   * @return ItemStack[2] Array containing the built item in the first slot and eventual secondary output in the second one. Null if no item could be built.
   */
  public static NonNullList<ItemStack> tryBuildToolPart(ItemStack pattern, NonNullList<ItemStack> materialItems, boolean removeItems)
      throws TinkerGuiException {
    Item itemPart = Pattern.getPartFromTag(pattern);
    if(itemPart == null || !(itemPart instanceof MaterialItem) || !(itemPart instanceof IToolPart)) {
      String error = I18n.translateToLocalFormatted("gui.error.invalid_pattern");
      throw new TinkerGuiException(error);
    }

    IToolPart part = (IToolPart) itemPart;

    if(!removeItems) {
      materialItems = Util.deepCopyFixedNonNullList(materialItems);
    }

    // find the material from the input
    Optional<RecipeMatch.Match> match = Optional.empty();
    Material foundMaterial = null;
    for(Material material : TinkerRegistry.getAllMaterials()) {
      // craftable?
      if(!material.isCraftable()) {
        continue;
      }
      Optional<RecipeMatch.Match> newMatch = material.matches(materialItems, part.getCost());
      if(!newMatch.isPresent()) {
        continue;
      }

      // we found a match, yay
      if(!match.isPresent()) {
        match = newMatch;
        foundMaterial = material;
        // is it more complex than the old one?
      }
    }

    // nope, no material
    if(!match.isPresent()) {
      return null;
    }

    ItemStack output = ((MaterialItem) itemPart).getItemstackWithMaterial(foundMaterial);
    if(output.isEmpty()) {
      return null;
    }
    if(output.getItem() instanceof IToolPart && !((IToolPart) output.getItem()).canUseMaterial(foundMaterial)) {
      return null;
    }

    RecipeMatch.removeMatch(materialItems, match.get());

    // check if we have secondary output
    ItemStack secondary = ItemStack.EMPTY;
    int leftover = (match.get().amount - part.getCost()) / Material.VALUE_Shard;
    if(leftover > 0) {
      secondary = TinkerRegistry.getShard(foundMaterial);
      secondary.setCount(leftover);
    }

    // build an item with this
    return ListUtil.getListFrom(output, secondary);
  }

  /**
   * Rebuilds a tool from its raw data, material info and applied modifiers
   *
   * @param rootNBT The root NBT tag compound of the tool to to rebuild. The NBT will be modified, overwriting old
   *                data.
   */
  public static void rebuildTool(NBTTagCompound rootNBT, TinkersItem tinkersItem) throws TinkerGuiException {
    boolean broken = TagUtil.getToolTag(rootNBT).getBoolean(Tags.BROKEN);
    // Recalculate tool base stats from material stats
    NBTTagList materialTag = TagUtil.getBaseMaterialsTagList(rootNBT);
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(materialTag);
    List<PartMaterialType> pms = tinkersItem.getRequiredComponents();

    // ensure all needed Stats are present
    while(materials.size() < pms.size()) {
      materials.add(Material.UNKNOWN);
    }
    for(int i = 0; i < pms.size(); i++) {
      if(!pms.get(i).isValidMaterial(materials.get(i))) {
        materials.set(i, Material.UNKNOWN);
      }
    }

    // the base stats of the tool
    NBTTagCompound toolTag = tinkersItem.buildTag(materials);
    TagUtil.setToolTag(rootNBT, toolTag);
    // and its copy for reference
    rootNBT.setTag(Tags.TOOL_DATA_ORIG, toolTag.copy());

    // save the old modifiers list and clean up all tags that get set by modifiers/traits
    NBTTagList modifiersTagOld = TagUtil.getModifiersTagList(rootNBT);
    rootNBT.removeTag(Tags.TOOL_MODIFIERS); // the active-modifiers tag
    rootNBT.setTag(Tags.TOOL_MODIFIERS, new NBTTagList());
    rootNBT.removeTag("ench"); // and the enchantments tag
    rootNBT.removeTag(Tags.ENCHANT_EFFECT); // enchant effect too, will be readded by a trait either way

    // clean up traits
    rootNBT.removeTag(Tags.TOOL_TRAITS);
    tinkersItem.addMaterialTraits(rootNBT, materials);

    // fire event
    TinkerEvent.OnItemBuilding.fireEvent(rootNBT, ImmutableList.copyOf(materials), tinkersItem);

    // reapply modifiers
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(rootNBT);
    NBTTagList modifiersTag = TagUtil.getModifiersTagList(rootNBT);
    // copy over and reapply all relevant modifiers
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String identifier = modifiers.getStringTagAt(i);
      IModifier modifier = TinkerRegistry.getModifier(identifier);
      if(modifier == null) {
        log.debug("Missing modifier: {}", identifier);
        continue;
      }

      NBTTagCompound tag;
      int index = TinkerUtil.getIndexInList(modifiersTagOld, modifier.getIdentifier());

      if(index >= 0) {
        tag = modifiersTagOld.getCompoundTagAt(index);
      }
      else {
        tag = new NBTTagCompound();
      }

      modifier.applyEffect(rootNBT, tag);
      if(!tag.hasNoTags()) {
        int indexNew = TinkerUtil.getIndexInList(modifiersTag, modifier.getIdentifier());
        if(indexNew >= 0) {
          modifiersTag.set(indexNew, tag);
        }
        else {
          modifiersTag.appendTag(tag);
        }
      }
    }

    // remaining info, get updated toolTag
    toolTag = TagUtil.getToolTag(rootNBT);
    // adjust free modifiers
    int freeModifiers = toolTag.getInteger(Tags.FREE_MODIFIERS);
    freeModifiers -= TagUtil.getBaseModifiersUsed(rootNBT);
    toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, freeModifiers));

    // broken?
    if(broken) {
      toolTag.setBoolean(Tags.BROKEN, true);
    }

    TagUtil.setToolTag(rootNBT, toolTag);

    if(freeModifiers < 0) {
      throw new TinkerGuiException(Util.translateFormatted("gui.error.not_enough_modifiers", -freeModifiers));
    }
  }

  public static short getEnchantmentLevel(NBTTagCompound rootTag, Enchantment enchantment) {
    NBTTagList enchantments = rootTag.getTagList("ench", 10);

    int id = Enchantment.getEnchantmentID(enchantment);

    for(int i = 0; i < enchantments.tagCount(); i++) {
      if(enchantments.getCompoundTagAt(i).getShort("id") == id) {
        return enchantments.getCompoundTagAt(i).getShort("lvl");
      }
    }

    return 0;
  }

  public static void addEnchantment(NBTTagCompound rootTag, Enchantment enchantment) {
    NBTTagList enchantments = rootTag.getTagList("ench", 10);

    NBTTagCompound enchTag = new NBTTagCompound();
    int enchId = Enchantment.getEnchantmentID(enchantment);

    int id = -1;
    for(int i = 0; i < enchantments.tagCount(); i++) {
      if(enchantments.getCompoundTagAt(i).getShort("id") == enchId) {
        enchTag = enchantments.getCompoundTagAt(i);
        id = i;
        break;
      }
    }

    int level = enchTag.getShort("lvl") + 1;
    level = Math.min(level, enchantment.getMaxLevel());
    enchTag.setShort("id", (short) enchId);
    enchTag.setShort("lvl", (short) level);

    if(id < 0) {
      enchantments.appendTag(enchTag);
    }
    else {
      enchantments.set(id, enchTag);
    }

    rootTag.setTag("ench", enchantments);
  }
}
