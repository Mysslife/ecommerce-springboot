package com.ecommerce.admin.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

//Đây là file nhẽ ra phải tên là AuthController vì bao gồm các method register và login

@Controller
public class LoginController {
    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }

    @RequestMapping("/index")
    public String home(Model model) {
        model.addAttribute("title", "Homepage");
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        model.addAttribute("adminDto", new AdminDto()); // vào trang /register -> sau đó tạo ra đối tượng (model) adminDto với value các thuộc tính = undefinded. Sau đó, khi submit, sẽ nhận được value theo các trường tương ứng khi user nhập vào và submit
        model.addAttribute("title", "Register");
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    // Register:
    @PostMapping("/register-new") // như ở trên, khi vào trang /register -> tạo ra đối tượng (model) adminDto rỗng, sau đó user nhập value, submit thì sẽ nhận được value các trường thuộc tính tương ứng
    // @Valid để validation
    // @ModelAttribute để lấy được model tương ứng (ở đây là adminDto) khi vào trang /register (ở method get /register). Và vì là đối tượng AdminDto nên kiểu dữ liệu cũng phải là AdminDto
    // Model model rất hay: model.addAttribute(key, value); => tại các page html, chỉ cần gọi đến: "${key}" là sẽ tự động có value tương ứng với các key đã được set value trước đó!!
    // HttpSession session để truyền value từ trang này sang trang khác khi thực hiện redirect
    public String addNewAdmin(@Valid @ModelAttribute("adminDto")AdminDto adminDto,
                              BindingResult result, // binding error text message với field password, username, .... trong class AdminDto (@Size(min = 3, max = 10, message = "Invalid last name! (3-10 characters)")
                              Model model) {
       try {
//           session.removeAttribute("message");
           if(result.hasErrors()) {
               model.addAttribute("adminDto", adminDto);
               result.toString();
               return "register";
           }

           String username = adminDto.getUsername();
           Admin admin = adminServiceImpl.findByUsername(username);
           if(admin != null) {
               model.addAttribute("adminDto", adminDto);
               model.addAttribute("emailError", "Your email has been registered!");
//               session.setAttribute("message", "Your email has been registered!");
               return "register";
           }

           if(adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
               adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
               adminServiceImpl.save(adminDto);
//               session.setAttribute("message", "Register successfully!");
               model.addAttribute("adminDto", adminDto);
               model.addAttribute("success", "Register successfully!");
           } else {
               model.addAttribute("adminDto", adminDto);
               model.addAttribute("passwordError", "Passwords are not the same!");
//               session.setAttribute("message", "Passwords are not the same!");
               return "register";
           }
       } catch (Exception e) {
           e.printStackTrace();
           model.addAttribute("errors", "The server has been wrong!");
       }
        return "register";
    }

}
