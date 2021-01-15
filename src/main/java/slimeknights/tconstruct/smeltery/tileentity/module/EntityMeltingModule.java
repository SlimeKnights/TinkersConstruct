package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;

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
  public static final DamageSource SMELTERY_DAMAGE = new DamageSource("smeltery").setFireDamage();

  private final MantleTileEntity parent;
  private final IFluidHandler tank;
  /** Supplier that returns true if the tank has space */
  private final BooleanSupplier canMeltEntities;
  /** Function that tries to insert an item into the inventory */
  private final Function<ItemStack, ItemStack> insertFunction;
  /** Function that returns the bounds to check for entities */
  private final Supplier<AxisAlignedBB> bounds;

  @Nullable
  private EntityMeltingRecipe lastRecipe;

  /** Gets a nonnull world instance from the parent */
  private World getWorld() {
    return Objects.requireNonNull(parent.getWorld(), "Parent tile entity has null world");
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
    for (EntityMeltingRecipe recipe : RecipeHelper.getRecipes(getWorld().getRecipeManager(), RecipeTypes.ENTITY_MELTING, EntityMeltingRecipe.class)) {
      if (recipe.matches(type)) {
        lastRecipe = recipe;
        return recipe;
      }
    }
    return null;
  }

  /**
   * Gets the default fluid result
   * @return  Default fluid
   */
  public static FluidStack getDefaultFluid() {
    // TODO: consider a way to put this in a recipe
    return new FluidStack(TinkerFluids.blood.get(), 20);
  }

  /**
   * Interacts with entities in the structure
   */
  public void interactWithEntities() {
    AxisAlignedBB boundingBox = bounds.get();
    if (boundingBox == null) {
      return;
    }

    Boolean canMelt = null;
    for (Entity entity : getWorld().getEntitiesWithinAABB(Entity.class, boundingBox)) {
      // items are placed inside the smeltery
      if (entity instanceof ItemEntity) {
        ItemStack stack = insertFunction.apply(((ItemEntity) entity).getItem());
        if (stack.isEmpty()) {
          // picked up whole stack
          entity.remove();
        }
      }

      // only can melt living
      else if (entity instanceof LivingEntity) {
        // only fetch boolean once, its not the fastest as it tries to consume fuel
        if (canMelt == null) canMelt = canMeltEntities.getAsBoolean();

        // ensure we are allowed to melt
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
          if (entity.attackEntityFrom(SMELTERY_DAMAGE, damage)) {
            // its fine if we don't fill it all, leftover fluid is just lost
            tank.fill(fluid, FluidAction.EXECUTE);
          }
        } else {
          break;
        }
      }
    }
  }
}
