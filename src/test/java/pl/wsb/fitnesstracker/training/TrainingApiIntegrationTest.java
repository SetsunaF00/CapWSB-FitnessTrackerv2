package pl.wsb.fitnesstracker.training;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.IntegrationTest;
import pl.wsb.fitnesstracker.IntegrationTestBase;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.user.api.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class TrainingApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    private static User generateClient() {
        return new User(randomUUID().toString(), randomUUID().toString(), LocalDate.now(), randomUUID().toString());
    }

    private static Training generateTraining(User user) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Training(user, sdf.parse("2024-01-19 08:00:00"), sdf.parse("2024-01-19 09:30:00"),
                ActivityType.RUNNING, 10.5, 8.2);
    }

    private static Training generateTrainingWithActivityType(User user, ActivityType activityType) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Training(user, sdf.parse("2024-01-19 08:00:00"), sdf.parse("2024-01-19 09:30:00"),
                activityType, 0, 0);
    }

    private static Training generateTrainingWithDetails(User user, String startTime, String endTime, ActivityType activityType, double distance, double averageSpeed) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Training(user, sdf.parse(startTime), sdf.parse(endTime), activityType, distance, averageSpeed);
    }

    @Test
    void shouldReturnAllTrainings_whenGettingAllTrainings() throws Exception {
        User user1 = existingUser(generateClient());
        Training training1 = persistTraining(generateTraining(user1));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        mockMvc.perform(get("/v1/trainings").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(user1.getId()))
                .andExpect(jsonPath("$[0].startTime").value(sdf.format(training1.getStartTime())));
    }

    @Test
    void shouldReturnAllTrainingsForDedicatedUser_whenGettingAllTrainingsForDedicatedUser() throws Exception {
        User user1 = existingUser(generateClient());
        Training training1 = persistTraining(generateTraining(user1));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        mockMvc.perform(get("/v1/trainings/{userId}", user1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(user1.getId()))
                .andExpect(jsonPath("$[0].startTime").value(sdf.format(training1.getStartTime())));
    }

    @Test
    void shouldReturnAllFinishedTrainingsAfterTime_whenGettingAllFinishedTrainingsAfterTime() throws Exception {
        User user1 = existingUser(generateClient());
        Training training1 = persistTraining(generateTrainingWithDetails(user1, "2024-05-19 19:00:00", "2024-05-19 20:30:00", ActivityType.RUNNING, 14, 11.5));
        persistTraining(generateTrainingWithDetails(user1, "2024-05-17 19:00:00", "2024-05-17 20:30:00", ActivityType.RUNNING, 14, 11.5));

        mockMvc.perform(get("/v1/trainings/finished/{afterTime}", "2024-05-18").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(user1.getId()))
                .andExpect(jsonPath("$[0].activityType").value("RUNNING"));
    }

    @Test
    void getAllTrainingByActivityType_whenGettingAllTrainingByActivityType() throws Exception {
        User user1 = existingUser(generateClient());
        persistTraining(generateTrainingWithActivityType(user1, ActivityType.RUNNING));
        persistTraining(generateTrainingWithActivityType(user1, ActivityType.TENNIS));
        persistTraining(generateTrainingWithActivityType(user1, ActivityType.TENNIS));

        mockMvc.perform(get("/v1/trainings/activityType").param("activityType", "TENNIS").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activityType").value("TENNIS"))
                .andExpect(jsonPath("$[1].activityType").value("TENNIS"));
    }

    @Test
    void shouldPersistTraining_whenCreatingNewTraining() throws Exception {
        User user1 = existingUser(generateClient());
        String requestBody = """
                {
                    "userId": "%s",
                    "startTime": "2024-04-01T11:00:00",
                    "endTime": "2024-04-01T12:00:00",
                    "activityType": "RUNNING",
                    "distance": 5.0,
                    "averageSpeed": 10.0
                }
                """.formatted(user1.getId());

        mockMvc.perform(post("/v1/trainings").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(user1.getId()))
                .andExpect(jsonPath("$.distance").value(5.0))
                .andExpect(jsonPath("$.averageSpeed").value(10.0));
    }

    @Test
    void shouldUpdateTraining_whenUpdatingTraining() throws Exception {
        User user1 = existingUser(generateClient());
        Training training1 = persistTraining(generateTrainingWithActivityType(user1, ActivityType.RUNNING));

        String requestBody = """
                {
                  "userId": "%s",
                  "startTime": "2024-06-01T09:00:00",
                  "endTime": "2024-06-01T10:30:00",
                  "activityType": "TENNIS",
                  "distance": 2.0,
                  "averageSpeed": 4.0
                }
                """.formatted(user1.getId());

        mockMvc.perform(put("/v1/trainings/{trainingId}", training1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityType").value("TENNIS"))
                .andExpect(jsonPath("$.distance").value(2.0))
                .andExpect(jsonPath("$.averageSpeed").value(4.0));
    }
}
