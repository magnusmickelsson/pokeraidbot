package pokeraidbot.domain;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ClockService {
    private LocalTime mockTime = null;

    public void setMockTime(LocalTime mockTime) {
        this.mockTime = mockTime;
    }

    public LocalTime getCurrentTime() {
        return mockTime == null ? LocalTime.now() : mockTime;
    }
}
