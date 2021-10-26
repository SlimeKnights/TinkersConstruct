package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class SolarPoweredModifier extends SingleUseModifier {
  public SolarPoweredModifier() {
    super(0x48B518);
  }

  @Override
  public int getPriority() {
    return 185; // after tanned, before stoneshield
  }

  @Override
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount, @Nullable LivingEntity holder) {
    if (holder != null) {
      World world = holder.getEntityWorld();
      // note this may go negative, that is not a problem
      int skylight = world.getLightFor(LightType.SKY, holder.getPosition()) - world.getSkylightSubtracted();
      if (skylight > 0) {
        float chance = skylight * 0.05f; // up to a 75% chance at max sunlight
        int maxDamage = amount;
        // for each damage we will take, if the random number is below chance, reduce
        for (int i = 0; i < maxDamage; i++) {
          if (RANDOM.nextFloat() < chance) {
            amount--;
          }
        }
      }
    }
    return amount;
  }
}
