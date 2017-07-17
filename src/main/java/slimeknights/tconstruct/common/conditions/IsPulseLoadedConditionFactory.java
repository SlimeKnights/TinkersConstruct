package slimeknights.tconstruct.common.conditions;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import slimeknights.tconstruct.TConstruct;

public class IsPulseLoadedConditionFactory implements IConditionFactory {
  @Override
  public BooleanSupplier parse(JsonContext context, JsonObject json) {
    String pulseName = JsonUtils.getString(json, "pulse_name");

    Boolean invert = JsonUtils.getBoolean(json, "invert", false);

    return () -> invert ? !TConstruct.pulseManager.isPulseLoaded(pulseName) : TConstruct.pulseManager.isPulseLoaded(pulseName);
  }
}
