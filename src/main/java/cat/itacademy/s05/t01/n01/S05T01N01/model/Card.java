package cat.itacademy.s05.t01.n01.S05T01N01.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private String suit;
    private String rank;
}
