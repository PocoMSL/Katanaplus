
package com.example.katswords;

import net.minecraftforge.fml.common.Mod;

@Mod("katswords")
public class KatSwords {
    public KatSwords() {
        ModItems.register();
        ModEvents.register();
    }
}
