package slimeknights.tconstruct.library.tinkering;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

public class IndestructibleEntityItem extends ItemEntity {

  public IndestructibleEntityItem(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  public IndestructibleEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
    super(worldIn, x, y, z, stack);
  }

  @Override
  protected int getFireImmuneTicks() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected void dealFireDamage(int p_70081_1_) {
    // no fire damage for you
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
    // prevent any damage besides out of world
    return source.getDamageType().equals(DamageSource.OUT_OF_WORLD.damageType);
  }

  public static class EventHandler {

    public static final EventHandler instance = new EventHandler();

    private EventHandler() {
    }

    @SubscribeEvent
    public void onExpire(ItemExpireEvent event) {
      if(event.getEntityItem() instanceof IndestructibleEntityItem) {
        event.setCanceled(true);
      }
    }
  }
}
