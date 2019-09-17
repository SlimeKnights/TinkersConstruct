package slimeknights.tconstruct.common.conditions;

public class RecipeConditions {

  /*public static final IConditionSerializer CONDITION_PULSE_LOADED = CraftingHelper.register(new ResourceLocation("tconstruct", "pulse_loaded"), json -> {
    String pulseName = JSONUtils.getString(json, "pulse_name");

    Boolean inverted = JSONUtils.getBoolean(json, "invert", false);

    return () -> inverted ? !TConstruct.pulseManager.isPulseLoaded(pulseName) : TConstruct.pulseManager.isPulseLoaded(pulseName);
  });

  public static final IConditionSerializer CONDITION_CONFIG_OPTION_ENABLED = CraftingHelper.register(new ResourceLocation("tconstruct", "config_option_enabled"), json -> {
    String configSetting = JSONUtils.getString(json, "config_setting", "");

    switch (configSetting) {
      case "registerAllItems":
        return () -> Config.forceRegisterAll;
      case "addFlintRecipe":
        return () -> Config.gravelFlintRecipe;
      case "matchVanillaSlimeblock":
        return () -> Config.matchVanillaSlimeblock;
      default:
        throw new RuntimeException(String.format("Invalid config setting: %s", configSetting));
    }
  });*/
}
