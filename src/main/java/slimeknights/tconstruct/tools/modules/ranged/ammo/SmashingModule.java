package slimeknights.tconstruct.tools.modules.ranged.ammo;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileFuseModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability.FluidModifierHook;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/** Module that allows arrows to perform fluid effect on hit */
public enum SmashingModule implements ModifierModule, FluidModifierHook, ProjectileLaunchModifierHook.NoShooter, ProjectileHitModifierHook, ProjectileFuseModifierHook, VolatileDataModifierHook, ValidateModifierHook, ModifierRemovalHook, DisplayNameModifierHook, TooltipModifierHook {
  INSTANCE;

  /** Key storing current fluid */
  private static final ResourceLocation KEY_FLUID = TConstruct.getResource("smashing_fluid");
  /** Key storing current fluid tag, if present */
  private static final ResourceLocation KEY_FLUID_TAG = TConstruct.getResource("smashing_fluid_tag");
  /** Key storing amount, only used on projectile data */
  private static final ResourceLocation KEY_AMOUNT = TConstruct.getResource("smashing_amount");
  /** Key storing validation constant, ensures part swapping doesn't cause issues. Used only on the tool. */
  private static final ResourceLocation KEY_VALIDATE = TConstruct.getResource("smashing_validate");
  /** Projectile boolean marking that a fluid effect happened */
  private static final ResourceLocation KEY_USED = TConstruct.getResource("smashing_used");
  private static final String FORMAT = TConstruct.makeTranslationKey("modifier", "smashing.format");
  private static final Component EMPTY_TO_SWAP = TConstruct.makeTranslation("modifier", "smashing.empty_to_swap");
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SmashingModule>defaultHooks(ToolFluidCapability.HOOK, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_FUSE, ModifierHooks.VOLATILE_DATA, ModifierHooks.VALIDATE, ModifierHooks.REMOVE, ModifierHooks.DISPLAY_NAME, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<SmashingModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public RecordLoadable<SmashingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }


  /* Tank helpers */

  /** Gets the capacity for the given fluid */
  private static int getAmount(Fluid fluid) {
    FluidEffects effects = FluidEffectManager.INSTANCE.find(fluid);
    return effects.hasEffects() ? effects.ingredient().getAmount(fluid) : 0;
  }

  /** Gets the capacity for the given fluid */
  private static int getAmount(ModifierEntry modifier, Fluid fluid) {
    return getAmount(fluid) * modifier.getLevel();
  }

  /** Gets the fluid from the given mod data */
  private static Fluid getFluid(IModDataView data) {
    if (data.contains(KEY_FLUID, Tag.TAG_STRING)) {
      ResourceLocation id = ResourceLocation.tryParse(data.getString(KEY_FLUID));
      if (id != null) {
        return BuiltInRegistries.FLUID.get(id);
      }
    }
    return Fluids.EMPTY;
  }

  /** Gets the current fluid NBT */
  @Nullable
  private static CompoundTag getFluidTag(IModDataView data) {
    if (data.contains(KEY_FLUID_TAG, Tag.TAG_COMPOUND)) {
      return data.getCompound(KEY_FLUID_TAG);
    }
    return null;
  }

  /** Removes the fluid from the tool */
  private static void clearFluid(ModDataNBT data) {
    data.remove(KEY_FLUID);
    data.remove(KEY_VALIDATE);
    data.remove(KEY_AMOUNT);
    data.remove(KEY_FLUID_TAG);
  }

  /** Gets the amount to store in NBT to ensure no funny business with part swapping causes dupes */
  private static float getValidationAmount(IToolStackView tool, ModifierEntry modifier) {
    float level = modifier.getEffectiveLevel();
    for (ModifierEntry entry : tool.getModifiers()) {
      level = entry.getHook(ModifierHooks.CRAFT_COUNT).modifyCraftCount(tool, modifier, level);
    }
    return level;
  }

  /* Tank filling/draining */

