package al.photoBackup.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media")
public class MediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @JsonIgnore
    @JoinColumn(name = "userid")
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserEntity userEntity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "thumbnail_name", nullable = false)
    private String thumbnailName;

    @Column(name = "thumbnail_mid_name", nullable = false)
    private String thumbnailMidName;

}
