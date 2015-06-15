package tconstruct.library.utils;

import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.materials.Material;
import tconstruct.library.materials.ToolMaterialStats;
import tconstruct.library.modifiers.IModifier;
import tconstruct.library.modifiers.ModifyException;
import tconstruct.library.modifiers.RecipeMatch;
import tconstruct.library.modifiers.TraitModifier;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.traits.ITrait;

public final class ToolBuilder {

  private static Logger log = Util.getLogger("ToolBuilder");

  private ToolBuilder() {
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

    if(modifier == null || !(modifier instanceof TraitModifier)) {
      log.error("addTrait: No matching modifier for the Trait {} present", trait.getIdentifier());
      return;
    }

    TraitModifier traitModifier = (TraitModifier) modifier;

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

  public static ItemStack tryModifyTool(ItemStack[] stacks, ItemStack toolStack, boolean removeItems)
      throws ModifyException {
    ItemStack copy = toolStack.copy();

    // obtain a working copy of the items if the originals shouldn't be modified
    if(!removeItems) {
      ItemStack[] stacksCopy = new ItemStack[stacks.length];
      for(int i = 0; i < stacks.length; i++) {
        if(stacks[i] != null) {
          stacksCopy[i] = stacks[i].copy();
        }
      }

      stacks = stacksCopy;
    }

    Set<IModifier> appliedModifiers = Sets.newHashSet();
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      RecipeMatch.Match match;
      do {
        match = modifier.matches(stacks);
        ItemStack backup = copy.copy();

        // found a modifier that is applicable. Try to apply the match
        while(match != null && match.amount > 0) {
          ModifyException caughtException = null;
          boolean canApply = false;
          try {
            canApply = modifier.canApply(copy);
          } catch(ModifyException e) {
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
   * Rebuilds a tool from its raw data, material info and applied modifiers
   *
   * @param rootNBT The root NBT tag compound of the tool to to rebuild. The NBT will be modified, overwriting old
   *                data.
   */
  public static void rebuildTool(NBTTagCompound rootNBT, TinkersItem tinkersItem) {
    // Recalculate tool base stats from material stats
    NBTTagList materialTag = TagUtil.getBaseMaterialsTagList(rootNBT);
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(materialTag);

    NBTTagCompound toolTag = tinkersItem.buildTag(materials);
    TagUtil.setToolTag(rootNBT, toolTag);

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
      multiplier += stat.durabilityModifier;
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
