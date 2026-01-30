package slimeknights.tconstruct.tools.modules.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeCache;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public enum SeveringModule implements ModifierModule, ProcessLootModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SeveringModule>defaultHooks(ModifierHooks.PROCESS_LOOT);
  public static final RecordLoadable<SeveringModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<SeveringModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @SuppressWarnings("removal")
  @Override
  public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
    // if no damage source, probably not a mob
    // otherwise blocks breaking (where THIS_ENTITY is the player) start dropping player heads
    if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
      return;
    }

    // must have an entity
    Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
    if (entity != null) {
      // ensure no head so far
      if (generatedLoot.stream().noneMatch(stack -> stack.is(Tags.Items.HEADS))) {
        // find proper recipe
        Level world = context.getLevel();
        List<SeveringRecipe> recipes = SeveringRecipeCache.findRecipe(world.getRecipeManager(), entity.getType());
        if (!recipes.isEmpty()) {
          float level = modifier.getEffectiveLevel();
          float looting = context.getLootingModifier();
          // deprecated method of doubling chances
          float chanceMultiplier = entity.getType().is(TinkerTags.EntityTypes.RARE_MOBS) ? 2 : 1;
          for (SeveringRecipe recipe : recipes) {
            if (world.random.nextFloat() < recipe.getChance(level, looting) * chanceMultiplier) {
              ItemStack result = recipe.getOutput(entity);
              if (!result.isEmpty()) {
                // if count is not 1, it's a random range from 1 to count
                if (result.getCount() > 1) {
                  result.setCount(world.random.nextInt(result.getCount()) + 1);
                }
                generatedLoot.add(result);
              }
            }
          }
        }
      }
    }
  }
}
