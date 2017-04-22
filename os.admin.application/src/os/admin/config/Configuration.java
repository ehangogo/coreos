package os.admin.config;

public @interface Configuration {
	String route() default "localhost";
	String descriptor() default "admin";
}