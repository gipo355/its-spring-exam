package com.example.itsspringexam;

import java.util.ArrayList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class Products {

  @GetMapping
  public ArrayList<Article> getAllProducts() {
    ArrayList<Article> arrayList = DatabaseConnection.displayArticles();

    return arrayList;
  }
}
