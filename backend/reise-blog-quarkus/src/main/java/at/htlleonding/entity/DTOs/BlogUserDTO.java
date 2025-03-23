package at.htlleonding.entity.DTOs;

import lombok.Data;

@Data
public class BlogUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}