package com.spring.example.services;

import com.spring.example.model.Product;
import com.spring.example.respository.ProductRepository;
import custom.anotations.Autowired;
import custom.anotations.Component;

import java.util.List;

@Component
public class ProductService
{

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsWithFinalPrice()
    {
        List<Product> products =  productRepository.getAllProducts();

        for(Product product : products)
        {
            double newPrice  = product.getPrice() - (product.getPrice() * product.getDiscount()/100);
            product.setPrice(newPrice);
            System.out.println("Prrice of "+product.getName()+" after "+product.getDiscount()+
                    "% discount is"+product.getPrice());
        }

        return products;
    }

    public void addProduct(Product pProduct)
    {
        productRepository.addProduct(pProduct);
    }
}
