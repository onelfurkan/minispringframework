package com.spring.example.main;

import custom.anotations.Autowired;
import custom.anotations.Component;
import com.spring.example.appcontext.AppConfig;
import com.spring.example.appcontext.ApplicationContext;
import com.spring.example.model.Product;
import com.spring.example.services.ProductService;

@Component
public class Application
{
    @Autowired
    static ProductService productService;

    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);

        productService.addProduct(new Product("Laptop",10,new Double(100)));
        productService.addProduct(new Product("Bag",10,new Double(200)));
        productService.addProduct(new Product("mobile phone",10,new Double(300)));

        productService.getProductsWithFinalPrice();
    }
}
