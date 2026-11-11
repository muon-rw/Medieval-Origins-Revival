package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.entity.SummonedMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin extends TargetGoal {
    @Shadow protected LivingEntity target;

    protected NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee, boolean mustReach) {
        super(mob, mustSee, mustReach);
    }

    @Inject(method = "findTarget", at = @At("RETURN"))
    private void onFindTarget(CallbackInfo ci) {
        if (this.mob instanceof TamableAnimal tamable && tamable.isTame() && target != null) {
            if (target instanceof SummonedMob) {
                this.target = null;
            }
        }
    }
}