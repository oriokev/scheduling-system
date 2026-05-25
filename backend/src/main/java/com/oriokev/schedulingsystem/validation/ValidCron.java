package com.oriokev.schedulingsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CronExpressionValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCron {
    String message() default "Invalid Quartz cron expression";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
