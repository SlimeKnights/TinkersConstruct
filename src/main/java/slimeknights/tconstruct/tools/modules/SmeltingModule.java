package slimeknights.tconstruct.tools.modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.LauncherHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.SingleItemContainer;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule.TAG_SLOT;

/** Module that cooks items inside a frypan */
public record SmeltingModule(RecipeType<? extends AbstractCookingRecipe> recipeType, float multiplier, InventoryModule input, InventoryModule output) implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, LauncherHitModifierHook, BlockHarvestModifierHook, ProjectileLaunchModifierHook, OnAttackedModifierHook, PlantHarvestModifierHook, ShearsModifierHook, SlingLaunchModifierHook {
  /** NBT key to store the cooking time */
  private static final String TAG_TIME = "tic_remaining_time";
  /** Container instance for recipe lookups */
  private static final SingleItemContainer CONTAINER = new SingleItemContainer();
  /** Cache of last recipe found */
  private static AbstractCookingRecipe lastRecipe = null;
  /** Cooking time for when a slot has no available recipe */
  private static final int NO_RECIPE = -1;
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SmeltingModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.LAUNCHER_HIT, ModifierHooks.BLOCK_HARVEST, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.ON_ATTACKED, ModifierHooks.PLANT_HARVEST, ModifierHooks.SHEAR_ENTITY, ModifierHooks.SLING_LAUNCH);
  @SuppressWarnings("unchecked")
  public static final RecordLoadable<SmeltingModule> LOADER = RecordLoadable.create(
    TinkerLoadables.RECIPE_TYPE.<RecipeType<? extends AbstractCookingRecipe>>flatXmap(t -> (RecipeType<? extends AbstractCookingRecipe>) t, t -> t)
                               .requiredField("recipe_type", SmeltingModule::recipeType),
    FloatLoadable.FROM_ZERO.requiredField("multiplier", SmeltingModule::multiplier),
    InventoryModule.LOADER.directField(SmeltingModule::input),
    OutputKeyField.INSTANCE,
    // TODO 1.21: remove default value
    Pattern.PARSER.defaultField("output_pattern", Patterns.RESULT, true, m -> m.output.pattern()),
    SmeltingModule::new);

  /** @apiNote use {@link #SmeltingModule(RecipeType, float, InventoryModule, ResourceLocation, Pattern)} */
  @Internal
  public SmeltingModule {}

  public SmeltingModule(RecipeType<? extends AbstractCookingRecipe> recipeType, float multiplier, InventoryModule inventory, @Nullable ResourceLocation outputKey, Pattern outputPattern) {
    this(recipeType, multiplier, inventory, InventoryModule.builder().from(inventory).key(outputKey).pattern(outputPattern).filter(ItemPredicate.NONE).slots(inventory.slots()));
  }

  public SmeltingModule(RecipeType<? extends AbstractCookingRecipe> recipeType, float multiplier, InventoryModule inventory) {
    this(recipeType, multiplier, inventory, null, Patterns.RESULT);
  }

  @Override
  public RecordLoadable<SmeltingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addModules(Builder builder) {
    builder.addModule(input);
    builder.addModule(output);
  }

  /** Finds the recipe for the given stack */
  @Nullable
  private static AbstractCookingRecipe findRecipe(RecipeType<? extends AbstractCookingRecipe> recipeType, ItemStack stack, Level level, ModifierId modifier) {
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

  /** Redirect to {@link #cookItems(IToolStackView, ModifierEntry, Level, LivingEntity, float)} that fetches level from an entity. */
  private void cookItems(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, float amount) {
    cookItems(tool, modifier, holder.level(), holder, amount);
  }

  /** Logic to actually cook the items */
  private void cookItems(IToolStackView tool, ModifierEntry modifier, Level level, @Nullable LivingEntity holder, float amount) {
    if (!input.condition().matches(tool, modifier) || amount < 0) {
      return;
    }
    // first, fetch the inventory, ensure it exists
    ModDataNBT data = tool.getPersistentData();
    ResourceLocation key = input.getKey(modifier.getModifier());

    if (data.contains(key, Tag.TAG_LIST)) {
      // going to cook each slot until we used up all the cooking power
      ListTag list = tool.getPersistentData().get(key, InventoryModule.GET_COMPOUND_LIST);
      float cookingPower = amount * multiplier;
      for (int i = 0; i < list.size(); i++) {
        // lazily load a few pieces of data
        AbstractCookingRecipe recipe = null;
        ItemStack stack = null;

        CompoundTag entry = list.getCompound(i);
        int time = entry.getInt(TAG_TIME);
        // 0 means no recipe, time for a lookup
        if (time == 0) {
          time = NO_RECIPE;
          stack = ItemStack.of(entry);
          recipe = findRecipe(recipeType, stack, level, modifier.getId());
          if (recipe != null) {
            time = recipe.getCookingTime();
          }
          entry.putInt(TAG_TIME, time);
        }
        // negative time means can't cook (no recipe)
        if (time >= 0) {
          // if we ran out of heat, stop cooking
          if (time > cookingPower) {
            time -= cookingPower;
            entry.putInt(TAG_TIME, time);
          } else {
            // locate the result and see if we might fit
            int slot = entry.getInt(TAG_SLOT);
            ItemStack currentResult = output.getStack(tool, modifier, slot);
            int maxStackSize = 0;
            if (!currentResult.isEmpty()) {
              maxStackSize = Math.min(currentResult.getMaxStackSize(), output.getSlotLimit(tool, modifier, slot));
              // no space in output? freeze at 1 tick left to cook
              if (currentResult.getCount() >= maxStackSize) {
                entry.putInt(TAG_TIME, 1);
                continue;
              }
            }

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
                ItemStack result = recipe.assemble(CONTAINER, level.registryAccess());

                // check again if we have space for the result now that we know its size
                if (!result.isEmpty()) {
                  // will be 0 if the current result is empty; use the new result size in that case
                  if (maxStackSize == 0) {
                    maxStackSize = Math.min(result.getMaxStackSize(), output.getSlotLimit(tool, modifier, slot));
                  }
                  // if not enough space for the combo or its type is wrong, just mark as almost finished and give up
                  if (result.getCount() + currentResult.getCount() > maxStackSize || !currentResult.isEmpty() && !ItemStack.isSameItemSameTags(currentResult, result)) {
                    entry.putInt(TAG_TIME, 1);
                    CONTAINER.setStack(ItemStack.EMPTY);
                    continue;
                  }
                }

                // it fits! now time to make it sits
                // update output stack
                if (currentResult.isEmpty()) {
                  output.setStack(tool, modifier, slot, result);
                } else {
                  currentResult.grow(result.getCount());
                  output.setStack(tool, modifier, slot, currentResult);
                }

                // shrink the input
                stack.shrink(1);
                if (stack.isEmpty()) {
                  list.remove(i);
                  i--;
                } else {
                  // shrink stack
                  InventoryModule.writeStack(stack, slot, entry);
                  // update time to cook again
                  entry.putInt(TAG_TIME, recipe.getCookingTime());
                }

                // play sound
                if (holder != null) {
                  level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, holder.getSoundSource(), 1, 1);

                  // grant XP
                  float experience = recipe.getExperience();
                  if (experience > 0 && level instanceof ServerLevel serverLevel) {
                    int floored = Mth.floor(experience);
                    float fraction = Mth.frac(experience);
                    if (fraction != 0 && level.getRandom().nextFloat() < fraction) {
                      floored += 1;
                    }
                    ExperienceOrb.award(serverLevel, holder.position(), floored);
                  }
                }
              } catch (Exception e) {
                TConstruct.LOG.error("Error getting result of recipe {} on modifier {}, this usually indicates a broken recipe", recipe.getId(), modifier, e);
              }
              CONTAINER.setStack(ItemStack.EMPTY);
            } else {
              // lost the recipe? stop trying to smelt it
              entry.putInt(TAG_TIME, NO_RECIPE);
            }
          }
        }
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // melee hits cook by melee damage
    cookItems(tool, modifier, context.getAttacker(), damageDealt);
  }

  @Override
  public void onLauncherHitEntity(IToolStackView tool, ModifierEntry modifier, Projectile projectile, LivingEntity attacker, Entity target, @Nullable LivingEntity livingTarget, float damageDealt) {
    cookItems(tool, modifier, attacker, damageDealt);
  }

  @Override
  public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
    cookItems(tool, modifier, context.getLiving(), harvested);
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // arrow launch cook by arrow power
    float amount;
    if (arrow != null) {
      amount = (float) arrow.getBaseDamage();
    } else if (projectile instanceof ProjectileWithPower withPower) {
      amount = withPower.getPower();
    } else {
      amount = ToolStats.PROJECTILE_DAMAGE.getDefaultValue();
    }
    cookItems(tool, modifier, shooter, amount);
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // damage taken cooks for armor/shields
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      cookItems(tool, modifier, context.getEntity(), amount);
    }
  }

  @Override
  public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
    // power becomes force, but is halved when applied, so double it. Divide by sling specific multipliers
    cookItems(tool, modifier, holder, force * 2 / multiplier);
  }

  @Override
  public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    cookItems(tool, modifier, context.getLevel(), context.getPlayer(), 1);
  }

  @Override
  public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, Player player, Entity entity, boolean isTarget) {
    cookItems(tool, modifier, player, 1);
  }

  /** Custom field instance for the output key field, allows defaulting to the context key. */
  private enum OutputKeyField implements LoadableField<ResourceLocation,SmeltingModule> {
    INSTANCE;

    @Override
    public String key() {
      return "output_key";
    }

    @Override
    public ResourceLocation get(JsonObject json, String key, TypedMap context) {
      if (json.has(key)) {
        return JsonHelper.getResourceLocation(json, key);
      }
      // default to modifier name with an output suffix
      ResourceLocation id = context.get(ContextKey.ID);
      if (id != null) {
        return id.withSuffix("_output");
      }
      throw new JsonParseException("Unable to fetch ID from context, this usually implements a broken JSON deserializer");
    }

    @Override
    public void serialize(SmeltingModule module, JsonObject json) {
      ResourceLocation key = module.output.key();
      if (key != null) {
        json.addProperty(key(), key.toString());
      }
    }

    @Override
    public ResourceLocation decode(FriendlyByteBuf buffer, TypedMap context) {
      return buffer.readResourceLocation();
    }

    @Override
    public void encode(FriendlyByteBuf buffer, SmeltingModule module) {
      buffer.writeResourceLocation(Objects.requireNonNull(module.output.key()));
    }
  }
}
