package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IHelmetInteractModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes on helmets */
public class SlurpingModifier extends TankModifier implements IHelmetInteractModifier {
  public SlurpingModifier() {
    super(0xF98648, FluidAttributes.BUCKET_VOLUME);
  }

  @Override
  public boolean startHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    if (!player.isSneaking()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(player.getEntityWorld().getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          if (!player.getEntityWorld().isRemote) {
            ToolAttackContext context = new ToolAttackContext(player, player, Hand.MAIN_HAND, player, player, false, 1.0f, false);
            FluidStack remaining = recipe.applyEffects(fluid, level, context);
            if (!player.isCreative()) {
              setFluid(tool, remaining);
            }
          }
          player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);
          return true;
        }
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IHelmetInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }
}
