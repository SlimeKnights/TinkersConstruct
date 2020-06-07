package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
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
import slimeknights.tconstruct.library.fluid.IFluidTankUpdater;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.smeltery.CastingFluidHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.recipe.AbstractCastingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CastingTileEntity extends InventoryTileEntity implements ITickableTileEntity, ISidedInventory, FluidUpdatePacket.IFluidPacketReceiver {
  private int INPUT = 0;
  private int OUTPUT = 1;
  public FluidTankAnimated tank;
  public LazyOptional<CastingFluidHandler> holder = LazyOptional.of(() -> new CastingFluidHandler(this, tank));
  protected int timer;
  private List<? extends AbstractCastingRecipe> recipes = Lists.newArrayList();
  protected AbstractCastingRecipe recipe;
  protected final IRecipeType<? extends AbstractCastingRecipe> recipeType;

  public CastingTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<? extends AbstractCastingRecipe> recipeType) {
    super(tileEntityTypeIn, new TranslationTextComponent("casting"), 2, 1);
    this.tank = new FluidTankAnimated(0, this);
    this.itemHandler = new SidedInvWrapper(this, Direction.DOWN);
    this.recipeType = recipeType;
  }


  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
      return holder.cast();
    return super.getCapability(capability, facing);
  }

  public void interact(PlayerEntity player) {
    // can't interact if liquid inside
    if (tank.getFluidAmount() > 0) {
      System.out.println("can't interact if liquid inside");
      return;
    }

    // completely empty -> insert current item into input
    if (!isStackInSlot(0) && !isStackInSlot(1)) {
      System.out.println("completely empty -> insert current item into input");
      ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
      setInventorySlotContents(INPUT, stack);
    }
    // take item out
    else {
      System.out.println("take item out");
      // take out stack 1 if something is there, 0 otherwise
      int slot = isStackInSlot(OUTPUT) ? OUTPUT : INPUT;

      // Additional info: Only 1 item can be put into the casting block usually, however recipes
      // can have ItemStacks with stacksize > 1 as output
      // we therefore spill the whole contents on extraction.
      ItemStack stack = getStackInSlot(slot);
      if (slot == OUTPUT) {
        // fire player smelt event?
        System.out.println("fire player smelt event?");
      }
      ItemHandlerHelper.giveItemToPlayer(player, stack);
      setInventorySlotContents(slot, ItemStack.EMPTY);

      // send a block update for the comparator, needs to be done after the stack is removed
      if (slot == OUTPUT) {
        System.out.println("send a block update");
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
    return index == 1;
  }

  @Override
  public void tick() {
    // no recipe
//    System.out.println(recipe.getId().toString());
    if (recipe == null) {
      return;
    }
    // fully filled
    if (tank.getFluidAmount() == tank.getCapacity() && !tank.getFluid().isEmpty()) {
      timer++;
      System.out.println(String.format("timer=%d", timer));
      if (!getWorld().isRemote) {
        if (timer >= recipe.getCoolingTime()) {
          if (recipe.consumesCast()) {
            setInventorySlotContents(INPUT, ItemStack.EMPTY);
          }
          if (recipe.switchSlots()) {
            setInventorySlotContents(INPUT, recipe.getRecipeOutput());
          }
          else {
            setInventorySlotContents(OUTPUT, recipe.getRecipeOutput());
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

  protected AbstractCastingRecipe findRecipe(Fluid fluid) {

    recipes.clear();
    recipes = world.getRecipeManager().getRecipes((IRecipeType<AbstractCastingRecipe>) recipeType, this, world);
    for (AbstractCastingRecipe temp :recipes) {
      System.out.println(temp.getRecipeOutput().getItem().getRegistryName());
    }

    for (AbstractCastingRecipe candidate : recipes) {
      System.out.println(candidate.getRecipeOutput().getItem().getRegistryName());
      if (candidate.matches(fluid, this, world)) {
        recipe = candidate;
        System.out.println("candidate.matches(fluid, this, world)=true");
        System.out.println(recipe.toString());
      }
    }
//    CastingRecipe recipe = getWorld().getRecipeManager().getRecipe(TinkerSmeltery.castingRecipeType, this, this.world).orElse(null);
    return recipe;
  }

  public int initNewCasting(Fluid fluid, IFluidHandler.FluidAction action) {
    System.out.println(String.format("initNewCasting(%s, %s)", fluid.getRegistryName(), action.toString()));
    AbstractCastingRecipe recipe = findRecipe(fluid);
    System.out.println(recipe.getRecipeOutput().getItem().getRegistryName());
    if (recipe != null) {
      if (action == IFluidHandler.FluidAction.SIMULATE) {
        this.recipe = recipe;
        System.out.println(String.format("recipe=%s:%d::%s", recipe.getFluid().getRegistryName(), recipe.getFluidAmount(), recipe.getRecipeOutput().getItem().getRegistryName()));
      }
      return recipe.getFluidAmount();
    }
    return 0;
  }

  public void reset() {
    timer = 0;
    recipe = null;
    tank.setCapacity(0);
    tank.setFluid(FluidStack.EMPTY);
    tank.setRenderOffset(0);

    if (getWorld() != null && !getWorld().isRemote && getWorld() instanceof ServerWorld) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(getPos(), FluidStack.EMPTY), (ServerWorld) world, getPos());
    }
  }

  public FluidTankAnimated getInternalTank() {
    return tank;
  }

  public void updateFluidTo(FluidStack fluid) {
    int oldAmount = tank.getFluidAmount();
    tank.setFluid(fluid);

    if (fluid.isEmpty()) {
      reset();
      return;
    }
    else if (recipe == null) {
      findRecipe(fluid.getFluid());
      if (recipe != null) {
        tank.setCapacity(recipe.getFluidAmount());
      }
    }
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
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return null;
  }
}
