package slimeknights.tconstruct.smeltery.block.entity.module;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.TinkerTags.EntityTypes;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Module to handle fetching items from the bounds and interacting with entities in the structure
 */
@RequiredArgsConstructor
public class EntityMeltingModule {
  // TODO: migrate to whatever mojang is doing
  private final MantleBlockEntity parent;
  private final IFluidHandler tank;
  /** Supplier that returns true if the tank has space */
  private final BooleanSupplier canMeltEntities;
  /** Function that tries to insert an item into the inventory */
  private final Function<ItemStack, ItemStack> insertFunction;
  /** Function that returns the bounds to check for entities */
  private final Supplier<AABB> bounds;


  @Nullable
  private EntityMeltingRecipe lastRecipe;

  /** Gets a nonnull world instance from the parent */
  private Level getLevel() {
    return Objects.requireNonNull(parent.getLevel(), "Parent tile entity has null world");
  }


  /* Damage sources */

  @Nullable
  private DamageSource smelteryMagic = null;
  /** Damage source for attacking fire immune mobs */
  private DamageSource smelteryMagic() {
    if (smelteryMagic == null) {
      smelteryMagic = TinkerDamageTypes.source(getLevel().registryAccess(), TinkerDamageTypes.SMELTERY_MAGIC);
    }
    return smelteryMagic;
  }

  @Nullable
  private DamageSource smelteryHeat = null;
  /** Damage source for attacking regular mobs */
  private DamageSource smelteryHeat() {
    if (smelteryHeat == null) {
      smelteryHeat = TinkerDamageTypes.source(getLevel().registryAccess(), TinkerDamageTypes.SMELTERY_HEAT);
    }
    return smelteryHeat;
  }

  /**
   * Finds a recipe for the given entity type
   * @param type  Entity type
   * @return  Recipe
   */
  @Nullable
  private EntityMeltingRecipe findRecipe(EntityType<?> type) {
    if (lastRecipe != null && lastRecipe.matches(type)) {
      return lastRecipe;
    }
    // find a new recipe if the last recipe does not match
    EntityMeltingRecipe recipe = EntityMeltingRecipeCache.findRecipe(getLevel().getRecipeManager(), type);
    if (recipe != null) {
      lastRecipe = recipe;
    }
    return recipe;
  }

  /**
   * Gets the default fluid result
   * @return  Default fluid
   */
  public static FluidStack getDefaultFluid() {
    // TODO: consider a way to put this in a recipe
    return new FluidStack(TinkerFluids.liquidSoul.get(), FluidValues.GLASS_PANE / 5);
  }

  /**
   * checks if an entity can be melted
   * @param entity  Entity to check
   * @return  True if they can be melted
   */
  private boolean canMeltEntity(LivingEntity entity) {
    // fire based mobs are absorbed instead of damaged
    return !entity.isInvulnerableTo(entity.fireImmune() ? smelteryMagic() : smelteryHeat())
           // have to special case players because for some dumb reason creative players do not return true to invulnerable to
           && !(entity instanceof Player && ((Player)entity).getAbilities().invulnerable)
           // also have to special case fire resistance, so a blaze with fire resistance is immune to the smeltery
           && !entity.hasEffect(MobEffects.FIRE_RESISTANCE);
  }

  /**
   * Interacts with entities in the structure
   * @return True if something was melted and fuel is needed
   */
  public boolean interactWithEntities() {
    AABB boundingBox = bounds.get();
    if (boundingBox == null) {
      return false;
    }

    Boolean canMelt = null;
    boolean melted = false;
    for (Entity entity : getLevel().getEntitiesOfClass(Entity.class, boundingBox)) {
      if (!entity.isAlive()) {
        continue;
      }

      // items are placed inside the smeltery
      EntityType<?> type = entity.getType();
      if (entity instanceof ItemEntity itemEntity) {
        ItemStack stack = insertFunction.apply(itemEntity.getItem());
        // picked up whole stack
        if (stack.isEmpty()) {
          entity.discard();
        } else {
          itemEntity.setItem(stack);
        }
      }

      // only can melt living, ensure its not immune to our damage
      // if canMelt is already found as false, skip instance checks, we only care about items now
      // if the type is hidden, skip as well, I suppose thats your blacklist if you must have one
      else if (canMelt != Boolean.FALSE && !type.is(EntityTypes.MELTING_HIDE) && entity instanceof LivingEntity && canMeltEntity((LivingEntity)entity)) {
        // only fetch boolean once, its not the fastest as it tries to consume fuel
        if (canMelt == null) canMelt = canMeltEntities.getAsBoolean();

        // ensure we have fuel/any other needed smeltery states
        if (canMelt) {
          // determine what we are melting
          FluidStack fluid;
          int damage;
          EntityMeltingRecipe recipe = findRecipe(entity.getType());
          if (recipe != null) {
            fluid = recipe.getOutput((LivingEntity) entity);
            damage = recipe.getDamage();
          } else {
            fluid = getDefaultFluid();
            damage = 2;
          }

          // if the entity is successfully damaged, fill the tank with fluid
          if (entity.hurt(entity.fireImmune() ? smelteryMagic() : smelteryHeat(), damage)) {
            // its fine if we don't fill it all, leftover fluid is just lost
            tank.fill(fluid, FluidAction.EXECUTE);
            melted = true;
          }
        }
      }
    }
    return melted;
  }
}
