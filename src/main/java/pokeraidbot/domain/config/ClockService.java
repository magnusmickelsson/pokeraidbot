package pokeraidbot.domain.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ClockService {
    private LocalTime mockTime = null;

    public ClockService() {
    }

    public void setMockTime(LocalTime mockTime) {
        this.mockTime = mockTime;
    }

    public LocalTime getCurrentTime() {
        return mockTime == null ? LocalTime.now() : mockTime;
    }

    public LocalDateTime getCurrentDateTime() {
        return mockTime == null ? LocalDateTime.now() : LocalDateTime.of(LocalDate.now(), mockTime);
    }
}
