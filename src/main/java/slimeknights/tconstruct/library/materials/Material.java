package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.PartType;
import slimeknights.tconstruct.library.traits.ITrait;

public class Material extends RecipeMatchRegistry implements IMaterial {


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
  private final String identifier;

  /**
   * The fluid associated with this material, can be null.
   * If non-null also indicates that the material can be cast.
   */
  @Nullable
  protected Fluid fluid;

  /**
   * Material can be crafted into parts in the PartBuilder
   */
  private boolean craftable;

  /**
   * This item will be used instead of the generic shard item when returning leftovers.
   */
  private ItemStack shardItem = ItemStack.EMPTY;

  // we use a specific map for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * the linked map to ensure the order when iterating
  private final LinkedHashMap<PartType, IMaterialStats> stats = new LinkedHashMap<>();
  /**
   * Stat-ID -> Traits used in conjunction with these stats.
   * <em>null</em> is an allowed key, and is used for general traits that are not stats specific.
   */
  private final LinkedHashMap<PartType, List<ITrait>> traitsByStats = new LinkedHashMap<>();

  public Material(String identifier) {
    // lowercases and removes whitespaces
    this.identifier = Util.sanitizeLocalizationString(identifier);
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Setting this to true allows to craft parts in the PartBuilder
   */
  public Material setCraftable(boolean craftable) {
    this.craftable = craftable;
    return this;
  }

  @Override
  public boolean isCraftable() {
    return this.craftable;
  }

  @Override
  public Optional<Fluid> getFluid() {
    return Optional.ofNullable(fluid);
  }

  /**
   * Associates this fluid with the material. Used for melting/casting items.
   */
  public Material setFluid(@Nullable Fluid fluid) {
    if(fluid != null && !ForgeRegistries.FLUIDS.containsValue(fluid)) {
      TinkerRegistry.log.warn("Materials cannot have an unregistered fluid associated with them!");
    }
    this.fluid = fluid;
    return this;
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
  public <T extends IMaterialStats> Optional<T> getStatsForType(PartType partType) {
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
  public Material addTrait(ITrait materialTrait, PartType forStatsType) {
    // todo: we don't register traits automatically on addition anymore, check if all are present
    getStatTraits(forStatsType).add(materialTrait);
    return this;
  }

  @Override
  public List<ITrait> getAllTraitsForStats(PartType partType) {
    List<ITrait> traits = traitsByStats.get(partType);

    if(traits == null) {
      return traitsByStats.getOrDefault(null, ImmutableList.of());
    }
    else {
      return traits;
    }
  }

  /**
   * Obtains the list of traits for the given stat, creates it if it doesn't exist yet.
   */
  protected List<ITrait> getStatTraits(PartType forStatsType) {
    if(!this.traitsByStats.containsKey(forStatsType)) {
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
    if(stack.isEmpty()) {
      this.shardItem = ItemStack.EMPTY;
    }
    else {
      Optional<RecipeMatch.Match> matchOptional = matches(stack);
      if(matchOptional.isPresent()) {
        RecipeMatch.Match match = matchOptional.get();
        if(match.amount == MaterialValues.VALUE_Shard) {
          this.shardItem = stack;
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
      }
    }
  }

  @Override
  public ItemStack getShard() {
    if(shardItem != ItemStack.EMPTY) {
      return shardItem.copy();
    }
    return ItemStack.EMPTY;
  }

}
