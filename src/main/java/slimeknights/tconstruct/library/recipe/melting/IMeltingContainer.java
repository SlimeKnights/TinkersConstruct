package slimeknights.tconstruct.library.recipe.melting;

import slimeknights.mantle.recipe.container.ISingleStackContainer;

/** Interface for melting inventories */
public interface IMeltingContainer extends ISingleStackContainer {
  /** Default number of nuggets per ore */
  int BASE_NUGGET_RATE = 9;

  /**
   * Gets the number of nuggets to return per ore block
   * @return  Nuggets per ore
   */
  int getNuggetsPerOre();

  /**
   * Applies the value from the parameter to boost this ore
   * @param amount  Amount
   * @param rate    Rate
   * @return  Boosted amount
   */
  static int applyOreBoost(int amount, int rate) {
    return amount * rate / BASE_NUGGET_RATE;
  }
}
