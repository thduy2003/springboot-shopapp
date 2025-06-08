package com.example.shopapp.components;

import com.example.shopapp.models.Category;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(id = "groupA", topics = {"get-all-categories", "insert-a-category"})
public class MyKafkaListener {
    @KafkaListener(topics = "insert-a-category")
    public void listenerCategory(Category category) {
        System.out.println("Received Category: " + category);
    }

    @KafkaListener(topics = "get-all-categories")
    public void listenListOfCategory(List<Category> categories) {
        System.out.println("Received list of Category: " + categories);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Category category) {
        System.out.println("Received unknown: " + category);
    }
}
