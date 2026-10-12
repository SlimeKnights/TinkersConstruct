package slimeknights.tconstruct.smeltery.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import org.joml.Vector3f;
import slimeknights.mantle.client.render.FaucetFluid;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.tconstruct.TConstruct;

import java.util.List;
import java.util.stream.Collectors;

/** Extension of {@link FaucetFluid.Loader} for rendering reverse gravity fluids traveling up the block instead of down. */
public class ReverseFaucetFluidLoader extends FaucetFluid.Loader {
  public ReverseFaucetFluidLoader() {
    super("Reverse Faucet Fluid", "tinkering/reverse_faucet_fluid", TConstruct.getResource("_default"));
  }

  public static ReverseFaucetFluidLoader REGISTRY = new ReverseFaucetFluidLoader();

  public static void initialize(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(REGISTRY);
  }

  /** Overridable method controlling how the default instance is mapped using the bottom field. */
  @Override
  public FaucetFluid fromJson(JsonObject json) {
    FaucetFluid defaultInstance = getDefaultInstance();
    List<FluidCuboid> side = parseFluids(json, "side", defaultInstance.side());
    List<FluidCuboid> center = parseFluids(json, "center", defaultInstance.center());
    boolean cont = GsonHelper.getAsBoolean(json, "continue", false);
    return new FaucetFluid(side, center, cont);
  }

  /**
   * Parses the fluids for the given side, defaulting as relevant
   * @param json Json object to parse
   * @param tag  Tag name to parse
   * @param def  Default list for this region
   * @return List of fluid cuboids
   */
  private static List<FluidCuboid> parseFluids(JsonObject json, String tag, List<FluidCuboid> def) {
    JsonElement element;
    if (json.has(tag)) {
      element = json.get(tag);
      // top is a keyword that can sub for primitive for both types
    } else if (json.has("top") && json.get("top").isJsonPrimitive()) {
      element = json.get("top");
    } else {
      return def;
    }
    // primitives set the default to the given value
    if (element.isJsonPrimitive()) {
      int value = element.getAsInt();
      return def.stream().map(cuboid -> {
        Vector3f to = new Vector3f(cuboid.getTo());
        to.y = value;
        return cuboid.withTo(to);
      }).collect(Collectors.toList());
    } else {
      return FluidCuboid.LIST_LOADABLE.getIfPresent(json, tag);
    }
  }


  public static void renderReverseFaucetFluids(LevelAccessor world, BlockPos pos, Direction direction, PoseStack matrices, VertexConsumer buffer, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light) {
    int i = 0;
    FaucetFluid faucetFluid;
    do {
      // get the faucet data for the block
      i++;
      faucetFluid = ReverseFaucetFluidLoader.REGISTRY.get(world.getBlockState(pos.above(i)));
      // render all down cubes with the given offset
      matrices.pushPose();
      matrices.translate(0, i, 0);
      for (FluidCuboid cube : faucetFluid.getFluids(direction)) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, true);
      }
      matrices.popPose();
    } while (faucetFluid.isContinued());
  }
}
