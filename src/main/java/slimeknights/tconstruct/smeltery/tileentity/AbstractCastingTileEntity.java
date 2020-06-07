package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.smeltery.CastingFluidHandler;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;
import slimeknights.tconstruct.smeltery.recipe.TileCastingWrapper;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AbstractCastingTileEntity extends InventoryTileEntity implements ITickableTileEntity, ISidedInventory,
  FluidUpdatePacket.IFluidPacketReceiver {
  public static final int INPUT = 0;
  public static final int OUTPUT = 1;
  public FluidTankAnimated tank;
  public LazyOptional<CastingFluidHandler> holder = LazyOptional.of(() -> new CastingFluidHandler(this, tank));
  private final TileCastingWrapper crafting;
  protected int timer;
  protected AbstractCastingRecipe recipe;
  protected AbstractCastingRecipe lastRecipe;
  protected final IRecipeType<AbstractCastingRecipe> recipeType;

  public AbstractCastingTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<AbstractCastingRecipe> recipeType) {
    super(tileEntityTypeIn, new TranslationTextComponent("gui.tconstruct.casting"), 2, 1);
    this.tank = new FluidTankAnimated(0, this);
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
    if (world.isRemote) {
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
      if (slot == OUTPUT) {
        // fire player smelt event?
      }
      ItemHandlerHelper.giveItemToPlayer(player, stack);
      setInventorySlotContents(slot, ItemStack.EMPTY);

      // send a block update for the comparator, needs to be done after the stack is removed
      if (slot == OUTPUT) {
        this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
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
    if (recipe == null) {
      return;
    }
    // fully filled
    if (tank.getFluidAmount() == tank.getCapacity() && !tank.getFluid().isEmpty()) {
      timer++;
      if (!getWorld().isRemote) {
        if (timer >= recipe.getCoolingTime()) {
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
          getWorld().playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.AMBIENT, 0.07f, 4f);

          reset();

          getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
        }
      }
      else if (getWorld().rand.nextFloat() > 0.9f) {
        world.addParticle(ParticleTypes.SMOKE, pos.getX() + getWorld().rand.nextDouble(), pos.getY() + 1.1d, pos.getZ() + getWorld().rand.nextDouble(), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  @Nullable
  protected AbstractCastingRecipe findRecipe() {
    if (this.lastRecipe != null && this.lastRecipe.matches(crafting, world)) {
      return this.lastRecipe;
    }
    AbstractCastingRecipe castingRecipe = getWorld().getRecipeManager().getRecipe(this.recipeType, crafting, getWorld()).orElse(null);
    if (castingRecipe != null) {
      this.lastRecipe = castingRecipe;
    }
    return castingRecipe;
  }

  /** Called from CastingFluidHandler.fill()
   * @param fluid   Fluid used in casting
   * @param action  EXECUTE or SIMULATE
   * @return        Amount of fluid needed for recipe, used to resize the tank.
   */
  public int initNewCasting(Fluid fluid, IFluidHandler.FluidAction action) {
    this.crafting.setFluid(fluid);

    AbstractCastingRecipe castingRecipe = findRecipe();
    if (castingRecipe != null && this.recipe == null) {
      if (action == IFluidHandler.FluidAction.EXECUTE) {
        this.recipe = castingRecipe;
      }
      return castingRecipe.getFluid().getAmount();
    }
    return 0;
  }

  public void reset() {
    timer = 0;
    recipe = null;
    tank.setCapacity(0);
    tank.setFluid(FluidStack.EMPTY);
    tank.setRenderOffset(0);
    crafting.setFluid(Fluids.EMPTY);

    if (getWorld() != null && !getWorld().isRemote && getWorld() instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(getPos(), FluidStack.EMPTY), (ServerWorld) world, getPos());
    }
  }

  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    tank.setFluid(fluid);

    tank.setRenderOffset(tank.getRenderOffset() + tank.getFluidAmount() - oldAmount);
  }

  @Override
  @Nonnull
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    CompoundNBT tankTag = new CompoundNBT();
    tank.writeToNBT(tankTag);
    tags.put("tank", tankTag);
    tags.putInt("timer", timer);
    return tags;
  }

  @Override
  public void read(CompoundNBT tags) {
    super.read(tags);

    tank.readFromNBT(tags.getCompound("tank"));

    updateFluidTo(tank.getFluid());

    timer = tags.getInt("timer");
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
    // we sync slot changes to all clients around
    if (world != null && world instanceof ServerWorld && !world.isRemote && !ItemStack.areItemsEqual(itemstack, getStackInSlot(slot))) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(itemstack, slot, pos), (ServerWorld) world, pos);
    }
    super.setInventorySlotContents(slot, itemstack);

    if(world != null && world.isRemote) {
      Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, pos, null, null, 0);
    }
  }
}
