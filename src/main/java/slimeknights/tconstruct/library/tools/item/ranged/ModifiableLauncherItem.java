package slimeknights.tconstruct.library.tools.item.ranged;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.item.ModifiableItemClientExtension;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerToolActions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook.KEY_DRAWTIME;

/** Base class for any items that launch projectiles */
public abstract class ModifiableLauncherItem extends ProjectileWeaponItem implements IModifiableDisplay {
  /** Persistent data key for the ammo being used on drawing back the bow. */
  public static final ResourceLocation KEY_DRAWBACK_AMMO = TConstruct.getResource("drawback_ammo");

  /** Tool definition for the given tool */
  @Getter
  private final ToolDefinition toolDefinition;

  /** Cached tool for rendering on UIs */
  private ItemStack toolForRendering;

  public ModifiableLauncherItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties);
    this.toolDefinition = toolDefinition;
  }


  /* Basic properties */

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return 1;
  }

  @Override
  public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
    return true;
  }


  /* Enchanting */

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
    return false;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
  }

  @Override
  public int getEnchantmentValue() {
    return 0;
  }

  @Override
  public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
    return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
  }

  @Override
  public Map<Enchantment,Integer> getAllEnchantments(ItemStack stack) {
    return EnchantmentModifierHook.getAllEnchantments(stack);
  }


  /* Loading */

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new ToolCapabilityProvider(stack);
  }

  @Override
  public void verifyTagAfterLoad(CompoundTag nbt) {
    ToolStack.verifyTag(this, nbt, getToolDefinition());
  }

  @Override
  public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
    ToolStack.ensureInitialized(stack, getToolDefinition());
  }


  /* Display */

  @Override
  public boolean isFoil(ItemStack stack) {
    // we use enchantments to handle some modifiers, so don't glow from them
    // however, if a modifier wants to glow let them
    return ModifierUtil.checkVolatileFlag(stack, SHINY);
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    return RarityModule.getRarity(stack);
  }


  /* Indestructible items */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return IndestructibleItemEntity.hasCustomEntity(stack);
  }

  @Nullable
  @Override
  public Entity createEntity(Level world, Entity original, ItemStack stack) {
    return IndestructibleItemEntity.createFrom(world, original, stack);
  }


  /* Damage/Durability */

  @Override
  public boolean isRepairable(ItemStack stack) {
    // handle in the tinker station
    return false;
  }

  @Override
  public boolean canBeDepleted() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    return ToolDamageUtil.getFakeMaxDamage(stack);
  }

  @Override
  public int getDamage(ItemStack stack) {
    if (!canBeDepleted()) {
      return 0;
    }
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    if (canBeDepleted()) {
      ToolStack.from(stack).setDamage(damage);
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    ToolDamageUtil.handleDamageItem(stack, amount, damager, onBroken);
    return 0;
  }


  /* Durability display */

  @Override
  public boolean isBarVisible(ItemStack pStack) {
    return DurabilityDisplayModifierHook.showDurabilityBar(pStack);
  }

  @Override
  public int getBarColor(ItemStack pStack) {
    return DurabilityDisplayModifierHook.getDurabilityRGB(pStack);
  }

  @Override
  public int getBarWidth(ItemStack pStack) {
    return DurabilityDisplayModifierHook.getDurabilityWidth(pStack);
  }


  /* Modifier interactions */

  @Override
  public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  @Override
  public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
    return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player);
  }

  @Override
  public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
    return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
  }


  /* Attacking */

  @Override
  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
    return EntityInteractionModifierHook.leftClickEntity(stack, player, target);
  }

  @Override
  public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
    return ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction);
  }

  @Override
  public Multimap<Attribute,AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
    return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt == null || slot.getType() != Type.HAND) {
      return ImmutableMultimap.of();
    }
    return getAttributeModifiers(ToolStack.from(stack), slot);
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return canPerformAction(stack, TinkerToolActions.SHIELD_DISABLE);
  }


  /* Arrow logic */

  @Override
  public int getUseDuration(ItemStack pStack) {
    return 72000;
  }

  @Override
  public abstract UseAnim getUseAnimation(ItemStack pStack);

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level pLevel, LivingEntity living) {
    ToolStack tool = ToolStack.from(stack);
    int duration = getUseDuration(stack);
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.TOOL_USING).beforeReleaseUsing(tool, entry, living, duration, 0, ModifierEntry.EMPTY);
    }
    return stack;
  }

  @Override
  public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
    onStopUsing(ToolStack.from(stack), entity, timeLeft);
  }

  /** Same as {@link #onStopUsing(ItemStack, LivingEntity, int)} but uses a tool. */
  protected void onStopUsing(IToolStackView tool, LivingEntity entity, int timeLeft) {
    UsingToolModifierHook.afterStopUsing(tool, entity, timeLeft);
    ModDataNBT data = tool.getPersistentData();
    data.remove(KEY_DRAWTIME);
    data.remove(KEY_DRAWBACK_AMMO);
  }

  @Override
  public void onUseTick(Level level, LivingEntity living, ItemStack bow, int chargeRemaining) {
    // play the sound at the end of loading as an indicator its loaded, texture is another indicator
    int duration = getUseDuration(bow);
    if (!level.isClientSide) {
      if (duration - chargeRemaining == ModifierUtil.getPersistentInt(bow, KEY_DRAWTIME, -1)) {
        level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.75F, 1.0F);
      }
    }
    ToolStack tool = ToolStack.from(bow);
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.TOOL_USING).onUsingTick(tool, entry, living, duration, chargeRemaining, ModifierEntry.EMPTY);
    }
  }


  /* Tooltips */

  @Override
  public Component getName(ItemStack stack) {
    return ToolNameHook.getName(getToolDefinition(), stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
  }

  @Override
  public int getDefaultTooltipHideFlags(ItemStack stack) {
    return TooltipUtil.getModifierHideFlags(getToolDefinition());
  }


  /* Display */

  @Override
  public ItemStack getRenderTool() {
    if (toolForRendering == null) {
      toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
    }
    return toolForRendering;
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(ModifiableItemClientExtension.INSTANCE);
  }


  /* Misc */

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return ModifiableItem.shouldCauseReequip(oldStack, newStack, slotChanged);
  }


  /* Harvest logic, mostly used by modifiers but technically would let you make a pickaxe bow */

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
  }

  @Override
  public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    return ToolHarvestLogic.mineBlock(stack, worldIn, state, pos, entityLiving);
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    return MiningSpeedToolHook.getDestroySpeed(stack, state);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
    return ToolHarvestLogic.handleBlockBreak(stack, pos, player);
  }


  /* Multishot helper */

  /** Gets the angle to fire the first arrow, each additional arrow offsets an additional 10 degrees */
  public static float getAngleStart(int count) {
    return -5 * (count - 1);
  }
}
