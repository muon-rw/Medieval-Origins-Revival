package dev.muon.medievalorigins.platform;

import java.util.ServiceLoader;

public final class Services {
    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
    public static final Registrar REGISTRAR = load(Registrar.class);

    private Services() {
    }

    private static <T> T load(Class<T> service) {
        return ServiceLoader.load(service, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No implementation found for " + service.getName()));
    }
}
