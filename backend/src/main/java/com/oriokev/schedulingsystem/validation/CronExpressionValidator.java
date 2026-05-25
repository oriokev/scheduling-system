package com.oriokev.schedulingsystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.quartz.CronExpression;

public class CronExpressionValidator implements ConstraintValidator<ValidCron, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // @NotBlank handles that case
        return CronExpression.isValidExpression(value);
    }
}
