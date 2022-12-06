package com.ecommerce.admin.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("adminDto", new AdminDto()); // vào trang /register -> sau đó tạo ra đối tượng (model) adminDto với value các thuộc tính = undefinded. Sau đó, khi submit, sẽ nhận được value theo các trường tương ứng khi user nhập vào và submit
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "forgot-password";
    }

    @PostMapping("/register-new") // như ở trên, khi vào trang /register -> tạo ra đối tượng (model) adminDto rỗng, sau đó user nhập value, submit thì sẽ nhận được value các trường thuộc tính tương ứng
    // @Valid để validation
    // @ModelAttribute để lấy được model tương ứng (ở đây là adminDto) khi vào trang /register (ở method get /register). Và vì là đối tượng AdminDto nên kiểu dữ liệu cũng phải là AdminDto
    // RedirectAttributes để truyền value từ trang này sang trang khác khi thực hiện redirect
    public String addNewAdmin(@Valid @ModelAttribute("adminDto")AdminDto adminDto,
                              BindingResult result, // binding error text message với field password, username, .... trong class AdminDto (@Size(min = 3, max = 10, message = "Invalid last name! (3-10 characters)")
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
       try {
           if(result.hasErrors()) {
               model.addAttribute("adminDto", adminDto);
               result.toString();
               System.out.println("Error");
               return "redirect:/register";
           }

           String username = adminDto.getUsername();
           Admin admin = adminServiceImpl.findByUsername(username);
           if(admin != null) {
               model.addAttribute("adminDto", adminDto);
               redirectAttributes.addFlashAttribute("message", "Your email has been registered!");
               System.out.println("Admin not null");
               return "redirect:/register";
           }

           if(adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
               adminServiceImpl.save(adminDto);
               System.out.println("success");
               model.addAttribute("adminDto", adminDto);
               redirectAttributes.addFlashAttribute("message", "Register successfully!");
           } else {
               model.addAttribute("adminDto", adminDto);
               redirectAttributes.addFlashAttribute("message", "Passwords are not the same!");
               System.out.println("password not same");
               session.setAttribute("message", "Passwords are not the same!");
               return "redirect:/register";

           }
       } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Can't register because of the server error!");
       }
        return "redirect:/register";
    }
}
