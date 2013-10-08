package org.bigraph.model.assistants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Fields annotated with <strong>BigmProperty</strong> are <i>property
 * names</i>.
 * @author alec
 */
@Target(ElementType.FIELD)
public @interface BigmProperty {
	Class<?> fired();
	Class<?> retrieved();
}
