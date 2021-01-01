package com.anilaltunkan.security.Interface;

import com.anilaltunkan.security.Exceptions.ResourceNotFoundException;
import com.anilaltunkan.security.dto.LoginRequest;
import com.anilaltunkan.security.dto.LoginResponse;
import com.anilaltunkan.security.dto.UserSummary;
import com.anilaltunkan.security.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {

    ResponseEntity<LoginResponse> login(String email, String accessToken, String refreshToken);

    ResponseEntity<LoginResponse> refresh(String accessToken, String refreshToken);

    UserSummary getUserProfile();

    public List<User> listAll();

    public void save(User user);

    public User getByEmail(String email);

    public User get(long id) throws ResourceNotFoundException;

    public void delete(long id);

}
