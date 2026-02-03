package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.ItemStackLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

import java.util.List;

/**
 * Module that makes a tool edible
 * @param representativeItem  Stack used for mods like Diet to know what we ate.
 */
public record EdibleModule(ItemStack representativeItem, LevelingInt duration, LevelingInt durabilityUsage, LevelingValue chance, ModifierCondition<IToolStackView> condition) implements ModifierModule, GeneralInteractionModifierHook, UsingToolModifierHook, OnAttackedModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EdibleModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.ON_ATTACKED);
  public static final RecordLoadable<EdibleModule> LOADER = RecordLoadable.create(
    ItemStackLoadable.OPTIONAL_ITEM_NBT.defaultField("representative_item", ItemStack.EMPTY, EdibleModule::representativeItem),
    LevelingInt.LOADABLE.requiredField("duration", EdibleModule::duration),
    LevelingInt.LOADABLE.requiredField("durability_usage", EdibleModule::durabilityUsage),
    LevelingValue.LOADABLE.requiredField("counter_chance", EdibleModule::chance),
    ModifierCondition.TOOL_FIELD, EdibleModule::new);
  /** Tool stat for the amount of hunger restored upon eating this. */
  public static final FloatToolStat HUNGER = new FloatToolStat(new ToolStatId(TConstruct.MOD_ID, "hunger"), 0xFFF0A8A4, 0, 0, 200, ItemPredicate.or(ItemPredicate.tag(TinkerTags.Items.INTERACTABLE_CHARGE), ItemPredicate.tag(TinkerTags.Items.ARMOR)));
  /** Tool stat for the amount of saturation restored upon eating this. */
  public static final FloatToolStat SATURATION = new FloatToolStat(new ToolStatId(TConstruct.MOD_ID, "saturation"), 0xFFF0A8A4, 0, 0, 200, ItemPredicate.or(ItemPredicate.tag(TinkerTags.Items.INTERACTABLE_CHARGE), ItemPredicate.tag(TinkerTags.Items.ARMOR)));

  public EdibleModule(ItemLike representativeItem, LevelingInt duration, LevelingInt durabilityUsage, LevelingValue chance) {
    this(new ItemStack(representativeItem), duration, durabilityUsage, chance, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<EdibleModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken() && player.canEat(false) && tool.getStats().getInt(HUNGER) > 0) {
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.EAT;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return this.duration.compute(modifier.getEffectiveLevel());
  }

  /** Takes a nibble of the tool */
  private void eat(IToolStackView tool, ModifierEntry modifier, Player player, ItemStack representativeItem) {
    StatsNBT stats = tool.getStats();
    int hunger = stats.getInt(HUNGER);
    if (hunger > 0) {
      Level world = player.level();
      float saturation = stats.get(SATURATION);
      player.getFoodData().eat(hunger, saturation);
      ModifierUtil.foodConsumer.onConsume(player, representativeItem, hunger, saturation);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
      world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

      // 15 damage for a bite per level, does not process reinforced/overslime, your teeth are tough
      int damage = this.durabilityUsage.compute(modifier.getEffectiveLevel());
      if (damage > 0 && ToolDamageUtil.directDamage(tool, damage, player, player.getUseItem())) {
        player.broadcastBreakEvent(player.getUsedItemHand());
      }
    }
  }

  /** Gets the item for particles and Diet */
  private ItemStack getRepresentativeItem(LivingEntity entity) {
    return !representativeItem.isEmpty() ? representativeItem : entity.getUseItem();
  }

  /** Plays effects for eating */
  private static void eatEffects(LivingEntity entity, ItemStack representativeItem, int amount) {
    entity.spawnItemParticles(representativeItem, amount);
    RandomSource random = entity.getRandom();
    entity.playSound(SoundEvents.GENERIC_EAT, 0.5f + 0.5f * random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
  }

  @Override
  public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    if (modifier != activeModifier && condition.matches(tool, modifier) && tool.getStats().getInt(HUNGER) > 0) {
      int useTime = useDuration - timeLeft;

      // if we reached the end, finish drinking; don't have to release the current use
      int duration = getUseDuration(tool, modifier);
      if (useTime == duration) {
        if (entity instanceof Player player && player.canEat(false)) {
          ItemStack representativeItem = getRepresentativeItem(entity);
          eatEffects(entity, representativeItem, 16);
          if (!entity.level().isClientSide) {
            eat(tool, modifier, player, representativeItem);
          }
        }
      }
      // if we have not finished drinking, and we can drink, play effects
      else if (useTime < duration && useTime % 4 == 0 && entity instanceof Player player && player.canEat(false)) {
        eatEffects(entity, representativeItem, 5);
      }
    }
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    if (!tool.isBroken() && useDuration - timeLeft == getUseDuration(tool, modifier) && condition.matches(tool, modifier) && tool.getStats().getInt(HUNGER) > 0 && entity instanceof Player player && player.canEat(false)) {
      ItemStack representativeItem = getRepresentativeItem(entity);
      if (modifier != activeModifier) {
        eatEffects(entity, representativeItem, 5);
      }
      if (!entity.level().isClientSide) {
        eat(tool, modifier, player, representativeItem);
      }
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (!tool.isBroken() && tool.hasTag(TinkerTags.Items.ARMOR) && condition.matches(tool, modifier) && tool.getStats().getInt(HUNGER) > 0) {
      LivingEntity entity = context.getEntity();
      float level = CounterModule.getLevel(tool, modifier, slotType, entity);
      if (context.getLevel().random.nextFloat() < chance.compute(level) && entity instanceof Player player && player.canEat(false)) {
        eat(tool, modifier, player, !representativeItem.isEmpty() ? representativeItem : entity.getItemBySlot(slotType));
      }
    }
  }
}
