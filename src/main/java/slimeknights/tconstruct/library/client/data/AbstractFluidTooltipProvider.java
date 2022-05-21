package slimeknights.tconstruct.library.client.data;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler.FluidUnit;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler.FluidUnitList;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Provider for fluid tooltip information */
public abstract class AbstractFluidTooltipProvider extends GenericDataProvider {
  private final Map<ResourceLocation,ResourceLocation> redirects = new HashMap<>();;
  private final Map<ResourceLocation,FluidUnitListBuilder> builders = new HashMap<>();
  private final String modId;

  public AbstractFluidTooltipProvider(DataGenerator generator, String modId) {
    super(generator, PackType.CLIENT_RESOURCES, FluidTooltipHandler.FOLDER, FluidTooltipHandler.GSON);
    this.modId = modId;
  }

  /** Adds all relevant fluids to the maps */
  protected abstract void addFluids();

  @Override
  public final void run(HashCache cache) throws IOException {
    addFluids();
    builders.forEach((key, builder) -> saveThing(cache, key, builder.build()));
    redirects.forEach((key, target) -> saveThing(cache, key, JsonUtils.withLocation("redirect", target)));
  }


  /* Helpers */

  /** Creates a ResourceLocation for the local mod */
  protected ResourceLocation id(String name) {
    return new ResourceLocation(modId, name);
  }

  /** Adds a fluid to the builder */
  protected FluidUnitListBuilder add(ResourceLocation id, @Nullable TagKey<Fluid> tag) {
    if (redirects.containsKey(id)) {
      throw new IllegalArgumentException(id + " is already registered as a redirect");
    }
    FluidUnitListBuilder newBuilder = new FluidUnitListBuilder(tag);
    FluidUnitListBuilder original = builders.put(id, newBuilder);
    if (original != null) {
      throw new IllegalArgumentException(id + " is already registered");
    }
    return newBuilder;
  }

  /** Adds a fluid to the builder */
  protected FluidUnitListBuilder add(String id, TagKey<Fluid> tag) {
    return add(id(id), tag);
  }

  /** Adds a fluid to the builder using the tag name as the ID */
  protected FluidUnitListBuilder add(TagKey<Fluid> tag) {
    return add(id(tag.location().getPath()), tag);
  }

  /** Adds a fluid to the builder with no tag */
  protected FluidUnitListBuilder add(ResourceLocation id) {
    return add(id, null);
  }

  /** Adds a fluid to the builder with no tag */
  protected FluidUnitListBuilder add(String id) {
    return add(id(id), null);
  }

  /** Adds a redirect from a named builder to a target */
  protected void addRedirect(ResourceLocation id, ResourceLocation target) {
    if (builders.containsKey(id)) {
      throw new IllegalArgumentException(id + " is already registered as a unit list");
    }
    ResourceLocation original = redirects.put(id, target);
    if (original != null) {
      throw new IllegalArgumentException(id + " is already redirecting to " + original);
    }
  }

  /** Builder for a unit list */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  protected class FluidUnitListBuilder {
    @Nullable
    private final TagKey<Fluid> tag;
    private final ImmutableList.Builder<FluidUnit> units = ImmutableList.builder();

    /** Adds a unit with a full translation key */
    public FluidUnitListBuilder addUnitRaw(String key, int amount) {
      units.add(new FluidUnit(key, amount));
      return this;
    }

    /** Adds a unit local to the current mod */
    public FluidUnitListBuilder addTinkerUnit(String key, int amount) {
      return addUnitRaw(TConstruct.makeTranslationKey("gui", "fluid." + key), amount);
    }

    /** Adds a unit local to the current mod */
    public FluidUnitListBuilder addUnit(String key, int amount) {
      return addUnitRaw(Util.makeTranslationKey("gui", id("fluid." + key)), amount);
    }

    /** Builds the final instance */
    private FluidUnitList build() {
      return new FluidUnitList(tag, units.build());
    }
  }
}
