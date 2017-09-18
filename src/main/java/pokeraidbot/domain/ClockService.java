package pokeraidbot.domain;

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
}
