package pokeraidbot.infrastructure.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime localTime) {
        return (localTime == null ? null : Timestamp.valueOf(localTime));
    }

    @SuppressWarnings("deprecation")
    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp time) {
        return (time == null ? null : time.toLocalDateTime());
    }
}