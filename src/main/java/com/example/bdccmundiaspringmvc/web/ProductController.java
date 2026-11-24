package com.example.bdccmundiaspringmvc.web;

import com.example.bdccmundiaspringmvc.entities.Product;
import com.example.bdccmundiaspringmvc.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository ;
    @GetMapping("/user/index")
    public String index(Model model){
    List<Product> products = productRepository.findAll();
    model.addAttribute("productList" , products);
        return "products";
    }

    @GetMapping("/")
    public String Home(){
        return "redirect:/user/index";
    }

    @GetMapping("/admin/newProduct")
    public String NewProduct(Model model){
        model.addAttribute("product", new Product());
        return "new-product";
    }

//    @Valid une fois tu va stocker les donner en objet de type product fais la validation !!
    @PostMapping("/admin/saveproduct")
    public String saveProduct(@Valid Product product , BindingResult bindingResult , Model model){
        if (bindingResult.hasErrors()) return "new-product";
        productRepository.save(product);
        return "redirect:/admin/index";
    }

    @GetMapping("/admin/delete")
    public String delete(Long id){
        productRepository.deleteById(id);
        return "redirect:/user/index";
    }

    @GetMapping("/notAuthorized")
    public String notAuthorized(){
        return "/notAuthorized";
    }
}
