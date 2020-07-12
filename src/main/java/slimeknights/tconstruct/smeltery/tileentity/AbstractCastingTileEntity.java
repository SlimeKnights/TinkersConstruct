package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.smeltery.CastingFluidHandler;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.recipe.TileCastingWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractCastingTileEntity extends TableTileEntity implements ITickableTileEntity, ISidedInventory, FluidUpdatePacket.IFluidPacketReceiver {
  // slots
  public static final int INPUT = 0;
  public static final int OUTPUT = 1;
  // NBT
  private static final String TAG_TANK = "tank";
  private static final String TAG_TIMER = "timer";
  private static final String TAG_RECIPE = "recipe";

  @Getter
  private final FluidTankAnimated tank = new FluidTankAnimated(0, this);
  private final LazyOptional<CastingFluidHandler> holder = LazyOptional.of(() -> new CastingFluidHandler(this, tank));
  private final TileCastingWrapper crafting;
  private final IRecipeType<AbstractCastingRecipe> recipeType;

  /** Current recipe progress */
  private int timer;
  /** Current in progress recipe */
  private AbstractCastingRecipe recipe;
  /** Name of the current recipe, fetched from NBT. Used since NBT is read before recipe manager access */
  private ResourceLocation recipeName;
  /** Cache recipe to reduce time during recipe lookups. Not saved to NBT */
  private AbstractCastingRecipe lastRecipe;

  protected AbstractCastingTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<AbstractCastingRecipe> recipeType) {
    super(tileEntityTypeIn, "gui.tconstruct.casting", 2, 1);
    this.itemHandler = new SidedInvWrapper(this, Direction.DOWN);
    this.recipeType = recipeType;
    this.crafting = new TileCastingWrapper(this, Fluids.EMPTY);
  }

  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
      return holder.cast();
    return super.getCapability(capability, facing);
  }

  /**
   * Called from {@link slimeknights.tconstruct.smeltery.block.AbstractCastingBlock#onBlockActivated(BlockState, World, BlockPos, PlayerEntity, Hand, BlockRayTraceResult)}
   * @param player Player activating the block.
   */
  public void interact(PlayerEntity player) {
    if (world == null || world.isRemote) {
      return;
    }
    // can't interact if liquid inside
    if (tank.getFluidAmount() > 0) {
      return;
    }

    // completely empty -> insert current item into input
    if (!isStackInSlot(0) && !isStackInSlot(1)) {
      ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
      setInventorySlotContents(INPUT, stack);
    }
    // take item out
    else {
      // take out stack 1 if something is there, 0 otherwise
      int slot = isStackInSlot(OUTPUT) ? OUTPUT : INPUT;

      // Additional info: Only 1 item can be put into the casting block usually, however recipes
      // can have ItemStacks with stacksize > 1 as output
      // we therefore spill the whole contents on extraction.
      ItemStack stack = getStackInSlot(slot);
      //if (slot == OUTPUT) {
        // fire player smelt event?
      //}
      ItemHandlerHelper.giveItemToPlayer(player, stack);
      setInventorySlotContents(slot, ItemStack.EMPTY);

      // send a block update for the comparator, needs to be done after the stack is removed
      if (slot == OUTPUT) {
        world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      }
    }
  }
  
  @Override
  @Nonnull
  public int[] getSlotsForFace(Direction side) {
    return new int[]{INPUT, OUTPUT};
  }

  @Override
  public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
    return index == INPUT && !isStackInSlot(OUTPUT);
  }

  @Override
  public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
    return index == OUTPUT;
  }

  @Override
  public void tick() {
    // no recipe
    if (world == null || recipe == null) {
      return;
    }
    // fully filled
    if (tank.getFluidAmount() == tank.getCapacity() && !tank.getFluid().isEmpty()) {
      timer++;
      if (!world.isRemote && timer >= recipe.getCoolingTime()) {
        ItemStack output = recipe.getCraftingResult(crafting);
        if (recipe.switchSlots()) {
          if (!recipe.isConsumed()) {
            setInventorySlotContents(OUTPUT, getStackInSlot(INPUT));
          }
          setInventorySlotContents(INPUT, output);
        }
        else {
          if (recipe.isConsumed()) {
            setInventorySlotContents(INPUT, ItemStack.EMPTY);
          }
          setInventorySlotContents(OUTPUT, output);
        }
        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, 0.07f, 4f);

        reset();

        world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      }
      else if (world.rand.nextFloat() > 0.9f) {
        world.addParticle(ParticleTypes.SMOKE, pos.getX() + world.rand.nextDouble(), pos.getY() + 1.1d, pos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  @Nullable
  private AbstractCastingRecipe findRecipe() {
    if (world == null) {
      return null;
    }
    if (this.lastRecipe != null && this.lastRecipe.matches(crafting, world)) {
      return this.lastRecipe;
    }
    AbstractCastingRecipe castingRecipe = world.getRecipeManager().getRecipe(this.recipeType, crafting, world).orElse(null);
    if (castingRecipe != null) {
      this.lastRecipe = castingRecipe;
    }
    return castingRecipe;
  }

  /**
   * Called from CastingFluidHandler.fill()
   * @param fluid   Fluid used in casting
   * @param action  EXECUTE or SIMULATE
   * @return        Amount of fluid needed for recipe, used to resize the tank.
   */
  public int initNewCasting(Fluid fluid, IFluidHandler.FluidAction action) {
    if (this.recipe != null || this.recipeName != null) {
      return 0;
    }
    this.crafting.setFluid(fluid);

    AbstractCastingRecipe castingRecipe = findRecipe();
    if (castingRecipe != null) {
      if (action == IFluidHandler.FluidAction.EXECUTE) {
        this.recipe = castingRecipe;
      }
      return castingRecipe.getFluidAmount(crafting);
    }
    return 0;
  }

  /**
   * Resets the casting table recipe to the default empty state
   */
  public void reset() {
    timer = 0;
    recipe = null;
    tank.setCapacity(0);
    tank.setFluid(FluidStack.EMPTY);
    tank.setRenderOffset(0);
    crafting.setFluid(Fluids.EMPTY);

    if (world != null && !world.isRemote && world instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(getPos(), FluidStack.EMPTY), (ServerWorld) world, getPos());
    }
  }

  @Override
  public void updateFluidTo(FluidStack fluid) {
    if (fluid.isEmpty()) {
      reset();
      tank.setRenderOffset(0);
    } else {
      tank.setRenderOffset(tank.getRenderOffset() + fluid.getAmount() - tank.getFluidAmount());
      int capacity = initNewCasting(fluid.getFluid(), FluidAction.EXECUTE);
      if (capacity > 0) {
        tank.setCapacity(capacity);
      }
    }
    tank.setFluid(fluid);
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    // no GUI
    return null;
  }


  /* NBT */

  /**
   * Loads a recipe in from its name and updates the tank capacity
   * @param world  Nonnull world instance
   * @param name   Recipe name to load
   */
  private void loadRecipe(World world, ResourceLocation name) {
    // if the tank is empty, ignore old recipe
    FluidStack fluid = tank.getFluid();
    if(!fluid.isEmpty()) {
      // fetch recipe by name
      AbstractCastingRecipe recipe = RecipeUtil.getRecipe(world.getRecipeManager(), name, AbstractCastingRecipe.class).orElse(null);
      if(recipe != null) {
        // update capacity from recipe
        crafting.setFluid(fluid.getFluid());
        tank.setCapacity(recipe.getFluidAmount(crafting));
      }
    }
  }

  @Override
  public void setWorldAndPos(World world, BlockPos pos) {
    super.setWorldAndPos(world, pos);
    // if we have a recipe name, swap recipe name for recipe instance
    if (recipeName != null) {
      loadRecipe(world, recipeName);
      recipeName = null;
    }
  }

  @Override
  @Nonnull
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    tags.put(TAG_TANK, tank.writeToNBT(new CompoundNBT()));
    tags.putInt(TAG_TIMER, timer);
    if (recipe != null) {
      tags.putString(TAG_RECIPE, recipe.getId().toString());
    } else if (recipeName != null) {
      tags.putString(TAG_RECIPE, recipeName.toString());
    }
    return tags;
  }

  @Override
  public void read(CompoundNBT tags) {
    super.read(tags);
    tank.readFromNBT(tags.getCompound(TAG_TANK));
    timer = tags.getInt(TAG_TIMER);
    if (tags.contains(TAG_RECIPE, NBT.TAG_STRING)) {
      ResourceLocation name = new ResourceLocation(tags.getString(TAG_RECIPE));
      // if we have a world, fetch the recipe
      if (world != null) {
        loadRecipe(world, name);
      } else {
        // otherwise fetch the recipe when the world is set
        recipeName = name;
      }
    }
  }
}
