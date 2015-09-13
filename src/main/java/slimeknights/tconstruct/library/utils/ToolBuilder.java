package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Sets;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
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
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;

public final class ToolBuilder {

  private static Logger log = Util.getLogger("ToolBuilder");

  private ToolBuilder() {
  }

  /**
   * Takes an array of Itemstacks and tries to build a tool with it. Amount of itemstacks has to match exactly.
   *
   * @param stacks Input.
   * @return The built tool or null if none could be built.
   */
  public static ItemStack tryBuildTool(ItemStack[] stacks, String name) {
    int length = -1;
    ItemStack[] input;
    // remove trailing nulls
    for(int i = 0; i < stacks.length; i++) {
      if(stacks[i] == null) {
        if(length < 0) {
          length = i;
        }
      }
      else if(length >= 0) {
        // incorrect input. gap with null in the stacks passed
        return null;
      }
    }

    if(length < 0) {
      return null;
    }

    input = Arrays.copyOf(stacks, length);

    for(ToolCore tool : TinkerRegistry.getTools()) {
      ItemStack output = tool.buildItemFromStacks(input);
      if(output != null) {
        // name the item
        if(name != null && !name.isEmpty()) {
          output.setStackDisplayName(name);
        }

        return output;
      }
    }

    return null;
  }

  /**
   * Adds the trait to the tag, taking max-count and already existing traits into account.
   *
   * @param rootCompound The root compound of the item
   * @param trait        The trait to add.
   * @param color        The color used on the tooltip. Will not be used if the trait already exists on the tool.
   */
  public static void addTrait(NBTTagCompound rootCompound, ITrait trait, EnumChatFormatting color) {
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
    int index = TinkerUtil.getIndexInList(tagList, trait.getIdentifier());
    if(index < 0) {
      traitModifier.updateNBTWithColor(tag, color);
      tagList.appendTag(tag);
      TagUtil.setModifiersTagList(rootCompound, tagList);
    }
    else {
      tag = tagList.getCompoundTagAt(index);
    }

    traitModifier.applyEffect(rootCompound, tag);
  }

