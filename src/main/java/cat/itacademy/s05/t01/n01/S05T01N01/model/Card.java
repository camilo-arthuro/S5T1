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

    public int getValue(){
        int value = 0;

        if("JQK".contains(rank)) {
            value = 10;
        } else if ("A".contains(rank)){
            value = 11;
        } else {
            value = Integer.parseInt(rank);
        }

        return value;
    }
}
