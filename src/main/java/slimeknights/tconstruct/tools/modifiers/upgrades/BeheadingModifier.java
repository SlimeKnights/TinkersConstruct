package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipeCache;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.misc.CommonTags;

import java.util.List;

public class BeheadingModifier extends Modifier {
  public BeheadingModifier() {
    super(0xBB8972);
  }

  @Override
  public List<ItemStack> processLoot(ToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping player heads
    if (!context.has(LootParameters.DAMAGE_SOURCE)) {
      return generatedLoot;
    }

    // must have an entity
    Entity entity = context.get(LootContextParameters.THIS_ENTITY);
    if (entity != null) {
      // ensure no head so far
      if (generatedLoot.stream().noneMatch(stack -> CommonTags.HEADS.contains(stack.getItem()))) {
        // find proper recipe
        BeheadingRecipe recipe = BeheadingRecipeCache.findRecipe(context.getWorld().getRecipeManager(), entity.getType());
        if (recipe != null) {
          // 5% chance per level, bonus 5% per level of looting
          if (RANDOM.nextFloat() < ((level + EnchantmentHelper.getLooting(((LivingEntity) context.get(LootContextParameters.THIS_ENTITY)))) * 0.05f)) {
            generatedLoot.add(recipe.getOutput(entity));
          }
        }
      }
    }
    return generatedLoot;
  }
}