  public static ItemStack tryRepairTool(ItemStack[] stacks, ItemStack toolStack, boolean removeItems) {
    if(toolStack == null || !(toolStack.getItem() instanceof IRepairable)) {
      return null;
    }

    // obtain a working copy of the items if the originals shouldn't be modified
    if(!removeItems) {
      stacks = Util.copyItemStackArray(stacks);
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
  public static ItemStack tryModifyTool(ItemStack[] input, ItemStack toolStack, boolean removeItems)
      throws TinkerGuiException {
    ItemStack copy = toolStack.copy();

    // obtain a working copy of the items if the originals shouldn't be modified
    ItemStack[] stacks = Util.copyItemStackArray(input);

    Set<IModifier> appliedModifiers = Sets.newHashSet();
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      RecipeMatch.Match match;
      do {
        match = modifier.matches(stacks);
        ItemStack backup = copy.copy();

        // found a modifier that is applicable. Try to apply the match
        while(match != null && match.amount > 0) {
          TinkerGuiException caughtException = null;
          boolean canApply = false;
          try {
            canApply = modifier.canApply(copy);
          } catch(TinkerGuiException e) {
            caughtException = e;
          }

          // but can it be applied?
          if(canApply) {
            modifier.apply(copy);

            RecipeMatch.removeMatch(stacks, match);

            appliedModifiers.add(modifier);
            match.amount--;
          }
          else {
            // materials would allow another application, but modifier doesn't
            // if we have already applied another modifier we cancel the whole thing to prevent situations where
            // only a part of the modifiers gets applied. either all or none.
            if(appliedModifiers.size() > 0 || !appliedModifiers.contains(modifier)) {
              // if we have a reason, rather tell the player that
              if(caughtException != null) {
                throw caughtException;
              }
              return null;
            }
            copy = backup;
            match = null;
            break;
          }
        }
      } while(match != null);
    }

    // check if all itemstacks were touched - otherwise there's an invalid item in the input
    for(int i = 0; i < input.length; i++) {
      if(input[i] != null && ItemStack.areItemStacksEqual(input[i], stacks[i])) {
        if(!appliedModifiers.isEmpty()) {
          String error =
              StatCollector.translateToLocalFormatted("gui.error.noModifierForItem", input[i].getDisplayName());
          throw new TinkerGuiException(error);
        }
        return null;
      }
    }

    // update output itemstacks
    if(removeItems) {
      for(int i = 0; i < input.length; i++) {
        // stacks might be null because stacksize got 0 during processing, we have to reflect that in the input
        // so the caller can identify that
        if(input[i] != null && stacks[i] == null) {
          input[i].stackSize = 0;
        }
        else {
          input[i] = stacks[i];
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

    return null;
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
  public static ItemStack tryReplaceToolParts(ItemStack toolStack, final ItemStack[] toolPartsIn, final boolean removeItems)
      throws TinkerGuiException {
    if(toolStack == null || !(toolStack.getItem() instanceof TinkersItem)) {
      return null;
    }

    // we never modify the original. Caller can remove all of them if we return a result
    final ItemStack[] toolParts = Util.copyItemStackArray(toolPartsIn);

    TIntIntMap assigned = new TIntIntHashMap();
    TinkersItem tool = (TinkersItem) toolStack.getItem();
    // materiallist has to be copied because it affects the actual NBT on the tool if it's changed
    final NBTTagList materialList = (NBTTagList) TagUtil.getBaseMaterialsTagList(toolStack).copy();

    // assing each toolpart to a slot in the tool
    for(int i = 0; i < toolParts.length; i++) {
      ItemStack part = toolParts[i];
      if(part == null) {
        continue;
      }
      if(!(part.getItem() instanceof IToolPart)) {
        // invalid item for toolpart replacement
        return null;
      }

      int candidate = -1;
      // find an applicable slot in the tool structure corresponding to the toolparts position
      for(int j = 0; j < tool.requiredComponents.length; j++) {
        PartMaterialType pmt = tool.requiredComponents[j];
        String partMat = ((IToolPart) part.getItem()).getMaterial(part).getIdentifier();
        String currentMat = materialList.getStringTagAt(j);
        // is valid and not the same material?
        if(pmt.isValid(part) && !partMat.equals(currentMat)) {
          // part not taken up by previous part already?
          if(!assigned.valueCollection().contains(j)) {
            candidate = j;
            // if a tool has multiple of the same parts we may want to replace another one as the currently selected
            // for that purpose we only allow to overwrite the current selection if the input slot is a later one than the current one
            if(i > j) {
              break;
            }
          }
        }
      }

      // no assignment found for a part. Invalid input.
      if(candidate < 0) {
        return null;
      }
      assigned.put(i, candidate);
    }

    // did we assign nothing?
    if(assigned.isEmpty()) {
      return null;
    }

    // We now know which parts to replace with which inputs. Yay. Now we only have to do so.
    // to do so we simply switch out the materials used and rebuild the tool
    assigned.forEachEntry(new TIntIntProcedure() {
      @Override
      public boolean execute(int i, int j) {
        String mat = ((IToolPart) toolParts[i].getItem()).getMaterial(toolParts[i]).getIdentifier();
        materialList.set(j, new NBTTagString(mat));
        if(removeItems) {
          toolPartsIn[i].stackSize--;
        }
        return true;
      }
    });

    ItemStack output = toolStack.copy();
    TagUtil.setBaseMaterialsTagList(output, materialList);
    NBTTagCompound tag = output.getTagCompound();
    rebuildTool(tag, (TinkersItem) output.getItem());
    output.setTagCompound(tag);

    // check if the output has enough durability. we only allow it if the result would not be broken
    if(output.getItemDamage() > output.getMaxDamage()) {
      String error = StatCollector.translateToLocalFormatted("gui.error.notEnoughDurability", output.getItemDamage() - output.getMaxDamage());
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
  public static ItemStack[] tryBuildToolPart(ItemStack pattern, ItemStack[] materialItems, boolean removeItems)
      throws TinkerGuiException {
    IToolPart part = Pattern.getPartFromTag(pattern);
    if(part == null || !(part instanceof MaterialItem)) {
      String error = StatCollector.translateToLocalFormatted("gui.error.invalidPattern");
      throw new TinkerGuiException(error);
    }

    if(!removeItems) {
      materialItems = Util.copyItemStackArray(materialItems);
    }

    // find the material from the input
    RecipeMatch.Match match = null;
    Material foundMaterial = null;
    for(Material material : TinkerRegistry.getAllMaterials()) {
      // craftable?
      if(!material.craftable) {
        continue;
      }
      RecipeMatch.Match newMatch = material.matches(materialItems, part.getCost());
      if(newMatch == null) {
        continue;
      }

      // we found a match, yay
      if(match == null) {
        match = newMatch;
        foundMaterial = material;
        // is it more complex than the old one?
      }
      else if(newMatch.stacks.size() > match.stacks.size()) {
        match = newMatch;
        foundMaterial = material;
      }
    }

    // nope, no material
    if(match == null) {
      return null;
    }

    ItemStack output = ((MaterialItem) part).getItemstackWithMaterial(foundMaterial);
    if(output == null) {
      return null;
    }

    RecipeMatch.removeMatch(materialItems, match);

    // check if we have secondary output
    ItemStack secondary = null;
    int leftover = (match.amount - part.getCost())/TinkerMaterials.VALUE_Shard;
    if(leftover > 0) {
      secondary = TinkerTools.shard.getItemstackWithMaterial(foundMaterial);
      secondary.stackSize = leftover;
    }

    // build an item with this
    return new ItemStack[]{output, secondary};
  }

  /**
   * Rebuilds a tool from its raw data, material info and applied modifiers
   *
   * @param rootNBT The root NBT tag compound of the tool to to rebuild. The NBT will be modified, overwriting old
   *                data.
   */
  public static void rebuildTool(NBTTagCompound rootNBT, TinkersItem tinkersItem) {
    boolean broken = TagUtil.getToolTag(rootNBT).getBoolean(Tags.BROKEN);
    // Recalculate tool base stats from material stats
    NBTTagList materialTag = TagUtil.getBaseMaterialsTagList(rootNBT);
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(materialTag);

    // ensure all needed Stats are present
    while(materials.size() < tinkersItem.requiredComponents.length) {
      materials.add(Material.UNKNOWN);
    }
    for(int i = 0; i < tinkersItem.requiredComponents.length; i++) {
      if(!tinkersItem.requiredComponents[i].isValidMaterial(materials.get(i))) {
        materials.set(i, Material.UNKNOWN);
      }
    }

    // the base stats of the tool
    NBTTagCompound toolTag = tinkersItem.buildTag(materials);
    TagUtil.setToolTag(rootNBT, toolTag);
    // and its copy for reference
    rootNBT.setTag(Tags.TOOL_DATA_ORIG, toolTag.copy());

    // clean up traits
    rootNBT.removeTag(Tags.TOOL_TRAITS);
    tinkersItem.addMaterialTraits(rootNBT, materials);

    // reapply modifiers
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(rootNBT);
    NBTTagList modifiersTag = TagUtil.getModifiersTagList(rootNBT);
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String identifier = modifiers.getStringTagAt(i);
      IModifier modifier = TinkerRegistry.getModifier(identifier);
      if(modifier == null) {
        log.debug("Missing modifier: {}", identifier);
        continue;
      }

      NBTTagCompound tag;
      int index = TinkerUtil.getIndexInList(modifiersTag, modifier.getIdentifier());

      if(index >= 0) {
        tag = modifiersTag.getCompoundTagAt(index);
      }
      else {
        tag = new NBTTagCompound();
      }

      modifier.applyEffect(rootNBT, tag);
    }

    // adjust free modifiers
    int freeModifiers = toolTag.getInteger(Tags.FREE_MODIFIERS);
    freeModifiers -= TagUtil.getBaseModifiersUsed(rootNBT);
    toolTag.setInteger(Tags.FREE_MODIFIERS, freeModifiers);

    // broken?
    if(broken) {
      toolTag.setBoolean(Tags.BROKEN, true);
    }
  }

  /**
   * A simple Tool consists of a head and an toolrod. Head determines primary stats, toolrod multiplies primary stats.
   */
  public static NBTTagCompound buildSimpleTool(Material headMaterial, Material handleMaterial,
                                               Material... accessoriesMaterials) {
    NBTTagCompound result;
    ToolMaterialStats headStats = headMaterial.getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats handleStats = handleMaterial.getStats(ToolMaterialStats.TYPE);

    // get the start values from the head
    result = calculateHeadParts(headStats);
    // add the accessories
    for(Material material : accessoriesMaterials) {
      ToolMaterialStats accessoryStats = material.getStats(ToolMaterialStats.TYPE);
      calculateAccessoryParts(result, accessoryStats);
    }

    // multiply with the handles
    calculateHandleParts(result, handleStats);

    // and don't forget the harvest level
    calculateHarvestLevel(result, headStats);

    result.setInteger(Tags.FREE_MODIFIERS, 3);

    return result;
  }

  /**
   * Takes an arbitrary amount of ToolMaterialStats and creates a tag with the mean of their stats.
   *
   * @return The resulting TagCompound
   */
  public static NBTTagCompound calculateHeadParts(ToolMaterialStats... stats) {
    int durability = 0;
    float attack = 0f;
    float speed = 0f;

    // sum up stats
    for(ToolMaterialStats stat : stats) {
      durability += stat.durability;
      attack += stat.attack;
      speed += stat.miningspeed;
    }

    // take mean
    durability /= stats.length;
    attack /= (float) stats.length;
    speed /= (float) stats.length;

    // create output tag
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger(Tags.DURABILITY, durability);
    tag.setFloat(Tags.ATTACK, attack);
    tag.setFloat(Tags.MININGSPEED, speed);

    return tag;
  }

  /**
   * Adds the durability of the given materials to the tag.
   */
  public static void calculateAccessoryParts(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int durability = baseTag.getInteger(Tags.DURABILITY);

    // sum up stats
    for(ToolMaterialStats stat : stats) {
      durability += stat.durability;
    }

    // set value
    baseTag.setInteger(Tags.DURABILITY, durability);
  }

  /**
   * Takes an arbitrary amount of ToolMaterialStats and multiplies the durability in the basetag with the average
   */
  public static void calculateHandleParts(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int count = 0;
    float multiplier = 0;

    // sum up stats
    for(ToolMaterialStats stat : stats) {
      multiplier += stat.handleQuality;
      count++;
    }

    if(count > 0) {
      // calculate the multiplier from the summed up stats
      multiplier *= (0.5 + count * 0.5);
      multiplier /= count;

      int durability = baseTag.getInteger(Tags.DURABILITY);
      durability *= multiplier;
      baseTag.setInteger(Tags.DURABILITY, durability);
    }
  }

  /**
   * Choses the highest harvestlevel of the given materials and sets it in the tag.
   */
  public static void calculateHarvestLevel(NBTTagCompound baseTag, ToolMaterialStats... stats) {
    int harvestLevel = 0;

    // get max
    for(ToolMaterialStats stat : stats) {
      if(stat.harvestLevel > harvestLevel) {
        harvestLevel = stat.harvestLevel;
      }
    }

    baseTag.setInteger(Tags.HARVESTLEVEL, harvestLevel);
  }
}
