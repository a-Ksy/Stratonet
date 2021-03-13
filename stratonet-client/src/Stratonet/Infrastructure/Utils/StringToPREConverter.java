package Stratonet.Infrastructure.Utils;

import Stratonet.Core.Models.PRE;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class StringToPREConverter {
    public static PRE Convert(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PRE pre = objectMapper.readValue(content, PRE.class);
            return pre;
        } catch (IOException ex) {
            return null;
        }
    }
}
