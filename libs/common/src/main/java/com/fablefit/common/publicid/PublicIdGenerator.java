package com.fablefit.common.publicid;

import java.util.UUID;

public final class PublicIdGenerator {

    private PublicIdGenerator() {}

    public static String generate(Class<?> entity) {
        PublicIdPrefix prefixAnnotation = entity.getAnnotation(PublicIdPrefix.class);
        String prefix = prefixAnnotation != null ? prefixAnnotation.value() : entity.getSimpleName().toLowerCase();
        return prefix + "_" + UUID.randomUUID().toString();
    }
}
