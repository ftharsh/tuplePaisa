package org.harsh.tuple.paisa.controller;

import org.harsh.tuple.paisa.exception.UserNotFoundException;
import org.harsh.tuple.paisa.model.User;
import org.harsh.tuple.paisa.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private  UserService userService;

    @InjectMocks
    private UserController userController;

    private static User registeredUser;
    private static User loginUser;

    @BeforeAll
    static void setupAll(){
        registeredUser = User.builder()
                .username("harsh")
                .password("password")
                .email("harsh@gmail.com")
                .build();

        loginUser = User.builder()
                .username("harsh")
                .password("dontknow")
                .build();
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        when(userService.registerUser(registeredUser)).thenReturn(registeredUser);

        // Act
        ResponseEntity<?> response = userController.registerUser(registeredUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredUser, response.getBody());
        verify(userService, times(1)).registerUser(registeredUser);
    }

    @Test
    void testLoginUser() {

        String token = "fakeToken";

        when(userService.loginUser("harsh","dontknow")).thenReturn(token);

        // Act
        ResponseEntity<?> response = userController.loginUser(loginUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("token", token);
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).loginUser("harsh", "dontknow");
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(registeredUser.getId());


        ResponseEntity<Void> response = userController.deleteUser(registeredUser.getId());

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(registeredUser.getId());
    }

   @Test
    void testLoginUserNotFound() {
        when(userService.loginUser("harsh","dontknow")).thenThrow(new UserNotFoundException(registeredUser.getUsername()));

          UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
              userController.loginUser(loginUser);
          });
        assertNotNull(exception);
        assertEquals("User not found", exception.getErrorResponse().getMessage());
       verify(userService, times(1)).loginUser("harsh", "dontknow");
   }
}
