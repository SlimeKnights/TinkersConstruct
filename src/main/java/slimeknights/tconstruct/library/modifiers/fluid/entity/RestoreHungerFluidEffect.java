package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

/** Effect to restore hunger to the target */
public record RestoreHungerFluidEffect(int hunger, float saturation, boolean canAlwaysEat, ItemOutput representative) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<RestoreHungerFluidEffect> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ONE.requiredField("hunger", e -> e.hunger),
    FloatLoadable.FROM_ZERO.requiredField("saturation", e -> e.saturation),
    BooleanLoadable.INSTANCE.defaultField("can_always_eat", false, e -> e.canAlwaysEat),
    ItemOutput.Loadable.OPTIONAL_ITEM.emptyField("representative_item", e -> e.representative),
    RestoreHungerFluidEffect::new);

  @Override
  public RecordLoadable<RestoreHungerFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity target = context.getLivingTarget();
    if (target instanceof Player player && player.canEat(canAlwaysEat)) {
      // we always consume the full amount as while hunger is capped, saturation is not
      float value = level.value();
      if (action.execute()) {
        int finalHunger = (int)(hunger * value);
        player.getFoodData().eat(finalHunger, saturation);
        ItemStack representative = this.representative.get();
        if (!representative.isEmpty()) {
          ModifierUtil.foodConsumer.onConsume(player, representative, finalHunger, saturation);
        }
      }
      return value;
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return FluidEffect.makeTranslation(getLoader(), hunger);
  }
}
