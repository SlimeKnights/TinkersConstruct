package slimeknights.tconstruct.library.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.BowCore;

public abstract class TinkerToolEvent extends TinkerEvent {

  public final ItemStack itemStack;
  public final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }

  @Cancelable
  public static class ExtraBlockBreak extends TinkerToolEvent {

    public final EntityPlayer player;
    public final IBlockState state;

    public int width;
    public int height;
    public int depth;
    public int distance;

    public ExtraBlockBreak(ItemStack itemStack, EntityPlayer player, IBlockState state) {
      super(itemStack);
      this.player = player;
      this.state = state;
    }

    public static ExtraBlockBreak fireEvent(ItemStack itemStack, EntityPlayer player, IBlockState state, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player, state);
      event.width = width;
      event.height = height;
      event.depth = depth;
      event.distance = distance;

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }

  public static class OnRepair extends TinkerToolEvent {

    public final int amount;

    public OnRepair(ItemStack itemStack, int amount) {
      super(itemStack);
      this.amount = amount;
    }

    public static boolean fireEvent(ItemStack itemStack, int amount) {
      OnRepair event = new OnRepair(itemStack, amount);
      return !MinecraftForge.EVENT_BUS.post(event);
    }
  }

  public static class OnMattockHoe extends TinkerToolEvent {

    public final BlockPos pos;
    public final World world;
    public final EntityPlayer player;

    public OnMattockHoe(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      super(itemStack);
      this.player = player;
      this.pos = pos;
      this.world = world;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      MinecraftForge.EVENT_BUS.post(new OnMattockHoe(itemStack, player, world, pos));
    }
  }

  public static class OnShovelMakePath extends TinkerToolEvent {

    public final BlockPos pos;
    public final EntityPlayer player;
    public final World world;

    public OnShovelMakePath(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      super(itemStack);
      this.pos = pos;
      this.player = player;
      this.world = world;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      MinecraftForge.EVENT_BUS.post(new OnShovelMakePath(itemStack, player, world, pos));
    }
  }

  /**
   * Cancel event to indicate that the block is not harvestable.
   * Leave the Result on DEFAUT to tell the Scythe to harvest the block, if it's harvestable
   * Set the Result to ALLOW to tell the Scythe that the block is harvestable, even if the check says it's not
   * Set the Result to DENY to let the Scythe know you handled the stuff (= harvest was successful, but not handled by the scythe)
   */
  @HasResult
  @Cancelable
  public static class OnScytheHarvest extends TinkerToolEvent {
    public final BlockPos pos;
    public final EntityPlayer player;
    public final IBlockState blockState;
    public final World world;
    public final boolean harvestable;

    public OnScytheHarvest(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState, boolean harvestable) {
      super(itemStack);
      this.pos = pos;
      this.player = player;
      this.world = world;
      this.blockState = blockState;
      this.harvestable = harvestable;
    }

    public static OnScytheHarvest fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState, boolean harvestable) {
      OnScytheHarvest event = new OnScytheHarvest(itemStack, player, world, pos, blockState, harvestable);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }

  public static class OnBowShoot extends TinkerToolEvent {

    public final EntityPlayer entityPlayer;
    public final BowCore bowCore;
    public final ItemStack ammo;
    public final int useTime;
    private float baseInaccuracy;

    public int projectileCount = 1;
    public boolean consumeAmmoPerProjectile = true;
    public boolean consumeDurabilityPerProjectile = true;
    public float bonusInaccuracy = 0;

    public OnBowShoot(ItemStack bow, ItemStack ammo, EntityPlayer entityPlayer, int useTime, float baseInaccuracy) {
      super(bow);
      this.bowCore = (BowCore) bow.getItem();
      this.ammo = ammo;
      this.entityPlayer = entityPlayer;
      this.useTime = useTime;
      this.baseInaccuracy = baseInaccuracy;
    }

    public static OnBowShoot fireEvent(ItemStack bow, ItemStack ammo, EntityPlayer entityPlayer, int useTime, float baseInaccuracy) {
      OnBowShoot event = new OnBowShoot(bow, ammo, entityPlayer, useTime, baseInaccuracy);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }

    public void setProjectileCount(int projectileCount) {
      this.projectileCount = projectileCount;
    }

    public void setConsumeAmmoPerProjectile(boolean consumeAmmoPerProjectile) {
      this.consumeAmmoPerProjectile = consumeAmmoPerProjectile;
    }

    public void setConsumeDurabilityPerProjectile(boolean consumeDurabilityPerProjectile) {
      this.consumeDurabilityPerProjectile = consumeDurabilityPerProjectile;
    }

    public void setBonusInaccuracy(float bonusInaccuracy) {
      this.bonusInaccuracy = bonusInaccuracy;
    }

    public float getBaseInaccuracy() {
      return baseInaccuracy;
    }

    public void setBaseInaccuracy(float baseInaccuracy) {
      this.baseInaccuracy = baseInaccuracy;
    }
  }
}
