package dev.stashy.extrasounds.mixin;

import com.mojang.authlib.GameProfile;
import dev.stashy.extrasounds.ExtraSounds;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class EffectMixin extends AbstractClientPlayerEntity
{
    int minDuration = 2;

    public EffectMixin(ClientWorld world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect)
    {
        var added = super.addStatusEffect(effect);
        if (added && effect.getDuration() > minDuration) ExtraSounds.playEffectSound(effect.getEffectType(), true);
        return added;
    }

    @Override
    public void applyStatusEffect(StatusEffectInstance effect)
    {
        if (effect.getDuration() > minDuration && !this.hasStatusEffect(effect.getEffectType()))
            ExtraSounds.playEffectSound(effect.getEffectType(), true);
        super.applyStatusEffect(effect);
    }

    @Override
    protected void onStatusEffectRemoved(StatusEffectInstance effect)
    {
        super.onStatusEffectRemoved(effect);
        ExtraSounds.playEffectSound(effect.getEffectType(), false);
    }

    @Inject(at = @At("HEAD"), method = "removeStatusEffectInternal")
    public void removeStatusEffectInternal(StatusEffect type, CallbackInfoReturnable<StatusEffectInstance> cir)
    {
        if (this.hasStatusEffect(type))
            ExtraSounds.playEffectSound(type, false);
    }
}