package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * TODO: replacement for forges fluid util
 */
public class FluidUtil {

  public static FluidVolume fromJson(JsonObject json) throws JsonSyntaxException {
    final FluidKey fluid = FluidKeys.get(Registry.FLUID.get(Identifier.tryParse(json.get("fluid").getAsString())));
    return fluid.withAmount(fluid == FluidKeys.EMPTY ? FluidAmount.ZERO : FluidAmount.of(json.get("amount").getAsInt(), 1000));
  }
}
