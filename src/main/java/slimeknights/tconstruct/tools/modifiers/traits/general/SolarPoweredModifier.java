package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class SolarPoweredModifier extends SingleUseModifier {
  @Override
  public int getPriority() {
    return 185; // after tanned, before stoneshield
  }

  @Override
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    if (holder != null) {
      Level world = holder.getCommandSenderWorld();
      // note this may go negative, that is not a problem
      int skylight = world.getBrightness(LightLayer.SKY, holder.blockPosition()) - world.getSkyDarken();
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
