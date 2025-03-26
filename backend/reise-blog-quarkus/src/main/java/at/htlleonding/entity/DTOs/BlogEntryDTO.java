package at.htlleonding.entity.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class BlogEntryDTO {
    public String id;
    public String title;
    public String description;
    public int impressionCount;
    public boolean commentsAllowed;
    public Date creationDate;
    public List<String> content;
    public String categoryName;
    public List<BlogCommentDTO> comments;
}
