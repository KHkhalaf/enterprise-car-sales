package com.anilaltunkan.security.controller;

import com.anilaltunkan.security.dto.LoginRequest;
import com.anilaltunkan.security.dto.LoginResponse;
import com.anilaltunkan.security.Interface.UserService;
import com.anilaltunkan.security.model.Authority;
import com.anilaltunkan.security.model.ConfirmationToken;
import com.anilaltunkan.security.model.User;
import com.anilaltunkan.security.repository.AuthorityRepository;
import com.anilaltunkan.security.repository.ConfirmationTokenRepository;
import com.anilaltunkan.security.service.CustomUserDetails;
import com.anilaltunkan.security.service.EmailSenderService;
import com.anilaltunkan.security.service.InjectSqlCommand;
import com.anilaltunkan.security.util.SecurityCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @RequestMapping(value = "/register")
    public ModelAndView showRegisterPage(User user) {
        ModelAndView mav = new ModelAndView("UserViews/register");
        mav.addObject("user",user);
        return mav;
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ModelAndView Register(@ModelAttribute("user") User user) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        user.setHashedPassword(new BCryptPasswordEncoder().encode(user.getHashedPassword()));
        User _user = userService.getByEmail(user.getEmail());
        if(_user != null)
        {
            modelAndView.addObject("message","This email already exists!");
            modelAndView.setViewName("Shared/error");
        }
        else
        {
            userService.save(user);
            long userId = userService.getByEmail(user.getEmail()).getId();
            InjectSqlCommand injectSqlCommand = new InjectSqlCommand();
            injectSqlCommand.addUserToAuthority(userId, user.getType());

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            EmailSenderService emailSenderService = new EmailSenderService();
            emailSenderService.sendEmail(confirmationToken.getConfirmationToken(), user.getEmail());

            confirmationTokenRepository.save(confirmationToken);

            modelAndView.addObject("emailId", user.getEmail());

            modelAndView.setViewName("Shared/successfulRegistration");
        }
        return modelAndView;
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userService.getByEmail(token.getUser().getEmail());
            user.setIsVerified(true);
            userService.save(user);
            modelAndView.setViewName("Shared/accountVerified");
        }
        else
        {
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("Shared/error");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/loginPage")
    public ModelAndView showLoginPage(LoginRequest loginRequest) {
        ModelAndView mav = new ModelAndView("UserViews/login");
        mav.addObject("loginRequest",loginRequest);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest
            ) throws IOException {

        if(!userService.getByEmail(loginRequest.getEmail()).getIsVerified())
            return null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);

        return userService.login(loginRequest.getEmail(), decryptedAccessToken, decryptedRefreshToken);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                      @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);
        return userService.refresh(decryptedAccessToken, decryptedRefreshToken);
    }

    @RequestMapping(value = "/logout")
    public void logout(){
        // Do something .....
    }
    @RequestMapping(value = "/accountNotVerified")
    public ModelAndView accountNotVerified(){

        ModelAndView mav = new ModelAndView("Shared/accountNotVerified");
        return mav;
    }
}
