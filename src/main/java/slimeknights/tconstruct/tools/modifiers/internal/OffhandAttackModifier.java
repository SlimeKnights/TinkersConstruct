package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.OffhandCooldownTracker;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class OffhandAttackModifier extends SingleUseModifier {
  public static final ResourceLocation DUEL_WIELDING = Util.getResource("duel_wielding");
  private final int cooldownTime;
  public OffhandAttackModifier(int color, int cooldownTime) {
    super(color);
    // vanilla is 20 / attackSpeed, making it 25 / attackSpeed makes the offhand only 80% of the speed
    this.cooldownTime = cooldownTime;
    MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
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
    return hand == Hand.OFF_HAND && !player.getCooldownTracker().hasCooldown(tool.getItem()) && tool.getItem() instanceof IModifiableWeapon;
  }

  /** Called when an entity is right clicked, since we only get living entities in the normal hook */
  protected void onEntityInteract(EntityInteract event) {
    if (event.isCanceled()) {
      return;
    }
    ItemStack stack = event.getItemStack();
    if (!stack.isEmpty() && TinkerTags.Items.MELEE.contains(stack.getItem())) {
      ToolStack tool = ToolStack.from(stack);
      PlayerEntity player = event.getPlayer();
      if (!tool.isBroken() && tool.getModifierLevel(this) > 0 && canAttack(tool, player, event.getHand())) {
        Entity target = event.getTarget();
        if (!player.world.isRemote()) {
          int oldHurtResistance = target.hurtResistantTime;
          target.hurtResistantTime = 0;
          ToolAttackUtil.attackEntity((IModifiableWeapon)tool.getItem(), tool, player, Hand.OFF_HAND, target, ToolAttackUtil.getCooldownFunction(player, Hand.OFF_HAND), false);
          target.hurtResistantTime = oldHurtResistance;
        }
        OffhandCooldownTracker.applyCooldown(player, tool, cooldownTime);
        // we handle swinging the arm, return consume to prevent resetting cooldown
        player.swing(Hand.OFF_HAND, !player.world.isRemote());
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.CONSUME);
      }
    }
  }

  @Override
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand) {
    if (canAttack(tool, player, hand)) {
      // target done in onEntityUse, this is just for cooldown cause you missed
      player.swing(Hand.OFF_HAND, false);
      OffhandCooldownTracker.applyCooldown(player, tool, cooldownTime);
      // we handle swinging the arm, return consume to prevent resetting cooldown
      player.swing(Hand.OFF_HAND, !player.world.isRemote());
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }
}
