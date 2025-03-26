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
    public String id;            // z. B. als Hex-String
    public String title;
    public String description;
    public int impressionCount;
    public boolean commentsAllowed;
    public Date creationDate;
    public List<String> content;  // Array where index 0=text, 1=base64 links, 2=base64 images
    public String categoryName;  // falls du die Kategorie nur als String ausgeben möchtest
    public List<BlogCommentDTO> comments;  // entweder max. 3 oder alle
}
