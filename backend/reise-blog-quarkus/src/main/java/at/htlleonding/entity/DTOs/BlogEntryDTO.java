package at.htlleonding.entity.DTOs;

import java.util.List;

public class BlogEntryDTO {
    public String id;            // z. B. als Hex-String
    public String title;
    public String description;
    public int impressionCount;
    public String categoryName;  // falls du die Kategorie nur als String ausgeben möchtest
    public List<BlogCommentDTO> comments;  // entweder max. 3 oder alle
}
