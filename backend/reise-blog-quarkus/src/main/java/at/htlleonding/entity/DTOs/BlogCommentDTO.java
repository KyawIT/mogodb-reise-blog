package at.htlleonding.entity.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class BlogCommentDTO{
    public String id;
    public String authorUsername;
    public Date creationDate;
    public String content;
}
