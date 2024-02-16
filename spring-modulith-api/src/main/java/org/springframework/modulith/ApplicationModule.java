/*
 * Copyright 2018-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.modulith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to customize information of a {@link Modulith} module.
 *
 * @author Oliver Drotbohm
 */
@Target({ ElementType.PACKAGE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationModule {

	public static final String OPEN_TOKEN = "¯\\_(ツ)_/¯";

	/**
	 * The human readable name of the module to be used for display and documentation purposes.
	 *
	 * @return will never be {@literal null}.
	 */
	String displayName() default "";

	/**
	 * List the names of modules that the module is allowed to depend on. Shared modules defined in
	 * {@link Modulith}/{@link Modulithic} will be allowed, too. Names listed are local ones, unless the application has
	 * configured {@link Modulithic#useFullyQualifiedModuleNames()} to {@literal true}. Explicit references to
	 * {@link NamedInterface}s need to be separated by a double colon {@code ::}, e.g. {@code module::API} if
	 * {@code module} is the logical module name and {@code API} is the name of the named interface.
	 * <p>
	 * Declaring an empty array will allow no dependencies to other modules. To not restrict the dependencies at all,
	 * leave the attribute at its default value.
	 *
	 * @return will never be {@literal null}.
	 * @see NamedInterface
	 */
	String[] allowedDependencies() default { OPEN_TOKEN };
}
