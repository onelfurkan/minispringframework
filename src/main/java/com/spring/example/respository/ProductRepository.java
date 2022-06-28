package com.spring.example.respository;

import com.spring.example.model.Product;
import custom.anotations.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductRepository
{
    private static final List<Product> products =  new ArrayList<>();

    public void addProduct(Product pProduct)
    {
        products.add(pProduct);
    }

    public List<Product> getAllProducts()
    {
        return products;
    }

}
