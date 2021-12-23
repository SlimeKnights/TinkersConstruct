package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OffhandAttackModifier extends SingleUseModifier {
  public static final ResourceLocation DUEL_WIELDING = TConstruct.getResource("duel_wielding");

  public OffhandAttackModifier(int color) {
    super(color);
  }

  @Override
  public int getPriority() {
    return 90;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return false;
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(DUEL_WIELDING, true);
  }

  /** If true, we can use the attack */
  protected boolean canAttack(IModifierToolStack tool, PlayerEntity player, Hand hand) {
    return hand == Hand.OFF_HAND && OffhandCooldownTracker.isAttackReady(player);
  }

  @Override
  public ActionResultType beforeEntityUse(IModifierToolStack tool, int level, PlayerEntity player, Entity target, Hand hand, EquipmentSlotType slotType) {
    if (canAttack(tool, player, hand)) {
      if (!player.world.isRemote()) {
        Item item = tool.getItem();
        IModifiableWeapon weapon = item instanceof IModifiableWeapon ? (IModifiableWeapon) item : IModifiableWeapon.DEFAULT;
        ToolAttackUtil.attackEntity(weapon, tool, player, Hand.OFF_HAND, target, ToolAttackUtil.getCooldownFunction(player, Hand.OFF_HAND), false, slotType);
      }
      OffhandCooldownTracker.applyCooldown(player, tool.getStats().getFloat(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, Hand.OFF_HAND, false);
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand, EquipmentSlotType slotType) {
    if (canAttack(tool, player, hand)) {
      // target done in onEntityInteract, this is just for cooldown cause you missed
      OffhandCooldownTracker.applyCooldown(player, tool.getStats().getFloat(ToolStats.ATTACK_SPEED), 20);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      OffhandCooldownTracker.swingHand(player, Hand.OFF_HAND, false);
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlotType.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setForceEnable(true));
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlotType.OFFHAND) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setForceEnable(false));
    }
  }
}
