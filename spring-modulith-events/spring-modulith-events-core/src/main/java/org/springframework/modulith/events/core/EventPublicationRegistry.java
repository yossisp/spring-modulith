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

import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.context.ApplicationListener;

/**
 * A registry to capture event publications to {@link ApplicationListener}s. Allows to register those publications, mark
 * them as completed and lookup incomplete publications.
 *
 * @author Oliver Drotbohm
 * @author Björn Kieling
 * @author Dmitry Belyaev
 */
public interface EventPublicationRegistry {

	/**
	 * Stores {@link TargetEventPublication}s for the given event and {@link ApplicationListener}s.
	 *
	 * @param event must not be {@literal null}.
	 * @param listeners must not be {@literal null}.
	 */
	Collection<TargetEventPublication> store(Object event, Stream<PublicationTargetIdentifier> listeners);

	/**
	 * Returns all {@link TargetEventPublication}s that have not been completed yet.
	 *
	 * @return will never be {@literal null}.
	 */
	Collection<TargetEventPublication> findIncompletePublications();

	/**
	 * Returns all {@link TargetEventPublication}s that have not been completed yet and have been published before the
	 * given duration in relation to "now".
	 *
	 * @param duration must not be {@literal null}.
	 * @return will never be {@literal null}.
	 * @since 1.1
	 */
	Collection<TargetEventPublication> findIncompletePublicationsOlderThan(Duration duration);

	/**
	 * Marks the publication for the given event and {@link PublicationTargetIdentifier} as completed.
	 *
	 * @param event must not be {@literal null}.
	 * @param targetIdentifier must not be {@literal null}.
	 */
	void markCompleted(Object event, PublicationTargetIdentifier targetIdentifier);

	/**
	 * Deletes all completed {@link TargetEventPublication}s that have been completed before the given {@link Duration}.
	 *
	 * @param duration must not be {@literal null}.
	 */
	void deleteCompletedPublicationsOlderThan(Duration duration);
}
