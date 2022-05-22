package slimeknights.tconstruct.world.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EarthSlimeEntity extends ArmoredSlimeEntity {
  public EarthSlimeEntity(EntityType<? extends Slime> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  protected ResourceLocation getDefaultLootTable() {
    return this.getSize() == 1 ? EntityType.SLIME.getDefaultLootTable() : BuiltInLootTables.EMPTY;
  }

  @Override
  protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
    // earth slime spawns with vanilla armor, but unlike zombies turtle shells are fair game
    // vanilla logic but simplified down to just helmets
    float multiplier = difficulty.getSpecialMultiplier();
    if (this.random.nextFloat() < 0.15F * multiplier) {
      int armorQuality = this.random.nextInt(3);
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }

      ItemStack current = this.getItemBySlot(EquipmentSlot.HEAD);
      if (current.isEmpty()) {
        Item item = armorQuality == 5 ? Items.TURTLE_HELMET : getEquipmentForSlot(EquipmentSlot.HEAD, armorQuality);
        if (item != null) {
          this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(item));
          this.enchantSpawnedArmor(multiplier, EquipmentSlot.HEAD);
        }
      }
    }
  }
}
