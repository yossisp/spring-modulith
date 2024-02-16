/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.modulith.moments;

import java.time.YearMonth;
import java.util.Objects;

import org.jmolecules.event.types.DomainEvent;
import org.springframework.util.Assert;

/**
 * A {@link DomainEvent} published on the last day of the month.
 *
 * @author Oliver Drotbohm
 */
public class MonthHasPassed implements DomainEvent {

	private final YearMonth month;

	/**
	 * Creates a new {@link MonthHasPassed} for the given {@link YearMonth}.
	 *
	 * @param month must not be {@literal null}.
	 */
	private MonthHasPassed(YearMonth month) {

		Assert.notNull(month, "YearMonth must not be null!");

		this.month = month;
	}

	/**
	 * Creates a new {@link MonthHasPassed} for the given {@link YearMonth}.
	 *
	 * @param month must not be {@literal null}.
	 */
	public static MonthHasPassed of(YearMonth month) {
		return new MonthHasPassed(month);
	}

	/**
	 * The month that has just passed.
	 *
	 * @return will never be {@literal null}.
	 */
	public YearMonth getMonth() {
		return month;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof MonthHasPassed that)) {
			return false;
		}

		return Objects.equals(month, that.month);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(month);
	}
}
