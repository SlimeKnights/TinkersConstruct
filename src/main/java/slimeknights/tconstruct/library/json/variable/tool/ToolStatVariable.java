package slimeknights.tconstruct.library.json.variable.tool;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/**
 * Variable to get a stat from the tool
 */
public record ToolStatVariable(INumericToolStat<?> stat) implements ToolVariable {
  @Override
  public float getValue(IToolStackView tool) {
    return tool.getStats().get(stat).floatValue();
  }

  @Override
  public IGenericLoader<? extends ToolVariable> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ToolStatVariable> LOADER = new IGenericLoader<>() {
    @Override
    public ToolStatVariable deserialize(JsonObject json) {
      return new ToolStatVariable(ToolStats.numericFromJson(GsonHelper.getAsString(json, "stat")));
    }

    @Override
    public void serialize(ToolStatVariable object, JsonObject json) {
      json.addProperty("stat", object.stat.getName().toString());
    }

    @Override
    public ToolStatVariable fromNetwork(FriendlyByteBuf buffer) {
      return new ToolStatVariable(ToolStats.numericFromNetwork(buffer));
    }

    @Override
    public void toNetwork(ToolStatVariable object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.stat.getName().toString());
    }
  };
}
