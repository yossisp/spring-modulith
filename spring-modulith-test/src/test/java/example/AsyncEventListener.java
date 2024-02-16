/*
 * Copyright 2023-2024 the original author or authors.
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
package example;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncEventListener {

	private final ApplicationEventPublisher publisher;

	@Async
	@EventListener
	void on(FirstEvent event) {
		publisher.publishEvent(new SecondEvent());
	}

	@Async
	@EventListener
	void on(SecondEvent event) {
		publisher.publishEvent(new ThirdEvent());
	}

	public static record FirstEvent() {}

	static record SecondEvent() {}

	public static record ThirdEvent() {}
}
