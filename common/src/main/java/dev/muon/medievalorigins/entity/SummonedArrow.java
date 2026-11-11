package dev.muon.medievalorigins.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SummonedArrow extends Arrow {
    private SummonedMob shooter;

    public SummonedArrow(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    public SummonedArrow(Level level, SummonedMob shooter) {
        super(level, shooter.getSelfAsMob(),
                shooter.getSelfAsMob().getProjectile(
                        shooter.getSelfAsMob().getItemInHand(ProjectileUtil.getWeaponHoldingHand(shooter.getSelfAsMob(), Items.BOW))),
                shooter.getSelfAsMob().getItemInHand(ProjectileUtil.getWeaponHoldingHand(shooter.getSelfAsMob(), Items.BOW)));
        this.shooter = shooter;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (shooter != null && shooter.getSelfAsMob().isAlliedTo(result.getEntity())) {
            discard();
            return;
        }
        super.onHitEntity(result);
    }
}