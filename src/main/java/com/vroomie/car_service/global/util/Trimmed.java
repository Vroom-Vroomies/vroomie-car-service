package com.vroomie.car_service.global.util;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 커스텀 어노테이션
@Target(ElementType.FIELD) // 필드에만 적용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 어노테이션 정보 유지
@JacksonAnnotationsInside // 이 어노테이션이 Jackson 관련 어노테이션을 포함함을 명시
@JsonDeserialize(using = TrimmedStringDeserializer.class) // 이 어노테이션이 붙으면 TrimmedStringDeserializer를 사용
public @interface Trimmed {
}