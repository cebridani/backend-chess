package io.github.cebridani.chesswebapi.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Move {
    
	@JsonProperty("after")
    private String after;

    @JsonProperty("before")
    private String before;

    @JsonProperty("color")
    private String color;

    @JsonProperty("flags")
    private String flags;

    @JsonProperty("from")
    private String from;

    @JsonProperty("lan")
    private String lan;

    @JsonProperty("piece")
    private String piece;

    @JsonProperty("san")
    private String san;

    @JsonProperty("to")
    private String to;

    @JsonProperty("promotion")
    private String promotion;
    
    
    @JsonProperty("captured")
    private String captured;
    
    public Move (String after) {
    	this.after = after;
    }
	
}
