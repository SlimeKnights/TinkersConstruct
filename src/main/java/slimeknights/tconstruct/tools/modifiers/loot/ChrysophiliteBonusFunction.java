package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.ApplyBonus.BinomialWithBonusCountFormula;
import net.minecraft.loot.functions.ApplyBonus.IFormula;
import net.minecraft.loot.functions.ApplyBonus.IFormulaDeserializer;
import net.minecraft.loot.functions.ApplyBonus.OreDropsFormula;
import net.minecraft.loot.functions.ApplyBonus.UniformBonusCountFormula;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;

import java.util.Set;

import net.minecraft.loot.LootFunction.Builder;

/** Loot modifier to boost drops based on teh chrysophilite amount */
public class ChrysophiliteBonusFunction extends LootFunction {
  public static final ResourceLocation ID = TConstruct.getResource("chrysophilite_bonus");
  public static final Serializer SERIALIZER = new Serializer();

  /** Formula to apply */
  private final IFormula formula;
  /** If true, the includes the helmet in the level, if false level is just gold pieces */
  private final boolean includeBase;
  protected ChrysophiliteBonusFunction(ILootCondition[] conditions, IFormula formula, boolean includeBase) {
    super(conditions);
    this.formula = formula;
    this.includeBase = includeBase;
  }

  /** Creates a generic builder */
  public static Builder<?> builder(IFormula formula, boolean includeBase) {
    return simpleBuilder(conditions -> new ChrysophiliteBonusFunction(conditions, formula, includeBase));
  }

  /** Creates a builder for the binomial with bonus formula */
  public static Builder<?> binomialWithBonusCount(float probability, int extra, boolean includeBase) {
    return builder(new BinomialWithBonusCountFormula(extra, probability), includeBase);
  }

  /** Creates a builder for the ore drops formula */
  public static Builder<?> oreDrops(boolean includeBase) {
    return builder(new OreDropsFormula(), includeBase);
  }

  /** Creates a builder for the uniform bonus count */
  public static Builder<?> uniformBonusCount(int bonusMultiplier, boolean includeBase) {
    return builder(new UniformBonusCountFormula(bonusMultiplier), includeBase);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    int level = ChrysophiliteModifier.getTotalGold(context.getParamOrNull(LootParameters.THIS_ENTITY));
    if (!includeBase) {
      level--;
    }
    if (level > 0) {
      stack.setCount(formula.calculateNewCount(context.getRandom(), stack.getCount(), level));
    }
    return stack;
  }

  @Override
  public Set<LootParameter<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootParameters.THIS_ENTITY);
  }

  @Override
  public LootFunctionType getType() {
    return TinkerModifiers.chrysophiliteBonusFunction;
  }

  /** Serializer class */
  private static class Serializer extends LootFunction.Serializer<ChrysophiliteBonusFunction> {
    @Override
    public void serialize(JsonObject json, ChrysophiliteBonusFunction loot, JsonSerializationContext context) {
      super.serialize(json, loot, context);
      json.addProperty("formula", loot.formula.getType().toString());
      JsonObject parameters = new JsonObject();
      loot.formula.serializeParams(parameters, context);
      if (parameters.size() > 0) {
        json.add("parameters", parameters);
      }
      json.addProperty("include_base", loot.includeBase);
    }

    @Override
    public ChrysophiliteBonusFunction deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditions) {
      ResourceLocation id = JsonHelper.getResourceLocation(json, "formula");
      IFormulaDeserializer deserializer = ApplyBonus.FORMULAS.get(id);
      if (deserializer == null) {
        throw new JsonParseException("Invalid formula id: " + id);
      }
      JsonObject parameters;
      if (json.has("parameters")) {
        parameters = JSONUtils.getAsJsonObject(json, "parameters");
      } else {
        parameters = new JsonObject();
      }
      IFormula formula = deserializer.deserialize(parameters, context);
      boolean includeBase = JSONUtils.getAsBoolean(json, "include_base", true);
      return new ChrysophiliteBonusFunction(conditions, formula, includeBase);
    }
  }
}
