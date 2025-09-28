package com.examble.drone_api.model;

import com.examble.drone_api.model.type.Priority;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private int destinationX, destinationY;
    private int weight;
    @Builder.Default
    private Priority priority = Priority.LOW;
    private boolean delivered = false;

    public Order(Long id,
                 int destinationX,
                 int destinationY,
                 int weight,
                 Priority priority) {
        this.id = id;
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.weight = weight;
        this.priority = priority;
    }
}
