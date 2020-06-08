package slimeknights.tconstruct.library.registration;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.registration.object.FluidObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class FluidDeferredRegister extends RegisterWrapper<Fluid> {
  private static final Item.Properties BUCKET_PROPS = new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC);
  private final DeferredRegister<Block> blockRegister;
  private final DeferredRegister<Item> itemRegister;
  public FluidDeferredRegister(String modID) {
    super(ForgeRegistries.FLUIDS, modID);
    this.blockRegister = new DeferredRegister<>(ForgeRegistries.BLOCKS, modID);
    this.itemRegister = new DeferredRegister<>(ForgeRegistries.ITEMS, modID);
  }

  @Override
  public void register(IEventBus bus) {
    super.register(bus);
    blockRegister.register(bus);
    itemRegister.register(bus);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket
   * @param name     Fluid name
   * @param builder  Properties builder
   * @param still    Function to create still from the properties
   * @param flowing  Function to create flowing from the properties
   * @param block    Function to create block from the fluid supplier
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends ForgeFlowingFluid> FluidObject<F> register(final String name, Builder builder, Function<Properties,? extends F> still, Function<Properties,? extends F> flowing, Function<Supplier<? extends FlowingFluid>,? extends FlowingFluidBlock> block) {
    // have to create still and flowing later, they need props
    DelayedSupplier<F> stillSup = new DelayedSupplier<>();
    DelayedSupplier<F> flowingSup = new DelayedSupplier<>();
    // create flowing and bucket, they just need a still supplier
    RegistryObject<FlowingFluidBlock> blockObj = blockRegister.register(name + "_fluid", () -> block.apply(stillSup));
    RegistryObject<BucketItem> bucketObj = itemRegister.register(name + "_bucket", () -> new BucketItem(stillSup, BUCKET_PROPS));
    // create props with the suppliers
    Properties props = builder.build(stillSup, flowingSup, blockObj, bucketObj);
    // create fluids now that we have props
    stillSup.supplier = register.register(name, () -> still.apply(props));
    flowingSup.supplier = register.register("flowing_" + name, () -> flowing.apply(props));
    // return the final nice object
    return new FluidObject<>(stillSup.supplier, flowingSup.supplier, blockObj, bucketObj);
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default fluid block
   * @param name     Fluid name
   * @param builder  Properties builder
   * @param still    Function to create still from the properties
   * @param flowing  Function to create flowing from the properties
   * @param material Block material
   * @param <F>      Fluid type
   * @return  Fluid object
   */
  public <F extends ForgeFlowingFluid> FluidObject<F> register(final String name, FluidAttributes.Builder builder, final Function<Properties,? extends F> still, final Function<Properties,? extends F> flowing, Material material) {
    return register(name, new Builder(builder).explosionResistance(100f), still, flowing, (fluid) -> new FlowingFluidBlock(fluid, Block.Properties.create(material).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
  }

  /**
   * Registers a fluid with still, flowing, block, and bucket using the default Forge fluid
   * @param name     Fluid name
   * @param builder  Properties builder
   * @param material Block material
   * @return  Fluid object
   */
  public FluidObject<ForgeFlowingFluid> register(final String name, FluidAttributes.Builder builder, Material material) {
    return register(name, builder, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, material);
  }

  /**
   * This is a supplier wrapper that we can set the contained supplier at a later time.
   * Used since fluids are wrapped in a ton of self referencing suppliers, so we make the properties with a supplier that we fill later
   * @param <T>
   */
  private static class DelayedSupplier<T> implements Supplier<T> {
    public Supplier<T> supplier;

    @Override
    public T get() {
      return supplier.get();
    }
  }

  /**
   * Properties builder class, since the Forge one requires too many suppliers that we do not have access to
   */
  public static class Builder {
    private FluidAttributes.Builder attributes;
    private boolean canMultiply = false;
    private int slopeFindDistance = 4;
    private int levelDecreasePerBlock = 1;
    private float explosionResistance = 1;
    private int tickRate = 5;

    public Builder(FluidAttributes.Builder attributes) {
      this.attributes = attributes;
    }

    public Builder canMultiply() {
      canMultiply = true;
      return this;
    }

    public Builder slopeFindDistance(int slopeFindDistance) {
      this.slopeFindDistance = slopeFindDistance;
      return this;
    }

    public Builder levelDecreasePerBlock(int levelDecreasePerBlock) {
      this.levelDecreasePerBlock = levelDecreasePerBlock;
      return this;
    }

    public Builder explosionResistance(float explosionResistance) {
      this.explosionResistance = explosionResistance;
      return this;
    }

    public Builder tickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
    }

    /**
     * Builds Forge fluid properties from this builder
     * @param still    Still fluid supplier
     * @param flowing  Flowing supplier
     * @param block    Block supplier
     * @param bucket   Bucket supplier
     * @return  Forge fluid properties
     */
    public ForgeFlowingFluid.Properties build(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, Supplier<? extends FlowingFluidBlock> block, Supplier<? extends Item> bucket) {
      ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(still, flowing, this.attributes)
        .slopeFindDistance(this.slopeFindDistance)
        .levelDecreasePerBlock(this.levelDecreasePerBlock)
        .explosionResistance(this.explosionResistance)
        .tickRate(this.tickRate)
        .block(block)
        .bucket(bucket);
      if (this.canMultiply) {
        properties.canMultiply();
      }
      return properties;
    }
  }
}
