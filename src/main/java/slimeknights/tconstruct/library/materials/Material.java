package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.*;
import java.util.stream.Collectors;

public class Material implements IMaterial {

  // todo config
//  public static int VALUE_Ore() {
//    return (int) (VALUE_Ingot * Config.oreToIngotRatio);
//  }
  // todo default stats. figure out how to hadnle them. maybe have to register stat types and handle it there?
/*
  static {
    UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
    UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
    UNKNOWN.addStats(new ExtraMaterialStats(0));
    UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
    UNKNOWN.addStats(new BowStringMaterialStats(1f));
    UNKNOWN.addStats(new ArrowShaftMaterialStats(1f, 0));
    UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
    UNKNOWN.addStats(new ProjectileMaterialStats());
  }*/

  /**
   * This String uniquely identifies a material.
   */
  private final ResourceLocation identifier;

  /**
   * The fluid associated with this material, can not be null, but Fluids.EMPTY.
   * If non-null also indicates that the material can be cast.
   */
  protected final Fluid fluid;

  /**
   * Material can be crafted into parts in the PartBuilder
   */
  private final boolean craftable;

  /**
   * This item will be used instead of the generic shard item when returning leftovers.
   */
  private final ItemStack shardItem;

  // we use a specific map for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * the linked map to ensure the order when iterating
  private final LinkedHashMap<ResourceLocation, IMaterialStats> stats = new LinkedHashMap<ResourceLocation, IMaterialStats>();
  /**
   * Stat-ID -> Traits used in conjunction with these stats.
   * <em>null</em> is an allowed key, and is used for general traits that are not stats specific.
   */
  private final LinkedHashMap<ResourceLocation, List<ITrait>> traitsByStats = new LinkedHashMap<ResourceLocation, List<ITrait>>();

  public Material(ResourceLocation identifier, Fluid fluid, boolean craftable, ItemStack shardItem) {
    // lowercases and removes whitespaces
    this.identifier = new ResourceLocation(identifier.getNamespace(), Util.sanitizeLocalizationString(identifier.getPath()));
    this.fluid = fluid;
    this.craftable = craftable;
    this.shardItem = shardItem;
  }

  @Override
  public ResourceLocation getIdentifier() {
    return identifier;
  }

  @Override
  public boolean isCraftable() {
    return this.craftable;
  }

  @Override
  public Fluid getFluid() {
    return fluid;
  }

  /* Stats */

  /**
   * Do not use this function directly stats. Use TinkerRegistry.addMaterialStats instead.
   */
  public Material addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getIdentifier(), materialStats);
    return this;
  }

  /**
   * Returns the material stats of the given type of this material.
   *
   * @param partType Identifier of the material.
   * @param <T>      Type of the Stats are determined by return value. Use the correct
   * @return The stats found or null if none present.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> Optional<T> getStatsForType(MaterialStatType partType) {
    return Optional.ofNullable((T) stats.get(partType));
  }

  @Override
  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  /* Traits */

  /**
   * Adds the trait as the default trait, will be used if no more specific one is present time.
   */
  public Material addTrait(ITrait materialTrait) {
    return addTrait(materialTrait, null);
  }

  /**
   * Adds the trait to be added if the specified stats are used.
   */
  public Material addTrait(ITrait materialTrait, ResourceLocation forStatsType) {
    // todo: we don't register traits automatically on addition anymore, check if all are present
    getStatTraits(forStatsType).add(materialTrait);
    return this;
  }

  @Override
  public List<ITrait> getAllTraitsForStats(MaterialStatType partType) {
    List<ITrait> traits = traitsByStats.get(partType);

    if (traits == null) {
      return traitsByStats.getOrDefault(null, ImmutableList.of());
    } else {
      return traits;
    }
  }

  /**
   * Obtains the list of traits for the given stat, creates it if it doesn't exist yet.
   */
  @SuppressWarnings("WeakerAccess")
  protected List<ITrait> getStatTraits(ResourceLocation forStatsType) {
    if (!this.traitsByStats.containsKey(forStatsType)) {
      // linked list since we're only ever iterating over the list
      this.traitsByStats.put(forStatsType, new LinkedList<>());
    }
    return this.traitsByStats.get(forStatsType);
  }

  @Override
  public Collection<ITrait> getAllTraits() {
    return traitsByStats.values().stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  public void setShard(ItemStack stack) {
    if (stack.isEmpty()) {
      //this.shardItem = ItemStack.EMPTY;
    } else {
      // todo: shard matching. Reimplement or move it somewhere else? OK like this? Do we keep recipematchregistry?
      /*
      Optional<RecipeMatch.Match> matchOptional = matches(stack);
      if(matchOptional.isPresent()) {
        RecipeMatch.Match match = matchOptional.get();
        if(match.amount == MaterialValues.VALUE_Shard) {*/
      //this.shardItem = stack;
      /*
        }
        else {
          TinkerRegistry.log.warn("Itemstack {} cannot be shard of material {} since it does not have the correct material value! (is {}, has to be {})",
                                  stack.toString(),
                                  identifier,
                                  match.amount,
                                  MaterialValues.VALUE_Shard);
        }
      }
      else {
        TinkerRegistry.log.warn("Itemstack {} cannot be shard of material {} since it is not associated with the material!",
                                stack.toString(),
                                identifier);
      }*/
    }
  }

  @Override
  public ItemStack getShard() {
    if (shardItem != ItemStack.EMPTY) {
      return shardItem.copy();
    }
    return ItemStack.EMPTY;
  }

}
