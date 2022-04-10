package slimeknights.tconstruct.library.tools.definition.weapon;

import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Attack logic for a modifiable weapon */
public interface IWeaponAttack extends IHaveLoader<IWeaponAttack> {
  /** Default weapon attack */
  IWeaponAttack DEFAULT = new IWeaponAttack() {
    @Override
    public boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage) {
      return ToolAttackUtil.dealDefaultDamage(context.getAttacker(), context.getTarget(), damage);
    }

    @Override
    public IGenericLoader<? extends IWeaponAttack> getLoader() {
      throw new UnsupportedOperationException("Attempt to serialize empty AOE iterator");
    }
  };

  /** Registry of all weapon attack loaders */
  GenericLoaderRegistry<IWeaponAttack> LOADER = new GenericLoaderRegistry<>(DEFAULT);

  /**
   * Deals damage using the tool
   * @param tool    Tool instance
   * @param context  Attack context
   * @param damage   Damage to deal
   * @return  True if we successfully attacked
   */
  boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage);
}
