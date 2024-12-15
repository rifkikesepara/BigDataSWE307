package com.example.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class DataGenerator implements Serializable {
    public Integer user_id;
    public long date_time;
    public String description_type;
    public float payment;

    public DataGenerator(){}

    public DataGenerator(Integer userid, long datetime, String description, float payment) {
        this.user_id = userid;
        this.date_time = datetime;
        this.description_type = description;
        this.payment = payment;
    }

    public static DataGenerator fromString(String dataGeneratorString) {
        // Regular expression to match the string representation
        String regex = "DataGenerator\\(user_id=(\\d+), date_time=(\\d+), description_type=([^,]+), electronics, payment=([\\d.]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dataGeneratorString);

        if (matcher.find()) {
            Integer userId = Integer.parseInt(matcher.group(1));
            long dateTime = Long.parseLong(matcher.group(2));
            String descriptionType = matcher.group(3);
            float payment = Float.parseFloat(matcher.group(4));

            // Create a new DataGenerator object
            return new DataGenerator(userId, dateTime, descriptionType, payment);
        } else {
            throw new IllegalArgumentException("String does not match expected format");
        }
    }

    @Override
    public String toString() {
        return user_id+","+date_time+","+description_type+","+payment;
    }
}

