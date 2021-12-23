package slimeknights.tconstruct.tools.modifiers.traits.skull;

import lombok.Getter;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class ChrysophiliteModifier extends SingleUseModifier {
  public static final ComputableDataKey<TotalGold> TOTAL_GOLD = TConstruct.createKey("chrysophilite", TotalGold::new);
  public ChrysophiliteModifier() {
    super(0xE8A074);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingDropsEvent.class, ChrysophiliteModifier::onLivingDrops);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    // adding a helmet? activate bonus
    if (context.getChangedSlot() == EquipmentSlotType.HEAD) {
      context.getTinkerData().ifPresent(data -> {
        TotalGold gold = data.get(TOTAL_GOLD);
        if (gold == null) {
          data.computeIfAbsent(TOTAL_GOLD).initialize(context);
        } else {
          gold.setGold(EquipmentSlotType.HEAD, tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL));
        }
      });
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlotType.HEAD) {
      IModifierToolStack newTool = context.getReplacementTool();
      // when replacing with a helmet that lacks this modifier, remove bonus
      if (newTool == null || newTool.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.remove(TOTAL_GOLD));
      }
    }
  }

  @Override
  public void onEquipmentChange(IModifierToolStack tool, int level, EquipmentChangeContext context, EquipmentSlotType slotType) {
    // adding a helmet? activate bonus
    EquipmentSlotType changed = context.getChangedSlot();
    if (slotType == EquipmentSlotType.HEAD && changed.getSlotType() == Group.ARMOR) {
      boolean hasGold = ChrysophiliteModifier.hasGold(context, changed);
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TOTAL_GOLD).setGold(changed, hasGold));
    }
  }

  /** Checks if the entity has gold in the given slot */
  public static boolean hasGold(EquipmentChangeContext context, EquipmentSlotType slotType) {
    IModifierToolStack tool = context.getToolInSlot(slotType);
    if (tool != null) {
      return tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL);
    } else {
      LivingEntity living = context.getEntity();
      return living.getItemStackFromSlot(slotType).makesPiglinsNeutral(living);
    }
  }

  /** Gets the level of the modifier on an entity */
  public static int getTotalGold(@Nullable Entity entity) {
    return Optional.ofNullable(entity)
                   .flatMap(e -> e.getCapability(TinkerDataCapability.CAPABILITY).resolve())
                   .map(data -> data.get(ChrysophiliteModifier.TOTAL_GOLD))
                   .map(TotalGold::getTotalGold)
                   .orElse(0);
  }

  /** Causes more gold armor to drop */
  private static void onLivingDrops(LivingDropsEvent event) {
    DamageSource source = event.getSource();
    if (source != null) {
      int gold = getTotalGold(source.getTrueSource());
      if (gold > 0) {
        float extraChance = 0.04f * gold;
        LivingEntity target = event.getEntityLiving();
        // check each slot for gold
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
          ItemStack stack = target.getItemStackFromSlot(slot);
          Random random = target.getRNG();
          // if the stack is gold, and it drops, we get it
          // don't have to worry about checking if it already dropped, the stacks are removed on drop
          if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack) && stack.makesPiglinsNeutral(target) && random.nextFloat() < extraChance) {
            // mobs damage items, its kinda weird
            if (stack.isDamageable()) {
              stack.setDamage(stack.getMaxDamage() - random.nextInt(1 + random.nextInt(Math.max(stack.getMaxDamage() - 3, 1))));
            }
            // remove stack to prevent further drops
            event.getDrops().add(target.entityDropItem(stack));
            target.setItemStackToSlot(slot, ItemStack.EMPTY);
          }
        }
      }
    }
  }

  /** Tracker to count how many slots contain gold */
  public static class TotalGold {
    private final boolean[] hasGold = new boolean[4];
    /** Gold value of the modifier, will be 1 for the modifier, and +1 for each golden armor piece */
    @Getter
    private int totalGold = 0;

    /**
     * Updates the status of gold in a slot on the entity
     * @param slotType  Slot to update
     * @param value     New value
     */
    protected boolean setGold(EquipmentSlotType slotType, boolean value) {
      if (slotType.getSlotType() == Group.ARMOR) {
        int index = slotType.getIndex();
        if (hasGold[index] != value) {
          hasGold[index] = value;
          if (value) {
            totalGold++;
          } else {
            totalGold--;
          }
          return true;
        }
      }
      return false;
    }

    /** Initializes the gold data */
    public void initialize(EquipmentChangeContext context) {
      totalGold = 1;
      for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
        boolean gold = hasGold(context, slotType);
        hasGold[slotType.getIndex()] = gold;
        if (gold) {
          totalGold++;
        }
      }
    }
  }
}
