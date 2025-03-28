package at.htlleonding.entity.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BlogUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}