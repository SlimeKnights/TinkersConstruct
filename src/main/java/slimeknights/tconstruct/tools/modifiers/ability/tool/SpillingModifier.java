package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Modifier to handle spilling recipes */
public class SpillingModifier extends TankModifier {
  public SpillingModifier() {
    super(0xF98648, FluidAttributes.BUCKET_VOLUME);
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(context.getAttacker().getEntityWorld().getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          FluidStack remaining = recipe.applyEffects(fluid, level, context);
          PlayerEntity player = context.getPlayerAttacker();
          if (player == null || !player.isCreative()) {
            setFluid(tool, remaining);
          }
        }
      }
    }
    return 0;
  }
}
