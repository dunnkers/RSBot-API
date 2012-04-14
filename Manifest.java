package org.powerbot.game.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Manifest {
	String name();

	String description() default "";

	double version() default 1.0;

	String[] authors();

	String website() default "";

	boolean premium() default false;
}
