package dev.muon.medievalorigins.power.factory;

import dev.muon.medievalorigins.power.OwnerAttributeTransferPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;

public class OwnerAttributeTransferPowerFactory extends PowerFactory<OwnerAttributeTransferPower> {

    public OwnerAttributeTransferPowerFactory() {
        super(OwnerAttributeTransferPower.CODEC);
    }
}