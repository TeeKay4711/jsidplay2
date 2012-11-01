package applet.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClass {
	/**
	 * Get key for bundle to be localized and displayed in the UI.
	 * 
	 * @return key to get a language dependent message
	 */
	String bundleKey();
}
