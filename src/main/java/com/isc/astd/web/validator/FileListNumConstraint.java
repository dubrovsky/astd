package com.isc.astd.web.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FileListNumValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileListNumConstraint {
    String message() default "Лист с таким номером уже существует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
