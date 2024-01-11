package slimeknights.tconstruct.library.json.variable.mining;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.BOOST;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.PERCENT;

/** Variable context for {@link slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule} */
public record MiningSpeedFormula(ModifierFormula formula, List<MiningSpeedVariable> variables, String[] variableNames) implements VariableFormula<MiningSpeedVariable> {
  /** Constructor instance for reading */
  private static final BiFunction<ModifierFormula, List<MiningSpeedVariable>,MiningSpeedFormula> CONSTRUCTOR = (formula, variables) -> new MiningSpeedFormula(formula, variables, EMPTY_STRINGS);
  /** Variables for the modifier formula */
  public static final String[] VARIABLES = { "level", "speed", "multiplier", "original_speed" };

  public MiningSpeedFormula(ModifierFormula formula, Map<String,MiningSpeedVariable> variables) {
    this(formula, List.copyOf(variables.values()), VariableFormula.getNames(variables));
  }

  @Override
  public GenericLoaderRegistry<MiningSpeedVariable> loader() {
    return MiningSpeedVariable.LOADER;
  }

  @Override
  public String[] defaultVariableNames() {
    return VARIABLES;
  }

  /** Builds the arguments from the context */
  private float[] getArguments(IToolStackView tool, ModifierEntry modifier, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit, float baseSpeed, float newSpeed, float multiplier) {
    int size = variables.size();
    float[] arguments = VariableFormula.statModuleArguments(size, formula.computeLevel(tool, modifier), baseSpeed, newSpeed, multiplier * tool.getMultiplier(ToolStats.MINING_SPEED));
    for (int i = 0; i < size; i++) {
      arguments[4+i] = variables.get(i).getValue(tool, event, player, sideHit);
    }
    return arguments;
  }

  /** Runs this formula */
  public float apply(IToolStackView tool, ModifierEntry modifier, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit, float baseSpeed, float newSpeed, float multiplier) {
    return formula.apply(getArguments(tool, modifier, event, player, sideHit, baseSpeed, newSpeed, multiplier));
  }

  /** Deserializes a melee variable context from JSON */
  public static MiningSpeedFormula deserialize(JsonObject json, boolean percent) {
    return VariableFormula.deserialize(MiningSpeedVariable.LOADER, CONSTRUCTOR, json, VARIABLES, percent ? PERCENT : BOOST);
  }

  /** Reads a melee variable context from the network */
  public static MiningSpeedFormula fromNetwork(FriendlyByteBuf buffer, boolean percent) {
    return VariableFormula.fromNetwork(MiningSpeedVariable.LOADER, CONSTRUCTOR, buffer, VARIABLES.length, percent ? PERCENT : BOOST);
  }
}
