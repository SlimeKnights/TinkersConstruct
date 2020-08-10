package slimeknights.tconstruct.smeltery.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Log4j2
public class FaucetFluidLoader extends JsonReloadListener {
  /** GSON instance for this */
  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();

  /** Singleton instance */
  public static final FaucetFluidLoader INSTANCE = new FaucetFluidLoader();

  /** Name of the default fluid model */
  private static final ResourceLocation DEFAULT_NAME = Util.getResource("_default");

  /** Map of fluids */
  private final Map<Block,FaucetFluid> fluidMap = new HashMap<>();

  private FaucetFluid defaultFluid;
  private FaucetFluidLoader() {
    super(GSON, "models/faucet_fluid");
    defaultFluid = FaucetFluid.EMPTY;
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> map, IResourceManager resourceManager, IProfiler profiler) {
    for (Entry<ResourceLocation,JsonElement> entry : map.entrySet()) {
      if (!entry.getValue().isJsonObject()) {
        continue;
      }
      ResourceLocation location = entry.getKey();
      try {
        JsonObject json = entry.getValue().getAsJsonObject();

        // special case: default for blocks missing values
        if(location.equals(DEFAULT_NAME)) {
          defaultFluid = FaucetFluid.fromJson(json);
          continue;
        }

        // all others are block
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if(block != null && block != Blocks.AIR) {
          // TODO: variant loader?
          fluidMap.put(block, FaucetFluid.fromJson(json));
        } else {
          log.debug("Skipping loading faucet fluid model '{}' as no coorsponding block exists", location);
        }
      } catch (Exception e) {
        log.warn("Exception loading faucet fluid model '{}': {}", location, e.getMessage());
      }
    }
  }

  /**
   * Gets faucet fluid data for the given block
   * @param state  Block state
   * @return  Faucet fluid data
   */
  public static FaucetFluid get(BlockState state) {
    return INSTANCE.fluidMap.getOrDefault(state.getBlock(), INSTANCE.defaultFluid);
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class FaucetFluid {
    private static final FaucetFluid EMPTY = new FaucetFluid(Collections.emptyList(), Collections.emptyList());
    private final List<FluidCuboid> side;
    private final List<FluidCuboid> center;

    /**
     * Gets the list of fluids for the given direction
     * @param dir  Direction
     * @return  List of fluids to render
     */
    public List<FluidCuboid> getFluids(Direction dir) {
      if (dir.getAxis() == Axis.Y) {
        return center;
      }
      return side;
    }

    /**
     * Creates a new fluid from JSON
     * @param json  Fluid to create
     * @return  New fluid
     */
    private static FaucetFluid fromJson(JsonObject json) {
      List<FluidCuboid> side = FluidCuboid.listFromJson(json, "side");
      List<FluidCuboid> center = FluidCuboid.listFromJson(json, "center");
      return new FaucetFluid(side, center);
    }
  }
}
