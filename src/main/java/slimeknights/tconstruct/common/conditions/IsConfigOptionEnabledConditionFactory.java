package slimeknights.tconstruct.common.conditions;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import slimeknights.tconstruct.common.config.Config;

public class IsConfigOptionEnabledConditionFactory implements IConditionFactory {
  @Override
  public BooleanSupplier parse(JsonContext context, JsonObject json) {
    String configSetting = JsonUtils.getString(json, "config_setting", "");

    switch(configSetting) {
      case "registerAllItems":
        return () -> Config.forceRegisterAll;
      case "addFlintRecipe":
        return () -> Config.gravelFlintRecipe;
      default:
        throw new RuntimeException(String.format("Invalid config setting: %s", configSetting));
    }
  }
}
