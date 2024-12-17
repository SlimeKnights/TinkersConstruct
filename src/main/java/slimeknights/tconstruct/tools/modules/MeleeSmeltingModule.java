package slimeknights.tconstruct.tools.modules;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.SingleItemContainer;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/** Module that cooks items inside a frypan */
public record MeleeSmeltingModule(RecipeType<? extends AbstractCookingRecipe> recipeType, float multiplier, InventoryModule inventory) implements ModifierModule, MeleeHitModifierHook {
  /** NBT key to store the cooking time */
  private static final String TAG_TIME = "tic_remaining_time";
  /** Container instance for recipe lookups */
  private static final SingleItemContainer CONTAINER = new SingleItemContainer();
  /** Cache of last recipe found */
  private static AbstractCookingRecipe lastRecipe = null;
  /** Cooking time for when an item finishes cooking */
  private static final int DONE_COOKING = -1;
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeleeSmeltingModule>defaultHooks(ModifierHooks.MELEE_HIT);
  @SuppressWarnings("unchecked")
  public static final RecordLoadable<MeleeSmeltingModule> LOADER = RecordLoadable.create(
    TinkerLoadables.RECIPE_TYPE.<RecipeType<? extends AbstractCookingRecipe>>flatXmap(t -> (RecipeType<? extends AbstractCookingRecipe>) t, t -> t)
                               .requiredField("recipe_type", MeleeSmeltingModule::recipeType),
    FloatLoadable.FROM_ZERO.requiredField("multiplier", MeleeSmeltingModule::multiplier),
    InventoryModule.LOADER.directField(MeleeSmeltingModule::inventory),
    MeleeSmeltingModule::new);

  @Override
  public RecordLoadable<MeleeSmeltingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addModules(Builder builder) {
    builder.addModule(inventory);
  }

  /** Finds the recipe for the given stack */
  @Nullable
  private static AbstractCookingRecipe findRecipe(RecipeType<? extends AbstractCookingRecipe> recipeType, ItemStack stack, Level level, ModifierId modifier) {
    if (stack.getCount() != 1) {
      return null;
    }
    CONTAINER.setStack(stack);
    try {
      // first, try the cached recipe
      if (lastRecipe != null && lastRecipe.matches(CONTAINER, level)) {
        return lastRecipe;
      }
      // if that failed, do a recipe lookup
      AbstractCookingRecipe recipe = level.getRecipeManager().getRecipeFor(recipeType, CONTAINER, level).orElse(null);
      if (recipe != null) {
        lastRecipe = recipe;
      }
      return recipe;
    } catch (Exception e) {
      // we don't have a good way to validate the recipe type on parse, so an invalid recipe type would error here
      TConstruct.LOG.error("Error fetching recipe for {} on modifier {}, this usually indicates a broken modifier or a broken recipe", stack, modifier, e);
      return null;
    } finally {
      CONTAINER.setStack(ItemStack.EMPTY);
    }
  }


  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!inventory.condition().matches(tool, modifier)) {
      return;
    }
    // first, fetch the inventory, ensure it exists
    ModDataNBT data = tool.getPersistentData();
    ResourceLocation key = inventory.getKey(modifier.getModifier());

    float cookingPower = damageDealt * multiplier;
    if (cookingPower > 0 && data.contains(key, Tag.TAG_LIST)) {
      LivingEntity attacker = context.getAttacker();
      Level level = attacker.getLevel();

      // going to cook each slot until we used up all the cooking power
      ListTag list = tool.getPersistentData().get(key, InventoryModule.GET_COMPOUND_LIST);
      for (int i = 0; i < list.size(); i++) {
        // lazily load a few pieces of data
        AbstractCookingRecipe recipe = null;
        ItemStack stack = null;

        CompoundTag entry = list.getCompound(i);
        int time = entry.getInt(TAG_TIME);
        // 0 means no recipe, time for a lookup
        if (time == 0) {
          time = DONE_COOKING;
          stack = ItemStack.of(entry);
          recipe = findRecipe(recipeType, stack, level, modifier.getId());
          if (recipe != null) {
            time = recipe.getCookingTime();
          }
          entry.putInt(TAG_TIME, time);
        }
        // negative time means done cooking
        if (time >= 0) {
          // if we ran out of heat, stop cooking
          if (time > cookingPower) {
            time -= cookingPower;
            entry.putInt(TAG_TIME, time);
            break;
          } else {
            // item is done cooking, time to update
            cookingPower -= time;

            // use the recipe we fetched earlier if present
            if (recipe == null) {
              stack = ItemStack.of(entry);
              if (!stack.isEmpty()) {
                recipe = findRecipe(recipeType, stack, level, modifier.getId());
              }
            }
            // if we have a recipe, time to cook
            if (recipe != null) {
              // attempt to assemble the recipe, use a try/catch in case their assemble logic is bad
              CONTAINER.setStack(stack);
              try {
                ItemStack result = recipe.assemble(CONTAINER);
                // cooked to nothing? just gotta give up
                if (result.isEmpty()) {
                  list.remove(i);
                  i--;
                } else {
                  // update the stack
                  int slot = entry.getInt(InventoryModule.TAG_SLOT);
                  entry.getAllKeys().clear();
                  // ensure we are not putting larger stacks in
                  if (result.getCount() > 1) {
                    result.setCount(1);
                  }
                  InventoryModule.writeStack(result, slot, entry);

                  // play sound
                  level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, attacker.getSoundSource(), 1, 1);

                  // grant XP
                  float experience = recipe.getExperience();
                  if (experience > 0 && level instanceof ServerLevel serverLevel) {
                    int floored = Mth.floor(experience);
                    float fraction = Mth.frac(experience);
                    if (fraction != 0 && level.getRandom().nextFloat() < fraction) {
                      floored += 1;
                    }
                    ExperienceOrb.award(serverLevel, attacker.position(), floored);
                  }
                }
              } catch (Exception e) {
                TConstruct.LOG.error("Error getting result of recipe {} on modifier {}, this usually indicates a broken recipe", recipe.getId(), modifier, e);
              }
              CONTAINER.setStack(ItemStack.EMPTY);
            }

            // no matter what happened, this item is done cooking
            entry.putInt(TAG_TIME, DONE_COOKING);
          }
        }
      }
    }
  }
}
