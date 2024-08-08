package bot.telegram.umelon.ulingua.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity(name = "words")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lang_id", nullable = false)
    private Language language;

    @Column(name = "original", nullable = false, length = 64)
    private String original;

    @Column(name = "part_of_speech", length = 50)
    private String partOfSpeech;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "tense", nullable = false, length = 50)
    private String tense;

    @Column(name = "hieroglyphs", length = 16)
    private String hieroglyphs;

    @Column(name = "transliteration", length = 128)
    private String transliteration;

    @ManyToMany(mappedBy = "words")
    private Set<User> users;

}
