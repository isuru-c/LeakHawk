package core.classifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sugeesh Chandraweera
 * @since 1.0
 */

/**
 * This annotation will be used in the every Classifier added to the content classifier.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentPattern {

    String patternName();

    String filePath();

}
