package slimeknights.tconstruct.library.modifiers.modules.display;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Simple module to change the color of the durability bar.
 * If you have a usecase of something more complex in JSON, feel free to request it, but for now just programming what we use.
 */
public record DurabilityBarColorModule(int color) implements DurabilityDisplayModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.DURABILITY_DISPLAY);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
  @Nullable
  @Override
  public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
    return null; // null means no change
  }

  @Override
  public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
    return 0; // 0 means no change
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
    return color;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<DurabilityBarColorModule> LOADER = new IGenericLoader<>() {
    @Override
    public DurabilityBarColorModule deserialize(JsonObject json) {
      return new DurabilityBarColorModule(JsonUtils.parseColor(GsonHelper.getAsString(json, "color")));
    }

    @Override
    public void serialize(DurabilityBarColorModule object, JsonObject json) {
      json.addProperty("color", JsonUtils.colorToString(object.color));
    }

    @Override
    public DurabilityBarColorModule fromNetwork(FriendlyByteBuf buffer) {
      return new DurabilityBarColorModule(buffer.readInt());
    }

    @Override
    public void toNetwork(DurabilityBarColorModule object, FriendlyByteBuf buffer) {
      buffer.writeInt(object.color);
    }
  };
}
