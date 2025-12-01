package slimeknights.tconstruct.library.data.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.registration.object.FluidObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Helper to generate tags for cost variants of items, typically used for melting */
@SuppressWarnings("removal")
@RequiredArgsConstructor
@CanIgnoreReturnValue
public class CostTagAppender {
  private final String metal;
  private final ResourceLocation prefix;
  private final String suffix;
  private final Function<ResourceLocation,IntrinsicTagAppender<Item>> tag;
  private final Map<Integer, IntrinsicTagAppender<Item>> tags = new HashMap<>();

  /** Creates a builder for a molten gear */
  public static CostTagAppender moltenToolMelting(FluidObject<?> fluid, Function<ResourceLocation,IntrinsicTagAppender<Item>> tag) {
    ResourceLocation id = fluid.getId();
    String metal = id.getPath().substring("molten_".length());
    return moltenToolMelting(id.getNamespace(), metal, tag);
  }

  /** Creates a builder for a molten gear */
  public static CostTagAppender moltenToolMelting(String domain, String metal, Function<ResourceLocation,IntrinsicTagAppender<Item>> tag) {
    return new CostTagAppender(metal, new ResourceLocation(domain, "melting/" + metal + "/tools_costing_"), "", tag);
  }

  /** Creates a tag for the given cost */
  @CheckReturnValue
  public IntrinsicTagAppender<Item> tag(int cost) {
    IntrinsicTagAppender<Item> appender = tags.get(cost);
    if (appender == null) {
      appender = this.tag.apply(prefix.withSuffix(cost + suffix));
      this.tags.put(cost, appender);
    }
    return appender;
  }

  /** Adds the passed items to the tag. */
  public CostTagAppender add(int cost, Item... items) {
    tag(cost).add(items);
    return this;
  }

  /** Adds the passed items to the tag. */
  public CostTagAppender add(int cost, boolean optional, ResourceLocation prefix, String... suffixes) {
    IntrinsicTagAppender<Item> tag = tag(cost);
    if (optional) {
      if (suffixes.length == 0) {
        tag.addOptional(prefix);
      } else for (String path : suffixes) {
        tag.addOptional(prefix.withSuffix('_' + path));
      }
    } else {
      if (suffixes.length == 0) {
        tag.add(ResourceKey.create(Registries.ITEM, prefix));
      } else for (String path : suffixes) {
        tag.add(ResourceKey.create(Registries.ITEM, prefix.withSuffix('_' + path)));
      }
    }
    return this;
  }

  /** Adds the passed items by ID with the metal as the prefix */
  public CostTagAppender optionalMetal(int cost, String domain, String... suffixes) {
    return add(cost, true, new ResourceLocation(domain, metal), suffixes);
  }

  /** Adds the given optional tag to the builder with the given prefix using our metal */
  public CostTagAppender metalTag(int cost, String prefix, String... names) {
    IntrinsicTagAppender<Item> tag = tag(cost);
    for (String name : names) {
      tag.addOptionalTag(new ResourceLocation(Mantle.COMMON, prefix + name + '/' + metal));
    }
    return this;
  }

  /** Adds the given optional tool tag to the builder with the given prefix using our metal */
  public CostTagAppender toolTag(int cost, String... names) {
    return metalTag(cost, "tools/", names);
  }

  /** Adds the given optional armor tag to the builder with the given prefix using our metal */
  public CostTagAppender armorTag(int cost, String... names) {
    return metalTag(cost, "armors/", names);
  }


  /* Vanilla tools */

  /** Adds common gear items from tags */
  @Internal // we cover everything from the base game that makes sense to melt, you probably want something more specialized if you wish to do stone/wood
  public CostTagAppender minecraft(String metal) {
    ResourceLocation prefix = new ResourceLocation(metal);
    // we use costs 1 and 3 for compat with tools complement
    // cost 2 isn't needed for any compat for vanilla, but we wish for a combined tag for non-vanilla so we do it for simplicity
    add(1, false, prefix, "shovel");
    add(2, false, prefix, "sword", "hoe");
    add(3, false, prefix, "pickaxe", "axe");
    // need cost 7 for paxels, rest of armor we do directly
    add(7, false, prefix, "leggings");
    toolTag(7, "paxels");
    // tools complement method is set to create the shovel tag, don't need it without them
    optionalMetal(1, "tools_complement", "knife");
    optionalMetal(3, "tools_complement", "sickle");
    return this;
  }

  /** Adds common gear items from tags */
  @Internal // we cover everything from the base game that makes sense to melt, you probably want something more specialized if you wish to do stone/wood
  public CostTagAppender minecraft() {
    return minecraft(metal);
  }


  /* Modded tools */

  /** Adds common tool items from tags */
  public CostTagAppender toolTags() {
    toolTag(2, "swords", "hoes");
    toolTag(3, "pickaxes", "axes");
    return this;
  }

  /** Adds tools complement tool items to the standard tags */
  @Internal // we already cover all metals tools complement does, no need for addons to use
  public CostTagAppender toolsComplement() {
    // only need the 1 tag if tools complement is present
    toolTag(1, "shovels");
    // tools complement exclusive tools
    optionalMetal(1, "tools_complement", "knife");
    optionalMetal(3, "tools_complement", "sickle");
    return this;
  }

  /** Creates a tag for the paxel and leggings. We currently have no paxels withot leggings, and leggings can be used directly without paxels */
  public CostTagAppender leggingsPaxel() {
    return armorTag(7, "leggings").toolTag(7, "paxels");
  }

  /** Adds the farmers delight knife to the tag. */
  public CostTagAppender fdKnife() {
    return optionalMetal(1, "farmersdelight", "knife");
  }

  /** Adds a railcraft crowbar */
  @Internal // there are only 3 of them, not needed in addons
  public CostTagAppender crowbar() {
    return optionalMetal(3, "railcraft", "crowbar");
  }

  /** Adds a tools complement excavator and a railcraft spike maul */
  @Internal // there are only 2 of them, not needed in addons
  public CostTagAppender excavatorSpikeMaul() {
    return optionalMetal(11, "tools_complement", "excavator").optionalMetal(11, "railcraft", "spike_maul");
  }
}
