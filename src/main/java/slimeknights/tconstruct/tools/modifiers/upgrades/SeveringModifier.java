package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipeCache;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

public class SeveringModifier extends Modifier {
  public SeveringModifier() {
    super(0xBB8972);
  }

  @Override
  public List<ItemStack> processLoot(IModifierToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping player heads
    if (!context.has(LootParameters.DAMAGE_SOURCE)) {
      return generatedLoot;
    }

    // must have an entity
    Entity entity = context.get(LootParameters.THIS_ENTITY);
    if (entity != null) {
      // ensure no head so far
      if (generatedLoot.stream().noneMatch(stack -> Tags.Items.HEADS.contains(stack.getItem()))) {
        // find proper recipe
        List<SeveringRecipe> recipes = SeveringRecipeCache.findRecipe(context.getWorld().getRecipeManager(), entity.getType());
        if (!recipes.isEmpty()) {
          // 5% chance per level, bonus 5% per level of looting
          float chance = (level + context.getLootingModifier()) * 0.05f;
          for (SeveringRecipe recipe : recipes) {
            ItemStack result = recipe.getOutput(entity);
            if (!result.isEmpty() && RANDOM.nextFloat() < chance) {
              // if count is not 1, its a random range from 1 to count
              if (result.getCount() > 1) {
                result.setCount(RANDOM.nextInt(result.getCount()) + 1);
              }
              generatedLoot.add(result);
            }
          }
        }
      }
    }
    return generatedLoot;
  }
}
