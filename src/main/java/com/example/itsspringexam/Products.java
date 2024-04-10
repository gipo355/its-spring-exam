package com.example.itsspringexam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class Products {

    @GetMapping
    public ArrayList<Article> getAllProducts() {
        ArrayList<Article> arrayList = DatabaseConnection.displayArticles();

        return arrayList;
    }

}
