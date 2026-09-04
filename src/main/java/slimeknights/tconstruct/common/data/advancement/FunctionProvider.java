package slimeknights.tconstruct.common.data.advancement;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.data.GenericStringProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Provides functions for advancements */
public class FunctionProvider extends GenericStringProvider {
  private final Map<ResourceLocation,String> functions = new HashMap<>();

  public FunctionProvider(PackOutput output) {
    super(output, Target.DATA_PACK, "functions", "mcfunction");
  }

  /** Generates all functions */
  protected void addAdvancements() {
    grant(AdvancementIds.STORY_ROOT);
    grant(AdvancementIds.STONE_PICK);
    grant(AdvancementIds.IRON_PICK);
    grant(AdvancementIds.NETHERITE_HOE);
    grant(AdvancementIds.WALK_ON_POWDER_SNOW);
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      String name = type.getName();
      grant(AdvancementIds.OBTAIN_ARMOR, "iron_" + name);
      grant(AdvancementIds.SHINY_GEAR, "diamond_" + name);
    }
    // netherite wants the whole set in your inventory at once
    grant(AdvancementIds.NETHERITE_ARMOR);
  }

  @Override
  public final CompletableFuture<?> run(CachedOutput cache) {
    addAdvancements();
    return GenericDataProvider.allOf(functions.entrySet().stream().map(entry -> saveString(cache, entry.getKey(), entry.getValue())));
  }

  /** Grants the given advancement */
  private void grant(ResourceLocation advancement) {
    functions.put(AdvancementIds.function(advancement), "advancement grant @s only " + advancement + '\n');
  }

  /** Grants the given advancement criteria */
  private void grant(ResourceLocation advancement, String criteria) {
    functions.put(AdvancementIds.function(advancement, criteria), "advancement grant @s only " + advancement + ' ' + criteria + '\n');
  }

  @Override
  public String getName() {
    return "Tinkers' Construct advancement mcfunction provider.";
  }
}
