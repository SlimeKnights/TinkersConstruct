package slimeknights.tconstruct.library.json.variable.melee;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable for use in {@link ConditionalMeleeDamageModule} */
public interface MeleeVariable extends IHaveLoader<MeleeVariable> {
  GenericLoaderRegistry<MeleeVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /**
   * Gets the value of the variable
   * @param tool     Tool instance
   * @param context  Attack context, will be null in tooltips
   * @param attacker Entity using the tool, may be null conditionally in tooltips
   * @return  Value of this variable
   */
  float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker);

  
  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, MeleeVariable {
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
      return value;
    }

    @Override
    public IGenericLoader<? extends MeleeVariable> getLoader() {
      return LOADER;
    }
  }
}
