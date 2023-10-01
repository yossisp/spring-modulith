package example.order.internal;

import org.springframework.stereotype.Component;

@Component
public class OrderInternalB {
    private final OrderInternalA orderInternalA;

    public OrderInternalB(OrderInternalA orderInternalA) {
        this.orderInternalA = orderInternalA;
    }

    void doSomething() {
        this.orderInternalA.doSomething();
    }
}
