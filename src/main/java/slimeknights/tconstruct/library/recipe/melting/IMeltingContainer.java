package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.container.ISingleStackContainer;

import java.util.Locale;

/** Interface for melting inventories */
public interface IMeltingContainer extends ISingleStackContainer {
  /**
   * Gets the logic to boost an ore with the ore rate
   * @return  Nuggets per ore
   */
  IOreRate getOreRate();

  /** Ore rate logic in a melting container */
  interface IOreRate {
    /** Boosts the given integer by the rate */
    int applyOreBoost(OreRateType rate, int amount);

    /** Boosts the given fluid stack by the rate */
    default FluidStack applyOreBoost(OreRateType rate, FluidStack fluid) {
      return new FluidStack(fluid, applyOreBoost(rate, fluid.getAmount()));
    }
  }

  /** Ore rate options */
  enum OreRateType {
    METAL,
    GEM;

    @Getter
    private final String name = this.name().toLowerCase(Locale.ROOT);

    /** Parses an ore rate from Json */
    public static OreRateType parse(JsonObject parent, String key) {
      String name = GsonHelper.getAsString(parent, key);
      switch (name) {
        case "metal": return METAL;
        case "gem": return GEM;
      }
      throw new JsonSyntaxException("Unknown ore rate type " + name);
    }
  }
}
