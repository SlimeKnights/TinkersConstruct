package slimeknights.tconstruct.library.json.variable.mining;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Gets the targeted block light level. Will use the targeted position if possible, otherwise the players position
 * @param lightLayer   Block light layer to use
 * @param fallback     Fallback value if missing event and player
 */
public record BlockLightVariable(LightLayer lightLayer, float fallback) implements MiningSpeedVariable {
  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (player != null) {
      // use block position if possible player position otherwise
      return player.level.getBrightness(lightLayer, event != null && sideHit != null ? event.getPos().relative(sideHit) : player.blockPosition());
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<BlockLightVariable> LOADER = new IGenericLoader<>() {
    @Override
    public BlockLightVariable deserialize(JsonObject json) {
      return new BlockLightVariable(JsonHelper.getAsEnum(json, "light_layer", LightLayer.class), GsonHelper.getAsFloat(json, "fallback"));
    }

    @Override
    public void serialize(BlockLightVariable object, JsonObject json) {
      json.addProperty("light_layer", object.lightLayer.name().toLowerCase(Locale.ROOT));
      json.addProperty("fallback", object.fallback);
    }

    @Override
    public BlockLightVariable fromNetwork(FriendlyByteBuf buffer) {
      return new BlockLightVariable(buffer.readEnum(LightLayer.class), buffer.readFloat());
    }

    @Override
    public void toNetwork(BlockLightVariable object, FriendlyByteBuf buffer) {
      buffer.writeEnum(object.lightLayer);
      buffer.writeFloat(object.fallback);
    }
  };
}