  @Override
  public int fill(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }
    ModDataNBT data = tool.getPersistentData();
    // already has fluid? can't fill
    if (data.contains(KEY_FLUID, Tag.TAG_STRING)) {
      return 0;
    }
    int amount = getAmount(modifier, resource.getFluid());
    // if the fluid is invalid, or not enough fluid is offered, give up
    if (amount == 0 || resource.getAmount() < amount) {
      return 0;
    }
    // success! we can fill
    if (action.execute()) {
      // we don't actually store the amount, its up to the modifier to determine that
      data.putString(KEY_FLUID, Loadables.FLUID.getString(resource.getFluid()));
      // we want to store a fixed size, but its possible part swapping changes our capacity, so keep track of our capacity at the time of storing
      data.putFloat(KEY_VALIDATE, getValidationAmount(tool, modifier));
      CompoundTag tag = resource.getTag();
      if (tag != null) {
        data.put(KEY_FLUID_TAG, tag.copy());
      }
    }
    return amount;
  }

  @Override
  public FluidStack drain(IToolStackView tool, ModifierEntry modifier, int maxDrain, FluidAction action) {
    if (maxDrain > 0) {
      ModDataNBT data = tool.getPersistentData();
      Fluid fluid = getFluid(data);
      if (fluid != Fluids.EMPTY) {
        int amount = getAmount(modifier, fluid);
        if (amount <= 0) {
          // 0 amount with a fluid means datapacks changed, best we can do is delete what we have
          clearFluid(data);
          // ensure we requested enough
        } else if (amount <= maxDrain) {
          FluidStack result = new FluidStack(fluid, amount, getFluidTag(data));
          if (action.execute()) {
            clearFluid(data);
          }
          return result;
        }
      }
    }
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(IToolStackView tool, ModifierEntry modifier, FluidStack resource, FluidAction action) {
    if (!resource.isEmpty()) {
      ModDataNBT data = tool.getPersistentData();
      Fluid fluid = getFluid(data);
      // ensure we have a valid fluid
      if (fluid != Fluids.EMPTY && resource.getFluid() == fluid) {
        int amount = getAmount(modifier, fluid);
        if (amount <= 0) {
          // 0 amount with a fluid means datapacks changed, best we can do is delete what we have
          clearFluid(data);
          // ensure we requested enough
        } else if (amount <= resource.getAmount()) {
          // ensure the tag matches
          CompoundTag storedTag = getFluidTag(data);
          if (Objects.equals(storedTag, resource.getTag())) {
            FluidStack result = new FluidStack(fluid, amount, storedTag);
            if (action.execute()) {
              clearFluid(data);
            }
            return result;
          }
        }
      }
    }
    return FluidStack.EMPTY;
  }


  /* Other tank behaviors */

  @Override
  public FluidStack getFluidInTank(IToolStackView tool, ModifierEntry modifier, int tank) {
    ModDataNBT data = tool.getPersistentData();
    Fluid fluid = getFluid(data);
    if (fluid != Fluids.EMPTY) {
      int amount = getAmount(modifier, fluid);
      if (amount > 0) {
        return new FluidStack(fluid, amount, getFluidTag(data));
      } else {
        // invalid, nothing more to do
        clearFluid(data);
      }
    }
    return FluidStack.EMPTY;
  }

  @Override
  public int getTankCapacity(IToolStackView tool, ModifierEntry modifier, int tank) {
    Fluid fluid = getFluid(tool.getPersistentData());
    if (fluid != Fluids.EMPTY) {
      return getAmount(modifier, fluid);
    }
    // TODO: should we return something else? this number when empty is really meaningless
    return FluidValues.BOTTLE;
  }

  @Override
  public boolean isFluidValid(IToolStackView tool, ModifierEntry modifier, int tank, FluidStack fluid) {
    return getAmount(fluid.getFluid()) > 0;
  }


  /* Tool data */

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    ToolFluidCapability.addTanks(modifier, volatileData, this);
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    ModDataNBT data = tool.getPersistentData();
    if (data.contains(KEY_FLUID, Tag.TAG_STRING)) {
      // if our new level is larger, error to prevent a fluid dupe
      float level = getValidationAmount(tool, modifier);
      if (data.getInt(KEY_VALIDATE) < level) {
        return EMPTY_TO_SWAP;
      }
      // delete some fluid to match new level
      data.putFloat(KEY_VALIDATE, level);
    }
    return null;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    // delete fluid on swap, such a waste
    clearFluid(tool.getPersistentData());
    return null;
  }


  /* Fluid effects */

  @Override
  public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // if firing an arrow via multishot, don't fill the extra projectiles with fluid to prevent some dupes with effects
    if (arrow == null || arrow.pickup == Pickup.ALLOWED || shooter instanceof Player player && player.getAbilities().instabuild) {
      ModDataNBT toolData = tool.getPersistentData();
      Fluid fluid = getFluid(toolData);
      if (fluid != Fluids.EMPTY) {
        int amount = getAmount(modifier, fluid);
        if (amount > 0) {
          persistentData.putString(KEY_FLUID, toolData.getString(KEY_FLUID));
          persistentData.putInt(KEY_AMOUNT, amount);
          if (toolData.contains(KEY_FLUID_TAG, Tag.TAG_COMPOUND)) {
            persistentData.put(KEY_FLUID_TAG, toolData.getCompound(KEY_FLUID_TAG));
          }
        }
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
    // find our fluid and ensure it affects blocks
    Fluid fluid = getFluid(persistentData);
    if (fluid != Fluids.EMPTY) {
      int amount = persistentData.getInt(KEY_AMOUNT);
      if (amount > 0) {
        FluidEffects effects = FluidEffectManager.INSTANCE.find(fluid);
        if (effects.hasEntityEffects()) {
          // apply the effect
          int drained = effects.applyToEntity(
            new FluidStack(fluid, amount, getFluidTag(persistentData)),
            modifier.getEffectiveLevel(),
            FluidEffectContext.builder(projectile.level()).user(attacker).projectile(projectile).location(hit.getLocation()).target(hit.getEntity(), target),
            FluidAction.EXECUTE
          );
          // drain the fluid
          if (drained > 0) {
            int remaining = amount - drained;
            if (remaining > 0) {
              persistentData.putInt(KEY_AMOUNT, remaining);
            } else {
              clearFluid(persistentData);
            }
            projectile.playSound(SoundEvents.SPLASH_POTION_BREAK);
            // mark as used to prevent arrow pickup later
            if (projectile instanceof AbstractArrow) {
              persistentData.putBoolean(KEY_USED, true);
            } else {
              projectile.discard();
            }
          }
        }
      } else {
        clearFluid(persistentData);
      }
    }
    return projectile.isRemoved();
  }

  @Override
  public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    // find our fluid and ensure it affects blocks
    Fluid fluid = getFluid(persistentData);
    boolean used = false;
    if (fluid != Fluids.EMPTY) {
      int amount = persistentData.getInt(KEY_AMOUNT);
      if (amount > 0) {
        FluidEffects effects = FluidEffectManager.INSTANCE.find(fluid);
        if (effects.hasBlockEffects()) {
          // apply the effect
          int drained = effects.applyToBlock(
            new FluidStack(fluid, amount, getFluidTag(persistentData)),
            modifier.getEffectiveLevel(),
            FluidEffectContext.builder(projectile.level()).user(attacker).projectile(projectile).location(hit.getLocation()).block(hit),
            FluidAction.EXECUTE
          );
          // drain the fluid
          if (drained > 0) {
            int remaining = amount - drained;
            used = true;
            if (remaining > 0) {
              // TODO: ideally, would like the arrow to live past this point to hit another block if the block is gone
              // but we don't have hit canceling tech right now due to an API change
              persistentData.putInt(KEY_AMOUNT, remaining);
            } else {
              clearFluid(persistentData);
            }
          }
        }
      } else {
        clearFluid(persistentData);
      }
    }
    // if the arrow is stopping, discard it to prevent a fluid dupe
    if (used || persistentData.getBoolean(KEY_USED)) {
      projectile.playSound(SoundEvents.SPLASH_POTION_BREAK);
      projectile.discard();
    }
  }

  @Override
  public void onProjectileFuseFinish(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow) {
    Fluid fluid = getFluid(persistentData);
    boolean used = false;
    if (fluid != Fluids.EMPTY) {
      int amount = persistentData.getInt(KEY_AMOUNT);
      if (amount > 0) {
        FluidEffects effects = FluidEffectManager.INSTANCE.find(fluid);
        if (effects.hasBlockEffects()) {
          // apply the effect at the location of the projectile
          Vec3 position = projectile.position();
          int drained = effects.applyToBlock(
            new FluidStack(fluid, amount, getFluidTag(persistentData)),
            modifier.getEffectiveLevel(),
            FluidEffectContext.builder(projectile.level()).user(projectile.getOwner()).projectile(projectile).location(position)
              .block(new BlockHitResult(position, projectile.getDirection(), projectile.blockPosition(), false)),
            FluidAction.EXECUTE
          );
          // drain the fluid
          if (drained > 0) {
            int remaining = amount - drained;
            used = true;
            if (remaining > 0) {
              persistentData.putInt(KEY_AMOUNT, remaining);
            } else {
              clearFluid(persistentData);
            }
          }
        }
      } else {
        clearFluid(persistentData);
      }
    }
    // if the arrow is stopping, discard it to prevent a fluid dupe
    if (used || persistentData.getBoolean(KEY_USED)) {
      projectile.playSound(SoundEvents.SPLASH_POTION_BREAK);
    }
  }


  /* Tooltip */

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    IModDataView data = tool.getPersistentData();
    Fluid fluid = getFluid(data);
    if (fluid != Fluids.EMPTY) {
      // formats as <name> <level> (<fluid>)
      return Component.translatable(FORMAT, name,
        new FluidStack(fluid, FluidValues.BOTTLE, getFluidTag(data)).getDisplayName()
      ).withStyle(name.getStyle());
    }
    return name;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    IModDataView data = tool.getPersistentData();
    Fluid fluid = getFluid(data);
    if (fluid != Fluids.EMPTY) {
      int amount = getAmount(modifier, fluid);
      if (amount > 0) {
        // formats as <fluid>: <amount> mb
        tooltip.add(modifier.getModifier().applyStyle(new FluidStack(fluid, amount, getFluidTag(data)).getDisplayName().copy()
          .append(": ").append(Component.translatable(ToolTankHelper.MB_FORMAT, TranslationHelper.COMMA_FORMAT.format(amount)))));
      }
    }
  }


  /* Helper */

  /** Helper for working with models. Not designed to be used directly. */
  public static ToolTankHelper TANK_HELPER = new ToolTankHelper(ToolTankHelper.CAPACITY_STAT, KEY_FLUID) {
    @Override
    public FluidStack getFluid(IToolStackView tool) {
      IModDataView data = tool.getPersistentData();
      Fluid fluid = SmashingModule.getFluid(data);
      if (fluid != Fluids.EMPTY) {
        int amount = getAmount(fluid);
        if (amount > 0) {
          return new FluidStack(fluid, amount, getFluidTag(data));
        }
      }
      return FluidStack.EMPTY;
    }

    @Deprecated
    @Override
    public FluidStack setFluid(IToolStackView tool, FluidStack fluid) {
      // disallowed currently, as we don't have a quick way to validate capacity for multiple levels
      return FluidStack.EMPTY;
    }

    @Override
    public int getCapacity(IToolStackView tool) {
      IModDataView data = tool.getPersistentData();
      Fluid fluid = SmashingModule.getFluid(data);
      if (fluid != Fluids.EMPTY) {
        return getAmount(fluid);
      }
      return FluidValues.BOTTLE;
    }
  };
}
