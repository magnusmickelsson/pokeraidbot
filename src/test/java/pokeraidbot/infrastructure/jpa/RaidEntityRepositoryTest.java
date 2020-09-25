package pokeraidbot.infrastructure.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pokeraidbot.infrastructure.jpa.raid.RaidEntity;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntitySignUp;
import pokeraidbot.infrastructure.jpa.raid.RaidGroup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static pokeraidbot.Utils.printTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RaidEntityRepositoryTest {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(RaidEntityRepositoryTest.class);
    @Autowired
    RaidEntityRepository entityRepository;
    private static final ExecutorService executorService =
            new ThreadPoolExecutor(3, 3, 20, TimeUnit.SECONDS, new LinkedTransferQueue<>());

    @Before
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setUp() throws Exception {
        entityRepository.deleteAllInBatch();
    }

    @After
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tearDown() throws Exception {
        entityRepository.delete(entityRepository.findOne("id1"));
    }

    @Test
    public void createWithGroup() throws Exception {
        final String id = "id1";
        RaidEntity raidEntity = new RaidEntity(id, "Mupp",
                format(LocalDateTime.now().plusMinutes(20)),
                "Thegym", "Theuser", "Theregion", false);
        final RaidGroup group = new RaidGroup("testserver", "channel", "abc1", "abc2", "testId",
                format(LocalDateTime.now().plusMinutes(10)));
        assertThat(raidEntity.addGroup(group), is(true));
        raidEntity = entityRepository.save(raidEntity);
        final RaidEntity loaded = entityRepository.findOne(id);
        assertThat(loaded, is(raidEntity));
        final Set<RaidGroup> groups = loaded.getGroupsAsSet();
        assertThat(groups.size(), is(1));
        assertThat(groups.iterator().next(), is(group));

        final List<RaidGroup> groupsForServer = entityRepository.findGroupsForServer("testserver");
        assertThat(groupsForServer.size(), is(1));
        assertThat(groupsForServer.iterator().next(), is(group));
    }

    private LocalDateTime format(LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.MILLIS);
    }

    @Test
    public void createAndGet() throws Exception {
        final String id = "id1";
        final RaidEntity raidEntity = entityRepository.save(new RaidEntity(id, "Mupp",
                format(LocalDateTime.now().plusMinutes(20)),
                "Thegym", "Theuser", "Theregion", false));
        assertNotNull(raidEntity);
        assertThat(entityRepository.findOne(id), is(raidEntity));
    }

    @Test
    public void createEx() {
        final String id = "id1";
        final RaidEntity raidEntity = entityRepository.save(new RaidEntity(id, "Mupp",
                format(LocalDateTime.now().plusMinutes(20)),
                "Thegym", "Theuser", "Theregion", true));
        RaidEntity loadedEntity = entityRepository.findOne(id);
        assertThat(loadedEntity.isExRaid(), is(true));
        assertThat(loadedEntity, is(raidEntity));
    }

    @Test
    public void createWithSignupsTryToCleanUpWithoutConcurrentModificationException() throws Exception {
        final String id = "id1";
        final RaidEntity raidEntity = entityRepository.save(new RaidEntity(id, "Mupp",
                format(LocalDateTime.now().plusMinutes(20)),
                "Thegym", "Theuser", "Theregion", false));
        final LocalTime now = LocalTime.now();

        // Create signups with 100 ms interval, during ~3 seconds
        Callable<Integer> creatingSignUpsTask = () -> {
            int numberOfSignUpsCreated = 0;
            final Random random = new Random();
            for (int i = 0; i < 30; i++) {
                Thread.sleep(100);
                createSignUp(id, 1, now, random, i);
                numberOfSignUpsCreated++;
            }
            return numberOfSignUpsCreated;
        };

        Callable<Integer> creatingSignUpsTask2 = () -> {
            int numberOfSignUpsCreated = 0;
            final Random random = new Random();
            for (int i = 0; i < 30; i++) {
                Thread.sleep(100);
                createSignUp(id, 2, now, random, i);
                numberOfSignUpsCreated++;
            }
            return numberOfSignUpsCreated;
        };

        // After 2 seconds, try and remove all signups for a certain time
        Callable<Integer> cleanUpSignUpsTask = () -> {
            Thread.sleep(2000);
            int numberDeleted = deleteSomeSignUps(id);
            return numberDeleted;
        };

        final List<Future<Integer>> futures =
                executorService.invokeAll(Arrays.asList(cleanUpSignUpsTask, creatingSignUpsTask, creatingSignUpsTask2));
        final RaidEntity theEntity = entityRepository.findOne(id);
        assertThat(theEntity == null, is(false));
        final Iterator<Future<Integer>> futureIterator = futures.iterator();
        final Future<Integer> cleanedUpFuture = futureIterator.next();
        final Future<Integer> signupFuture1 = futureIterator.next();
        final Future<Integer> signupFuture2 = futureIterator.next();
        final Integer signedUpInThread1 = signupFuture1.get();
        final Integer signedUpInThread2 = signupFuture2.get();
        final Integer cleanedUp = cleanedUpFuture.get();
        int signups = signedUpInThread1 + signedUpInThread2 - cleanedUp;
        assertThat(signedUpInThread1, is(30));
        assertThat(signedUpInThread2, is(30));
        assertThat(cleanedUp > 0, is(true));
        assertThat(theEntity.getSignUpsAsSet().size(), is(signups));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteSomeSignUps(String id) {
        int numberDeleted = 0;
        final RaidEntity theEntity = entityRepository.findOne(id);
        for (RaidEntitySignUp signUp : theEntity.getSignUpsAsSet()) {
            if (signUp.getNumberOfPeople().equals(3)) {
                theEntity.removeSignUp(signUp);
                LOGGER.warn("Removed: " + signUp);
                numberDeleted++;
            }
        }
        entityRepository.save(theEntity);
        return numberDeleted;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RaidEntitySignUp createSignUp(String id, Integer thread, LocalTime now, Random random, int i) {
        RaidEntitySignUp signUp = new RaidEntitySignUp("Mupp" + thread + "_" + i,
                random.nextInt(4) + 1, printTime(now));
        RaidEntity entity = entityRepository.findOne(id);
        entity.addSignUp(signUp);
        entityRepository.save(entity);
        LOGGER.warn("Created: " + signUp);
        return signUp;
    }
}
