package slimeknights.tconstruct.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public abstract class ArmoredSlimeEntity extends Slime {
  public ArmoredSlimeEntity(EntityType<? extends Slime> type, Level world) {
    super(type, world);
    if (!world.isClientSide) {
      tryAddAttribute(Attributes.ARMOR, new AttributeModifier("tconstruct.small_armor_bonus", 3, Operation.MULTIPLY_TOTAL));
      tryAddAttribute(Attributes.ARMOR_TOUGHNESS, new AttributeModifier("tconstruct.small_toughness_bonus", 3, Operation.MULTIPLY_TOTAL));
      tryAddAttribute(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("tconstruct.small_resistence_bonus", 3, Operation.MULTIPLY_TOTAL));
    }
  }

  /** Adds an attribute if possible */
  private void tryAddAttribute(Attribute attribute, AttributeModifier modifier) {
    AttributeInstance instance = getAttribute(attribute);
    if (instance != null) {
      instance.addTransientModifier(modifier);
    }
  }

  @Nullable
  @Override
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance difficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
    SpawnGroupData spawnData = super.finalizeSpawn(pLevel, difficulty, pReason, pSpawnData, pDataTag);
    this.setCanPickUpLoot(this.random.nextFloat() < (0.55f * difficulty.getSpecialMultiplier()));

    this.populateDefaultEquipmentSlots(random, difficulty);

    // pumpkins on halloween
    if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
      LocalDate localdate = LocalDate.now();
      if (localdate.get(ChronoField.MONTH_OF_YEAR) == 10 && localdate.get(ChronoField.DAY_OF_MONTH) == 31 && this.random.nextFloat() < 0.25F) {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
        this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
      }
    }

    return spawnData;
  }

  @Override
  protected abstract void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty);

  @Override
  protected void populateDefaultEquipmentEnchantments(RandomSource random, DifficultyInstance difficulty) {
    // no-op, unused
  }

  @Override
  public boolean canHoldItem(ItemStack stack) {
    // only pick up items that go in the head slot, don't have a renderer for other slots
    return getEquipmentSlotForItem(stack) == EquipmentSlot.HEAD;
  }

  @Override
  protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    ItemStack stack = this.getItemBySlot(EquipmentSlot.HEAD);
    float slotChance = this.getEquipmentDropChance(EquipmentSlot.HEAD);
    // items do not always drop if a large slime, increases chance of inheritance
    // small slimes always drop, no losing gear
    if (slotChance > 0.25f && getSize() > 1) {
      slotChance = 0.25f;
    }
    boolean alwaysDrop = slotChance > 1.0F;
    if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack) && (recentlyHit || alwaysDrop) && (this.random.nextFloat() - (looting * 0.01f)) < slotChance) {
      if (!alwaysDrop && stack.isDamageableItem()) {
        int max = stack.getMaxDamage();
        stack.setDamageValue(max - this.random.nextInt(1 + this.random.nextInt(Math.max(max - 3, 1))));
      }
      this.spawnAtLocation(stack);
      this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
    }
  }

  @SuppressWarnings("IntegerDivisionInFloatingPointContext")
  @Override
  public void remove(Entity.RemovalReason reason) {
    // on death, split into multiple slimes, and let them inherit armor if it did not drop
    int size = this.getSize();
    Level level = level();
    if (!level.isClientSide && size > 1 && this.isDeadOrDying()) {
      Component name = this.getCustomName();
      boolean noAi = this.isNoAi();
      boolean invulnerable = this.isInvulnerable();
      float offset = size / 4.0F;
      int newSize = size / 2;
      int count = 2 + this.random.nextInt(3);
      // determine which child will receive the helmet
      ItemStack helmet = getItemBySlot(EquipmentSlot.HEAD);
      int helmetIndex = -1;
      if (!helmet.isEmpty()) {
        helmetIndex = this.random.nextInt(count);
      }

      // spawn all children
      float dropChance = getEquipmentDropChance(EquipmentSlot.HEAD);
      for(int i = 0; i < count; ++i) {
        float x = ((i % 2) - 0.5F) * offset;
        float z = ((i / 2) - 0.5F) * offset;
        Slime slime = this.getType().create(level);
        assert slime != null;
        if (this.isPersistenceRequired()) {
          slime.setPersistenceRequired();
        }
        slime.setCustomName(name);
        slime.setNoAi(noAi);
        slime.setInvulnerable(invulnerable);
        slime.setSize(newSize, true);
        if (i == helmetIndex) {
          slime.setItemSlot(EquipmentSlot.HEAD, helmet.copy());
          setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        } else if (dropChance < 1 && random.nextFloat() < 0.25) {
          slime.setItemSlot(EquipmentSlot.HEAD, helmet.copy());
        }
        slime.moveTo(this.getX() + x, this.getY() + 0.5D, this.getZ() + z, this.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(slime);
      }
    }

    // calling supper does the split reason again, but we need to transfer armor
    this.setRemoved(reason);
    if (reason == Entity.RemovalReason.KILLED) {
      this.gameEvent(GameEvent.ENTITY_DIE);
    }
    this.invalidateCaps();
  }
}
