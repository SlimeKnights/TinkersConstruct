package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeCache;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class SeveringModifier extends Modifier {
  @Override
  public List<ItemStack> processLoot(IToolStackView tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping player heads
    if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
      return generatedLoot;
    }

    // must have an entity
    Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
    if (entity != null) {
      // ensure no head so far
      if (generatedLoot.stream().noneMatch(stack -> stack.is(Tags.Items.HEADS))) {
        // find proper recipe
        List<SeveringRecipe> recipes = SeveringRecipeCache.findRecipe(context.getLevel().getRecipeManager(), entity.getType());
        if (!recipes.isEmpty()) {
          // 5% chance per level, each luck level adds an extra 1% per severing level
          float chance = (level) * (0.05f + 0.01f * context.getLootingModifier());
          // double chance for mobs such as ender dragons and the wither
          if (entity.getType().is(TinkerTags.EntityTypes.RARE_MOBS)) {
            chance *= 2;
          }
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
