package pokeraidbot.infrastructure.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

@Converter(autoApply = true)
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime localTime) {
        return (localTime == null ? null : Time.valueOf(localTime));
    }

    @SuppressWarnings("deprecation")
    @Override
    public LocalTime convertToEntityAttribute(Time time) {
        return (time == null ? null : LocalTime.of(time.getHours(), time.getMinutes()));
    }
}