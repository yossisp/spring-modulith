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
package example.events;

import static org.assertj.core.api.Assertions.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.MapPropertySource;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.modulith.events.core.EventPublicationRegistry;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author Oliver Drotbohm
 */
class PersistentDomainEventIntegrationTest {

	@Test
	void exposesEventPublicationForFailedListener() throws Exception {

		try (var context = new AnnotationConfigApplicationContext()) {

			context.getEnvironment().getPropertySources().addFirst(
					new MapPropertySource("test", Map.of("spring.modulith.republish-outstanding-events-on-restart", "true")));
			context.register(ApplicationConfiguration.class, InfrastructureConfiguration.class);
			context.refresh();

			var registry = context.getBean(EventPublicationRegistry.class);

			try {

				context.getBean(Client.class).method();

				Thread.sleep(200);

				assertThat(context.getBean(NonTxEventListener.class).getInvoked()).isEqualTo(1);
				assertThat(context.getBean(FirstTxEventListener.class).getInvoked()).isEqualTo(1);
				assertThat(context.getBean(SecondTxEventListener.class).getInvoked()).isEqualTo(1);
				assertThat(context.getBean(ThirdTxEventListener.class).getInvoked()).isEqualTo(1);
				assertThat(context.getBean(FourthTxEventListener.class).getInvoked()).isEqualTo(1);

			} finally {

				assertThat(registry.findIncompletePublications()) //
						.extracting(TargetEventPublication::getTargetIdentifier) //
						.extracting(PublicationTargetIdentifier::getValue) //
						.hasSize(2) //
						.allSatisfy(id -> {
							assertThat(id)
									.matches(it -> //
							it.contains(SecondTxEventListener.class.getName()) //
									|| it.contains(FourthTxEventListener.class.getName()));
						});

			}

			// Resubmit failed publications
			var incompletePublications = context.getBean(IncompleteEventPublications.class);

			incompletePublications.resubmitIncompletePublications(__ -> true);

			Thread.sleep(200);

			assertThat(context.getBean(NonTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(FirstTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(SecondTxEventListener.class).getInvoked()).isEqualTo(2);
			assertThat(context.getBean(ThirdTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(FourthTxEventListener.class).getInvoked()).isEqualTo(2);

			// Still 2 uncompleted publications
			assertThat(registry.findIncompletePublications()).hasSize(2);

			incompletePublications.resubmitIncompletePublications(it -> {
				return TargetEventPublication.class.cast(it)
						.getTargetIdentifier()
						.getValue().contains(SecondTxEventListener.class.getName());
			});

			assertThat(context.getBean(NonTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(FirstTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(SecondTxEventListener.class).getInvoked()).isEqualTo(3);
			assertThat(context.getBean(ThirdTxEventListener.class).getInvoked()).isEqualTo(1);
			assertThat(context.getBean(FourthTxEventListener.class).getInvoked()).isEqualTo(2);
		}
	}

	@Configuration
	@EnableAsync
	@EnablePersistentDomainEvents
	static class ApplicationConfiguration {

		@Bean
		NonTxEventListener nonTx() {
			return new NonTxEventListener();
		}

		@Bean
		FirstTxEventListener first() {
			return new FirstTxEventListener();
		}

		@Bean
		SecondTxEventListener second() {
			return new SecondTxEventListener();
		}

		@Bean
		ThirdTxEventListener third() {
			return new ThirdTxEventListener();
		}

		@Bean
		FourthTxEventListener fourth() {
			return new FourthTxEventListener();
		}

		@Bean
		Client client(ApplicationEventPublisher publisher) {
			return new Client(publisher);
		}
	}

	@RequiredArgsConstructor
	static class Client {

		private final ApplicationEventPublisher publisher;

		@Transactional
		public void method() {
			publisher.publishEvent(new DomainEvent());
		}
	}

	static class DomainEvent {}

	static class NonTxEventListener {

		@Getter int invoked = 0;

		@EventListener
		void on(DomainEvent event) {
			invoked++;
		}
	}

	static class FirstTxEventListener {

		@Getter int invoked = 0;

		@TransactionalEventListener
		public void on(DomainEvent event) {
			invoked++;
		}
	}

	static class SecondTxEventListener {

		@Getter int invoked = 0;

		@TransactionalEventListener
		public void on(DomainEvent event) {
			invoked++;
			throw new IllegalStateException();
		}
	}

	static class ThirdTxEventListener {

		@Getter int invoked = 0;

		@TransactionalEventListener
		public void on(DomainEvent event) {
			invoked++;
		}
	}

	static class FourthTxEventListener {

		@Getter int invoked = 0;

		@Async
		@TransactionalEventListener
		public void on(DomainEvent event) throws InterruptedException {

			invoked++;

			Thread.sleep(100);

			throw new RuntimeException("Error!");
		}
	}
}
