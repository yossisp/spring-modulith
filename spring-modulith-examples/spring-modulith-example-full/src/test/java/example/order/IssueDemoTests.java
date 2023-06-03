/*
 * Copyright 2022-2023 the original author or authors.
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
package example.order;


import example.inventory.InventoryUpdated;
import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

/**
 * A show case for how the Spring Modulith application event publication registry keeps track of incomplete publications
 * for failing transactional event listeners
 *
 * @author Oliver Drotbohm
 */
@ApplicationModuleTest
@RequiredArgsConstructor
class IssueDemoTests {

    @Test
    void inventoryToFulfillOrderIsX(Scenario scenario) {

        var order = new Order();

        /*
         * The test fails (because of H2?)
         */
        scenario.publish(new OrderCompleted(order.getId()))
                .andWaitForEventOfType(InventoryUpdated.class)
                .toArrive();
    }
}
