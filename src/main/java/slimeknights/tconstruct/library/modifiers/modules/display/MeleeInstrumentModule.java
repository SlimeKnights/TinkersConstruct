package slimeknights.tconstruct.library.modifiers.modules.display;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder.Stack;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Plays an instrument on successful melee hits.
 */
public record MeleeInstrumentModule(@Nullable MaterialId material, TagKey<Instrument> tag, IJsonPredicate<LivingEntity> attacker, IJsonPredicate<LivingEntity> target, ModifierCondition<IToolStackView> condition) implements ModifierModule, ConditionalModule<IToolStackView>, MonsterMeleeHitModifierHook.RedirectBefore, DamageDealtModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeleeInstrumentModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.DAMAGE_DEALT);
  public static final RecordLoadable<MeleeInstrumentModule> LOADER = RecordLoadable.create(
    MaterialId.PARSER.nullableField("instrument_material", MeleeInstrumentModule::material),
    TinkerLoadables.INSTRUMENT_TAGS.requiredField("instrument_tag", MeleeInstrumentModule::tag),
    LivingEntityPredicate.LOADER.defaultField("attacker", MeleeInstrumentModule::attacker),
    LivingEntityPredicate.LOADER.defaultField("target", MeleeInstrumentModule::target),
    ModifierCondition.TOOL_FIELD,
    MeleeInstrumentModule::new);

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Plays the sound for this attack */
  private void playSound(IToolStackView tool, LivingEntity attacker, @Nullable Player player, Entity target) {
    if (this.attacker.matches(attacker)) {
      // find the instrument to play
      Instrument instrument = null;
      if (this.material != null) {
        for (MaterialVariant material : tool.getMaterials()) {
          if (this.material.equals(material.getId())) {
            // need to convert the variant into an instrument, start by replacing the first . with a :
            String variant = material.getVariant().getVariant();
            int index = variant.indexOf('.');
            if (index != -1) {
              StringBuilder builder = new StringBuilder(variant);
              builder.setCharAt(index, ':');
              variant = builder.toString();
            }
            ResourceLocation key = ResourceLocation.tryParse(variant);
            if (key != null) {
              instrument = BuiltInRegistries.INSTRUMENT.get(key);
            }
            break;
          }
        }
      }
      // if we lacked a matching material, fetch a random instrument from the tag
      if (instrument == null) {
        instrument = BuiltInRegistries.INSTRUMENT.getTag(tag).flatMap(tag -> tag.getRandomElement(attacker.getRandom())).map(Holder::value).orElse(null);
      }
      // may still have no instrument if the tag was empty
      if (instrument != null) {
        float range = instrument.range() / 16f;
        Level level = attacker.level();

        level.playSound(null, target, instrument.soundEvent().value(), SoundSource.RECORDS, range, 1.0f);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, target.position(), GameEvent.Context.of(attacker));
      }
    }
  }

  @Override
  public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
    if (TinkerPredicate.matches(this.target, context.getLivingTarget())) {
      playSound(tool, context.getAttacker(), context.getPlayerAttacker(), context.getTarget());
    }
  }

  @Override
  public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (this.target.matches(target)) {
      LivingEntity attacker = context.getEntity();
      playSound(tool, attacker, ModifierUtil.asPlayer(attacker), target);
    }
  }


  /* Builder */

  public static Builder tag(TagKey<Instrument> tag) {
    return new Builder(tag);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  public static class Builder extends Stack<Builder> {
    private final TagKey<Instrument> tag;
    @Nullable
    private MaterialId material = null;
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;

    /** Builds the final module */
    public MeleeInstrumentModule build() {
      return new MeleeInstrumentModule(material, tag, attacker, target, condition);
    }
  }
}
