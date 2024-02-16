/*
 * Copyright 2017-2024 the original author or authors.
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
package org.springframework.modulith.events.core;

import java.time.Clock;
import java.time.Instant;

import org.springframework.util.Assert;

/**
 * An event publication.
 *
 * @author Oliver Drotbohm
 * @author Björn Kieling
 * @author Dmitry Belyaev
 */
public interface TargetEventPublication extends Completable, org.springframework.modulith.events.EventPublication {

	/**
	 * Creates a {@link TargetEventPublication} for the given event an listener identifier using a default {@link Instant}.
	 * Prefer using {@link #of(Object, PublicationTargetIdentifier, Instant)} with a dedicated {@link Instant} obtained
	 * from a {@link Clock}.
	 *
	 * @param event must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 * @return will never be {@literal null}.
	 * @see #of(Object, PublicationTargetIdentifier, Instant)
	 */
	static TargetEventPublication of(Object event, PublicationTargetIdentifier id) {
		return new DefaultEventPublication(event, id, Instant.now());
	}

	/**
	 * Creates a {@link TargetEventPublication} for the given event an listener identifier and publication date.
	 *
	 * @param event must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 * @param publicationDate must not be {@literal null}.
	 * @return will never be {@literal null}.
	 */
	static TargetEventPublication of(Object event, PublicationTargetIdentifier id, Instant publicationDate) {
		return new DefaultEventPublication(event, id, publicationDate);
	}

	/**
	 * Returns the identifier of the target that the event is supposed to be published to.
	 *
	 * @return
	 */
	PublicationTargetIdentifier getTargetIdentifier();

	/**
	 * Returns whether the publication is identified by the given {@link PublicationTargetIdentifier}.
	 *
	 * @param identifier must not be {@literal null}.
	 * @return
	 */
	default boolean isIdentifiedBy(PublicationTargetIdentifier identifier) {

		Assert.notNull(identifier, "Identifier must not be null!");

		return this.getTargetIdentifier().equals(identifier);
	}
}
